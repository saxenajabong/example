package com.test.widget;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.WrapperListAdapter;

import com.test.adapter.DragListAdapter;

public class DragListView extends android.widget.ListView {

    private boolean mDragMode;
    private WindowManager mWm;
    private int mStartPosition = INVALID_POSITION;
    private int mDragPointOffset; // Used to adjust drag view location
    private int mDragHandler = 0;
    private ImageView mDragView;
    private OnItemDragNDropListener mDragNDropListener;

    private void init() {
        mWm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
    }

    public DragListView(Context context) {
        super(context);
        init();
    }

    public DragListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setDragListAdapter(DragListAdapter adapter) {
        mDragHandler = adapter.getDragHandler();
        setAdapter(adapter);
    }

    public boolean isDrag(MotionEvent ev) {
        if (mDragMode) return true;
        if (mDragHandler == 0) return false;

        int x = (int) ev.getX();
        int y = (int) ev.getY();

        int startPosition = pointToPosition(x, y);

        if (startPosition == INVALID_POSITION) {
            return false;
        }

        int childPosition = startPosition - getFirstVisiblePosition();
        View parent = getChildAt(childPosition);
        View handler = parent.findViewById(mDragHandler);

        if (handler == null) return false;

        int top = parent.getTop() + handler.getTop();
        int bottom = top + handler.getHeight();
        int left = parent.getLeft() + handler.getLeft();
        int right = left + handler.getWidth();

        return left <= x && x <= right && top <= y && y <= bottom;
    }

    public boolean isDragging() {
        return mDragMode;
    }

    public View getDragView() {
        return mDragView;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        final int x = (int) ev.getX();
        final int y = (int) ev.getY();


        if (action == MotionEvent.ACTION_DOWN && isDrag(ev)) mDragMode = true;

        boolean isDraggingEnabled = true;
        if (!mDragMode || !isDraggingEnabled) return super.onTouchEvent(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mStartPosition = pointToPosition(x, y);

                if (mStartPosition != INVALID_POSITION) {
                    int childPosition = mStartPosition - getFirstVisiblePosition();
                    mDragPointOffset = y - getChildAt(childPosition).getTop();
                    mDragPointOffset -= ((int) ev.getRawY()) - y;

                    startDrag(childPosition, y);
                    drag(0, y);
                }

                break;
            case MotionEvent.ACTION_MOVE:
                drag(0, y);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
            default:
                mDragMode = false;


                if (mStartPosition != INVALID_POSITION) {
                    // check if the position is a header/footer
                    int actualPosition = pointToPosition(x, y);
                    if (actualPosition > (getCount() - getFooterViewsCount()) - 1)
                        actualPosition = INVALID_POSITION;

                    stopDrag(mStartPosition - getFirstVisiblePosition(), actualPosition);
                }
                break;
        }

        return true;
    }

    private void startDrag(int childIndex, int y) {
        View item = getChildAt(childIndex);

        if (item == null) return;

        long id = getItemIdAtPosition(mStartPosition);

        if (mDragNDropListener != null)
            mDragNDropListener.onItemDrag(this, item, mStartPosition, id);

        Adapter adapter = getAdapter();
        DragListAdapter dndAdapter;

        // if exists a footer/header we have our adapter wrapped
        if (adapter instanceof WrapperListAdapter) {
            dndAdapter = (DragListAdapter) ((WrapperListAdapter) adapter).getWrappedAdapter();
        } else {
            dndAdapter = (DragListAdapter) adapter;
        }

        dndAdapter.onItemDrag(this, item, mStartPosition, id);

        item.setDrawingCacheEnabled(true);

        // Create a copy of the drawing cache so that it does not get recycled
        // by the framework when the list tries to clean up memory
        Bitmap bitmap = Bitmap.createBitmap(item.getDrawingCache());

        WindowManager.LayoutParams mWindowParams = new WindowManager.LayoutParams();
        mWindowParams.gravity = Gravity.TOP;
        mWindowParams.x = 0;
        mWindowParams.y = y - mDragPointOffset;

        mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        mWindowParams.format = PixelFormat.TRANSLUCENT;
        mWindowParams.windowAnimations = 0;

        Context context = getContext();
        ImageView v = new ImageView(context);
        v.setImageBitmap(bitmap);

        mWm.addView(v, mWindowParams);
        mDragView = v;

        item.setVisibility(View.INVISIBLE);
        item.invalidate(); // We have not changed anything else.
    }

    private void stopDrag(int childIndex, int endPosition) {
        if (mDragView == null) return;
        View item = getChildAt(childIndex);
        if (endPosition != INVALID_POSITION) {
            long id = getItemIdAtPosition(mStartPosition);

            if (mDragNDropListener != null)
                mDragNDropListener.onItemDrop(this, item, mStartPosition, endPosition, id);

            Adapter adapter = getAdapter();
            DragListAdapter dndAdapter;

            // if exists a footer/header we have our adapter wrapped
            if (adapter instanceof WrapperListAdapter) {
                dndAdapter = (DragListAdapter) ((WrapperListAdapter) adapter).getWrappedAdapter();
            } else {
                dndAdapter = (DragListAdapter) adapter;
            }
            dndAdapter.onItemDrop(this, item, mStartPosition, endPosition, id);
        }
        mDragView.setVisibility(GONE);
        mWm.removeView(mDragView);
        mDragView.setImageDrawable(null);
        mDragView = null;
        item.setDrawingCacheEnabled(false);
        item.destroyDrawingCache();
        item.setVisibility(View.VISIBLE);
        mStartPosition = INVALID_POSITION;
        invalidateViews(); // We have changed the adapter data, so change everything
    }

    private void drag(int x, int y) {
        if (mDragView == null) return;
        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) mDragView.getLayoutParams();
        layoutParams.x = x;
        layoutParams.y = y - mDragPointOffset;
        mWm.updateViewLayout(mDragView, layoutParams);
    }

    public interface OnItemDragNDropListener {
        void onItemDrag(DragListView parent, View view, int position, long id);

        void onItemDrop(DragListView parent, View view, int startPosition, int endPosition, long id);
    }
}

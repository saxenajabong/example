package com.test.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.test.R;

public class ListingLayout extends RelativeLayout implements ViewTreeObserver.OnGlobalLayoutListener {

    private static final String TAG = "widget.ListingLayout";
    private int searchBoxPosition = R.integer.top;

    public ListingLayout(Context context) {
        super(context);
    }

    public ListingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ListingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ListingLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.SearchBox, 0, 0);
        try {
            searchBoxPosition = a.getInteger(R.styleable.SearchBox_position, 0);
        } finally {
            a.recycle();
        }
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
        View search = findViewById(R.id.search);
        View list = findViewById(android.R.id.list);
        if (list == null) {
            throw new Resources.NotFoundException("List resource not found exception.");
        }
        if (search == null) {
            Log.d(TAG, "Search box is not used.");
        } else {
            LayoutParams searchLayoutParams = (LayoutParams) search.getLayoutParams();
            LayoutParams listLayoutParams = (LayoutParams) list.getLayoutParams();
            switch (searchBoxPosition) {
                case R.integer.top:
                    searchLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    listLayoutParams.addRule(RelativeLayout.BELOW, R.id.search);
                    break;
                case R.integer.bottom:
                    searchLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    listLayoutParams.addRule(RelativeLayout.ABOVE, R.id.search);
                    break;
            }
            search.setLayoutParams(searchLayoutParams);
            list.setLayoutParams(listLayoutParams);
        }
    }
}

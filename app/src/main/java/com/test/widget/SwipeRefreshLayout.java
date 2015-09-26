package com.test.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Controls the swipe layout as per list view position.
 */
public class SwipeRefreshLayout extends android.support.v4.widget.SwipeRefreshLayout {

    public SwipeRefreshLayout(Context context) {
        super(context);
    }

    public SwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean canChildScrollUp() {
        ListView listViw = (ListView) findViewById(android.R.id.list);
        return listViw.getFirstVisiblePosition() != 0;
    }
}

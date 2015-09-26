package com.test.adapter;


import android.widget.ListAdapter;

import com.test.widget.DragListView;

public interface DragListAdapter extends ListAdapter, DragListView.OnItemDragNDropListener {
    public int getDragHandler();
}

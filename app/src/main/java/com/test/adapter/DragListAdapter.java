package com.test.adapter;


import android.widget.ListAdapter;

import com.test.widget.ListView;

public interface DragListAdapter extends ListAdapter, ListView.OnItemDragNDropListener {
    public int getDragHandler();
}

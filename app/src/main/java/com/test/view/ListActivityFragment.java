package com.test.view;

import android.app.ListFragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;

import com.test.adapter.ListAdaptor;
import com.test.R;
import com.test.volley.ListModel;
import com.test.volley.ListRequest;
import com.test.volley.VolleyTest;
import com.test.widget.DragListView;


public class ListActivityFragment extends ListFragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String URL = "http://example.com/json.txt";

    private SwipeRefreshLayout swipeView;
    private ListAdaptor adaptor;
    private TextView emptyText;
    private ArrayList<ListModel> data;

    public ListActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return swipeView = (SwipeRefreshLayout) inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        emptyText = (TextView) swipeView.findViewById(R.id.empty1);
        EditText searchBox = (EditText) swipeView.findViewById(R.id.search);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adaptor != null) {
                    adaptor.getFilter().filter(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        swipeView.setOnRefreshListener(this);
        if (data == null) {
            swipeView.setEnabled(false);
            onRefresh();
        } else {
            new ListResponse().onResponse(data);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("com.test.data", data);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            data = savedInstanceState.getParcelableArrayList("com.test.data");
        }
    }

    @Override
    public void onRefresh() {
        VolleyTest.getInstance(getActivity().getApplicationContext())
                .getQueue().add(new ListRequest(URL, new ListResponse(), new ErrorListener()));
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                emptyText.setText(getString(R.string.loading));
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        VolleyTest.getInstance(getActivity().getApplicationContext()).getQueue().cancelAll(new RequestQueue.RequestFilter() {

            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
    }

    private class ListResponse implements Response.Listener<ArrayList<ListModel>> {
        @Override
        public void onResponse(ArrayList<ListModel> response) {
            data = response;
            if(getActivity() != null) {
                adaptor = new ListAdaptor(getActivity(), response, R.id.list_item_image);
                ((DragListView) getListView()).setDragListAdapter(adaptor);
                swipeView.setEnabled(true);
                swipeView.setRefreshing(false);
                emptyText.setText(getString(R.string.empty_string));
            }
        }
    }

    private class ErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            if(getActivity() != null) {
                Toast.makeText(getActivity(),
                        getString(R.string.error), Toast.LENGTH_LONG).show();
                emptyText.setText(getString(R.string.empty_string));
                swipeView.setEnabled(true);
                swipeView.setRefreshing(false);
            }
        }
    }
}

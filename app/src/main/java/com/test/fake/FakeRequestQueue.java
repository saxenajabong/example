package com.test.fake;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.NoCache;

public class FakeRequestQueue extends RequestQueue {
    public FakeRequestQueue(Context context) {
        super(new NoCache(), new BasicNetwork(new FakeHttpStack(context)));
    }
}
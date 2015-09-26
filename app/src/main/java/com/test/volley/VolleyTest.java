package com.test.volley;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Holds the queues, ImageLoader, and image cache.
 */
public class VolleyTest {

    private ImageLruCache imageCache;
    private RequestQueue queue;
    private ImageLoader imageLoader;

    private VolleyTest(Context context) {
        imageCache = new ImageLruCache(ImageLruCache.getDefaultLruCacheSize());
        queue = Volley.newRequestQueue(context);
        imageLoader = new ImageLoader(queue, imageCache);
    }

    /**
     * Used to return the singleton Image cache
     * We do this so that if the same image is loaded
     * twice on two different activities, the cache still remains
     * @return ImageLruCach
     */
    public ImageLruCache getCache() {
        return imageCache;
    }
    /**
     * Used to return the singleton RequestQueue
     * @return RequestQueue
     */
    public RequestQueue getQueue() {
        return queue;
    }
    /**
     * Used to return the singleton imageloader
     * that utilizes the image lru cache.
     * @return ImageLoader
     */
    public ImageLoader getImageLoader(){
        return imageLoader;
    }

    private static VolleyTest TEST;
    private static Context CONTEXT;

    /**
     * Static method to return the instance of VolleyTest.
     *
     * @param context Context of the application to initialise the values
     * @return instance of the VolleyTest
     */
    public static synchronized VolleyTest getInstance(Context context) {
        if(TEST == null) {
            if(context == null) {
                throw new IllegalArgumentException("Context can not be null.");
            }
            CONTEXT = context;
            TEST = new VolleyTest(CONTEXT);
        }
        return TEST;
    }
}

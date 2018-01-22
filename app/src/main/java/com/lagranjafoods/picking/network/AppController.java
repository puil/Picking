package com.lagranjafoods.picking.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class AppController {

    private static AppController mInstance;
    private static Context mCtx;
    private RequestQueue mRequestQueue;
    private String token;

    private AppController(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    // if an instance is already create , it will return it . if no instance was created , it will create a new one then reurn it
    public static synchronized AppController getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AppController(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(final Request<T> request) {
        getRequestQueue().add(request);
    }

    public <T> void addToRequestQueueWithTag(final Request<T> request, String tag) {
        request.setTag(tag);
        getRequestQueue().add(request);
    }

    public String getToken(){
        return token;
    }

    public void setToken(String tokenValue){
        this.token = tokenValue;
    }
}
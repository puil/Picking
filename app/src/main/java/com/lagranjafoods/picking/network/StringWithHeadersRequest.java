package com.lagranjafoods.picking.network;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class StringWithHeadersRequest extends Request<StringResponseWithHeader> {
    private final Response.Listener<StringResponseWithHeader> mListener;
    private Map<String, String> mHeaders;

    /**
     * Creates a new request with the given method.
     *
     * @param method the request {@link Method} to use
     * @param url URL to fetch the string at
     * @param listener Listener to receive the String response with headers
     * @param errorListener Error listener, or null to ignore errors
     */
    public StringWithHeadersRequest(int method, String url, Response.Listener<StringResponseWithHeader> listener,
                               Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        mListener = listener;
    }

    @Override
    protected void deliverResponse(StringResponseWithHeader response) {
        mListener.onResponse(response);
    }

    @Override
    protected Response<StringResponseWithHeader> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        mHeaders = response.headers;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        StringResponseWithHeader obj = new StringResponseWithHeader();
        obj.setHeaders(mHeaders);
        obj.setResponseData(parsed);
        if (null != getTag() && getTag() instanceof String)
            obj.setRequestTag((String)getTag());
        return Response.success(obj, HttpHeaderParser.parseCacheHeaders(response));
    }
}

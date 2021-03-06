package com.lagranjafoods.picking.network;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.lagranjafoods.picking.models.PalletStateEnum;
import com.lagranjafoods.picking.models.PickingActionResultEnum;
import com.lagranjafoods.picking.network.deserializers.DateDeserializer;
import com.lagranjafoods.picking.network.deserializers.PalletStateDeserializer;
import com.lagranjafoods.picking.network.deserializers.PickingActionResultDeserializer;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GsonRequest<T> extends Request<T> {
    private Gson gson;
    private final Class<T> clazz;
    private final Map<String, String> headers;
    private final Response.Listener<T> listener;
    private final Type classType;
    private String mRequestBody = null;

    /**
     * Make a GET request and return a parsed object from JSON.
     *
     * @param url URL of the request to make
     * @param clazz Relevant class object, for Gson's reflection
     * @param headers Map of request headers
     */

    public GsonRequest(int method, String url, Class<T> clazz, Map<String, String> headers, String body,
                       Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);

        this.classType = null;
        this.clazz = clazz;
        this.listener = listener;
        this.headers = initializeHeadersIfNullAndAddToken(headers);
        this.mRequestBody = body;

        this.setDefaultRetryPolicy();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(PalletStateEnum.class, new PalletStateDeserializer() );
        gsonBuilder.registerTypeAdapter(PickingActionResultEnum.class, new PickingActionResultDeserializer() );
        gsonBuilder.registerTypeAdapter(Date.class, new DateDeserializer());
        gson = gsonBuilder.create();
    }


    public GsonRequest(int method, String url, Type classType, Map<String, String> headers, String body,
                       Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);

        this.clazz = null;
        this.classType = classType;
        this.listener = listener;
        this.headers = initializeHeadersIfNullAndAddToken(headers);
        this.mRequestBody = body;

        this.setDefaultRetryPolicy();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(PalletStateEnum.class, new PalletStateDeserializer() );
        gsonBuilder.registerTypeAdapter(PickingActionResultEnum.class, new PickingActionResultDeserializer() );
        gsonBuilder.registerTypeAdapter(Date.class, new DateDeserializer());
        gson = gsonBuilder.create();
    }

    private void setDefaultRetryPolicy(){

        int timeoutMilliseconds = 20000;
        int maxNumberRetries = 0;

        setRetryPolicy(new DefaultRetryPolicy(timeoutMilliseconds, maxNumberRetries, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private Map<String, String> initializeHeadersIfNullAndAddToken(Map<String, String> headersFromRequest) {
        if (headersFromRequest == null)
            headersFromRequest = new HashMap<>();

        if (!headersFromRequest.containsKey("Token")){
            String token = AppController.getStaticToken();
            headersFromRequest.put("Token", token);
        }

        if (!headersFromRequest.containsKey("RequestNumber")){
            int requestNumber = AppController.getStaticRequestNumber();
            headersFromRequest.put("RequestNumber", String.valueOf(requestNumber));
        }

        return headersFromRequest;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    public String getBodyContentType() {
        return "application/json; charset=utf-8";
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        try {
            return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
        } catch (UnsupportedEncodingException uee) {
            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
            return null;
        }
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

            if (clazz != null){
                return Response.success(
                        gson.fromJson(json, clazz),
                        HttpHeaderParser.parseCacheHeaders(response));
            } else {
                return Response.success(
                        (T)gson.fromJson(json, classType),
                        HttpHeaderParser.parseCacheHeaders(response));
            }

        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }
}

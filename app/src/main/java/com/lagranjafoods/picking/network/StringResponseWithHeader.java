package com.lagranjafoods.picking.network;

import java.util.Map;

public class StringResponseWithHeader {
    private String responseData;
    private Map<String, String> headers;
    private Object requestTag;

    public String getResponseData() {
        return responseData;
    }

    public void setResponseData(String responseData) {
        this.responseData = responseData;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> mHeaders) {
        this.headers = mHeaders;
    }

    public Object getRequestTag() {
        return requestTag;
    }

    public void setRequestTag(Object requestTag) {
        this.requestTag = requestTag;
    }

    public String getHeader(String key) {
        if (headers != null && headers.containsKey(key))
            return headers.get(key);
        else
            return null;
    }
}

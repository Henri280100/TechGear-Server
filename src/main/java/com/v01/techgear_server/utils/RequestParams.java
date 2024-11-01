package com.v01.techgear_server.utils;

import java.util.Map;

public class RequestParams {
    private Map<String, String> params;

    public RequestParams() {
    }

    public RequestParams(Map<String, String> params) {
        this.params = params;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

}

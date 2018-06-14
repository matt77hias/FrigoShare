package com.frigoshare.proxy;

import com.frigoshare.application.Application;
import com.frigoshare.endpoint.Endpoint;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;

public final class Proxy {

    public static final String URL = "http://192.168.1.7:8080/_ah/api/";

    public static Endpoint getEndpoint() {
        Endpoint.Builder builder =  new Endpoint.Builder(
                newHttpTransport(),
                getJsonFactory(),
                null);
        if (Application.get().isDebug()) {
            builder.setRootUrl(URL);
        }
        return builder.build();

    }

    private static HttpTransport newHttpTransport() {
        return AndroidHttp.newCompatibleTransport();
    }

    private static JsonFactory getJsonFactory() {
        return GsonFactory.getDefaultInstance();
    }

    public static Endpoint.Users getUserEndpoint() {
        return getEndpoint().users();
    }

    public static Endpoint.Leftovers getLeftoverEndpoint() {
        return getEndpoint().leftovers();
    }
}

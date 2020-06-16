package com.example.three3d.util;

import java.io.IOException;

import okhttp3.Connection;
import okhttp3.Request;
import okhttp3.Response;

public interface Interceptor {
    Response intercept(Chain chain) throws IOException;

    interface Chain {
        Request request();

        Response proceed(Request request) throws IOException;

        Connection connection();
    }
}
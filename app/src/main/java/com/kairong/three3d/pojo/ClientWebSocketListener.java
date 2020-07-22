package com.kairong.three3d.pojo;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.Nullable;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class ClientWebSocketListener extends WebSocketListener {

    private WebSocket mWebSocket;
    private Handler mWebSocketHandler;

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        mWebSocket = webSocket;
        mWebSocket.send("您好，我是客户端");
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        Message message = Message.obtain();
        message.obj = text;
        System.err.println("++++++++++++++++"+message);
        mWebSocketHandler.sendMessage(message);
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        Message message = Message.obtain();
        message.obj = bytes.utf8();
        System.err.println("===================="+message);
        mWebSocketHandler.sendMessage(message);
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        if (null != mWebSocket) {
            mWebSocket.close(1000, "再见");
            mWebSocket = null;
        }
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
    }
}

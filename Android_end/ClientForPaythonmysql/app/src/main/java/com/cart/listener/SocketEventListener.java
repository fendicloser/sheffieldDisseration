package com.cart.listener;

public interface SocketEventListener {
    void socketConnectSuccess();
    void socketConnectFailed();
    void socketReceviedData(String s);
}

package com.cart.application;

import android.app.Application;

import com.cart.observer.SocketEventObserver;

public class CartApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SocketEventObserver.getInstance().init(this);
    }
}

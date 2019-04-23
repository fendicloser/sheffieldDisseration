package com.cart.observer;
//observer model，
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.cart.activity.WarnViewActivity;
import com.cart.listener.SocketEventListener;

import java.util.ArrayList;
import java.util.List;

public class SocketEventObserver {
    private Handler handler = new Handler(Looper.getMainLooper());

    private List<SocketEventListener> socketEventListeners;
    private Context context;
    private static SocketEventObserver instance;

    private SocketEventObserver() {
        socketEventListeners = new ArrayList<>();
    }

    public void addObserve(SocketEventListener listener) {
        synchronized (socketEventListeners) {
            socketEventListeners.add(listener);
        }
    }

    public void init(Context context) {
        this.context = context;
    }

    public void removeObserve(SocketEventListener listener) {
        synchronized (socketEventListeners) {
            socketEventListeners.remove(listener);
        }
    }

    public static SocketEventObserver getInstance() {
        if (instance == null) {
            synchronized (SocketEventObserver.class) {
                if (instance == null) {
                    instance = new SocketEventObserver();
                }
            }
        }
        return instance;
    }
//Notification pattern,
// now there is only one notification pattern, that is, "z#" needs to be recognized
    public void notifyReceviedData(final String data) {
        synchronized (socketEventListeners) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (data.equals("z#")) {
                        //if recieved data is "z#", process the reaction of MQ-2 directly.
                        if (context == null) {
                            throw new IllegalArgumentException("please init context before this method");
                        }
                        Intent intent = new Intent(context, WarnViewActivity.class);//here,enter the warnViewActivity
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        return;
                    }
                    for (int index = 0; index < socketEventListeners.size(); index++) {
                        socketEventListeners.get(index).socketReceviedData(data);
                    }
                }
            });
        }
    }

    public void notifyConnectObserverFailed() {
        synchronized (socketEventListeners) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    for (int index = 0; index < socketEventListeners.size(); index++) {
                        socketEventListeners.get(index).socketConnectFailed();
                    }
                }
            });
        }
    }

    public void notifyConnectObserverSuccess() {
        synchronized (socketEventListeners) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    for (int index = 0; index < socketEventListeners.size(); index++) {
                        socketEventListeners.get(index).socketConnectSuccess();
                    }
                }
            });
        }
    }
}

package com.cart.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.cart.manager.ClientManager;

public class ClientService {
    private String host;
    private int port;
    private static ClientService instance;

    private ClientService(){

    }

    public static ClientService getInstance(){
        if (instance == null){
            synchronized (ClientService.class){
                if (instance == null){
                    instance = new ClientService();
                }
            }
        }
        return instance;
    }
    //transition the "host" and "value" to socket
    //ClientManager.getInstance().startClient(host,port);
    public void onStartCommand(Intent intent) {
        host = intent.getStringExtra("host");
        port = intent.getIntExtra("port",0);
        System.out.println("start client service host is "+host+"  and port is "+port);
        ClientManager.getInstance().startClient(host,port);
    }

    public void onDestroy() {
        System.out.println("stop client service >>>>>>>>>>>> ");
        ClientManager.getInstance().stopClient();
//        Intent intent = new Intent(this,ClientService.class);
//        intent.putExtra("host",host);
//        intent.putExtra("port",port);
//        startService(intent);
    }
}

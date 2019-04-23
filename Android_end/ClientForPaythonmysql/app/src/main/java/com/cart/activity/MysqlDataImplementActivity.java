package com.cart.activity;

import com.cart.manager.ClientManager;

public class MysqlDataImplementActivity extends MysqlDataActivity {
    @Override
    //setting title
    public void initHeadTitle() {
        title.setText("MySQL Data");
    }

    @Override
    //send socket data is 5#,    5#  =====> check mysql data
    public void sendStartCommand() {
        ClientManager.getInstance().sendData("5#");
    }

    @Override
    //send socket data is end5#, end%# =====>stop threading
    public void sendStopCommand() {
        ClientManager.getInstance().sendData("end5#");
    }
}

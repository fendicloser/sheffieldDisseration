package com.cart.activity;

import com.cart.manager.ClientManager;

public class ObstacleAvoidActivity extends BaseMovementActivity {
    @Override
    public void initHeadTitle() {
        title.setText("Obstacle Void");
    }

    @Override
    public void sendStartCommand() {
        ClientManager.getInstance().sendData("2#");
    }

    @Override
    public void sendStopCommand() {
        ClientManager.getInstance().sendData("end2#");
    }
}

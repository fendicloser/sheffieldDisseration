package com.cart.activity;

import com.cart.manager.ClientManager;

public class PreSettingRoadAcivity extends BaseMovementActivity {
    @Override
    public void initHeadTitle() {
        title.setText("Presetting Road");
    }

    @Override
    public void sendStartCommand() {
        ClientManager.getInstance().sendData("4#");
    }

    @Override
    public void sendStopCommand() {
        ClientManager.getInstance().sendData("end4#");
    }
}

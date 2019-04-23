package com.cart.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

import com.cart.R;
import com.cart.service.ClientService;

//as second activtiy , this activity is not able to go back the main activity

public class HomeActivity extends FragmentActivity implements View.OnClickListener {

    private Button trackBtn;
    private Button obstacleBtn;
    private Button humanControlBtn;
    private Button presettingRoadBtn;
    private Button mysqlBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        trackBtn = findViewById(R.id.track);
        obstacleBtn = findViewById(R.id.obstacle);
        humanControlBtn = findViewById(R.id.human_control);
        presettingRoadBtn = findViewById(R.id.persetting_road);
        mysqlBtn=findViewById(R.id.check_mysql);

        trackBtn.setOnClickListener(this);
        obstacleBtn.setOnClickListener(this);
        humanControlBtn.setOnClickListener(this);
        presettingRoadBtn.setOnClickListener(this);
        mysqlBtn.setOnClickListener(this);
    }
    //destroy()
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ClientService.getInstance().onDestroy();
    }

    @Override
    public void onClick(View v) {
        //define the path of buttons, moving to each activity
        //Set the events for each button
        if (v == trackBtn) {//tracking movemtn ,
            Intent intent = new Intent(this, TrackMovmentActivity.class);
            startActivity(intent);
        } else if (v == obstacleBtn) {// obstacle-avoidable movemnt (ultr)
            Intent intent = new Intent(this, ObstacleAvoidActivity.class);
            startActivity(intent);
        } else if (v == humanControlBtn) {//human control
            Intent intent = new Intent(this, HumanityMoveActivity.class);
            startActivity(intent);
        } else if (v == presettingRoadBtn) {//preseeting-path
            Intent intent = new Intent(this, PreSettingRoadAcivity.class);
            startActivity(intent);
        }else  if (v==mysqlBtn){//MySQL DATABASE activity
            Intent intent = new Intent(this, MysqlDataImplementActivity.class);
            startActivity(intent);

        }
    }
}

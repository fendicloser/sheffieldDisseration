package com.cart.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cart.R;

public class WarnViewActivity extends FragmentActivity implements View.OnClickListener {
    private ImageView back;
    private TextView title;
    private Vibrator vibrator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.warning_view_activity);
        back = findViewById(R.id.title_back);
        title = findViewById(R.id.title_text);
        //must ensure that no harmful gas is detected, or you will always return to this interface
        back.setOnClickListener(this);
        title.setText("Warn");

        //call the vibration function of phone,
        // <uses-permission android:name="android.permission.VIBRATE"/> in androidMainfest.xml
        back.post(new Runnable() {
            @Override
            public void run() {
                vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                long[] pattern = new long[] { 1000, 2000, 1000, 3000 };
                vibrator.vibrate(5000);
                System.out.println("warning");
            }
        });
//         vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//        long[] pattern = new long[] { 1000, 2000, 1000, 3000 };
//        vibrator.vibrate(5000);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this,HomeActivity.class);
        startActivity(intent);
        vibrator.cancel();
    }

    @Override
    public void onClick(View v) {
        onBackPressed();
    }
}

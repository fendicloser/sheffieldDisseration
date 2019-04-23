package com.cart.activity;
//the socket request is not sent when click on related button, is sent after enter the responding interfaces
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cart.R;
import com.cart.observer.SocketEventObserver;
import com.cart.listener.SimpleSocketEventListener;
import com.cart.service.ClientService;
import com.cart.utils.SharePreferenceUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText host;
    private EditText port;
    private EditText url;
    private Button connect;
    private ProgressDialog dialog;
    private SimpleSocketEventListener listener = new SimpleSocketEventListener(){
        @Override
        public void socketConnectFailed() {
            dialog.dismiss();
            Toast.makeText(MainActivity.this, "connect failed ", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void socketConnectSuccess() {
            dialog.dismiss();
            SharePreferenceUtils.getInstance(getApplicationContext()).saveUrl(url.getText().toString());
            Toast.makeText(MainActivity.this, "connect success ", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this,HomeActivity.class);
            startActivity(intent);
            finish();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        host = findViewById(R.id.host);
        port = findViewById(R.id.port);
        url = findViewById(R.id.url);
        connect = findViewById(R.id.connect);
        SocketEventObserver.getInstance().addObserve(listener);
        connect.setOnClickListener(this);
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        //test data
//        host.setText("10.100.60.43");
//        port.setText("29731");
//        url.setText("https://www.zoom.us");

        //These Numbers are frequently reset during testing,
        // and in order to simplify testing, the default values are changed
        //You can simplify the user's actions by modifying this
        host.setText("143.167.144.4");
        port.setText("29330");
        url.setText("http://143.167.145.101:8090");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SocketEventObserver.getInstance().removeObserve(listener);
    }

    @Override
    public void onClick(View v) {
        if (TextUtils.isEmpty(host.getText().toString())|| TextUtils.isEmpty(port.getText().toString())
                ||TextUtils.isEmpty(url.getText().toString())){
            Toast.makeText(this, "please input the information", Toast.LENGTH_SHORT).show();
            return;
        }
        dialog.show();
        //building Socket Connection, step1, capture the IP address and the Port address
        Intent intent = new Intent(this, ClientService.class);
        //get inputted ip address and port
        intent.putExtra("host",host.getText().toString());
        intent.putExtra("port",Integer.parseInt(port.getText().toString()));
        ClientService.getInstance().onStartCommand(intent);
    }
}

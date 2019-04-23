package com.cart.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;
import android.net.Uri;
import android.widget.MediaController;

import com.cart.R;
import com.cart.listener.SimpleSocketEventListener;
import com.cart.manager.ClientManager;
import com.cart.observer.SocketEventObserver;
import com.cart.utils.SharePreferenceUtils;

public class HumanityMoveActivity extends FragmentActivity implements View.OnClickListener, View.OnTouchListener {

    public static final int MOVE_FORWARD_MSG = 0x01;
    public static final int MOVE_BACK_FORWARD_MSG = MOVE_FORWARD_MSG + 1;
    public static final int MOVE_LEFT_MSG = MOVE_BACK_FORWARD_MSG + 1;
    public static final int MOVE_RIGHT_MSG = MOVE_LEFT_MSG + 1;

    public static final int CAMERA_UP_MSG = MOVE_RIGHT_MSG + 1;
    public static final int CAMERA_DOWN_MSG = CAMERA_UP_MSG + 1;
    public static final int CAMERA_LEFT_MSG = CAMERA_DOWN_MSG + 1;
    public static final int CAMERA_RIGHT_MSG = CAMERA_LEFT_MSG + 1;

    private ImageView back;
    private TextView title;
    private VideoView mWebView;
    private TextView temperature;
    private TextView humanity;
    private ImageView moveForward;
    private ImageView moveBackForward;
    private ImageView moveLeft;
    private ImageView moveRight;
    private ImageView cameraUp;
    private ImageView cameraDown;
    private ImageView cameraLeft;
    private ImageView cameraRight;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Message message = new Message();
            message.copyFrom(msg);
            System.out.println("message is "+msg.what);
            String command = (String) msg.obj;
            ClientManager.getInstance().sendData(command);
            sendMessageDelayed(message, 500);
        }
    };

    private SimpleSocketEventListener listener = new SimpleSocketEventListener() {
        @Override
        public void socketReceviedData(String s) {
            super.socketReceviedData(s);
            try {
                if (s.endsWith("#")) {
                    String content = s.replaceAll("#", "");
                    String[] split = content.split(",");
                    String[] temp = split[0].split(":");
                    String[] human = split[1].split(":");
                    temperature.setText("Temperature(℃): " + temp[1]);
                    humanity.setText("Humanity(%): " + human[1]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
///Users/fendicloser/AndroidStudioProjects/ClientForPaython/app/src/main/java/com/cart/activity/HumanityMoveActivity.java

    //firstly, implement base function(getting the temperature value and humidity value, end button, title )
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.humanity_move_activity);
        back = findViewById(R.id.title_back);
        title = findViewById(R.id.title_text);
        temperature = findViewById(R.id.temperature);
        humanity = findViewById(R.id.humanity);
        mWebView = findViewById(R.id.webview);
        moveForward = findViewById(R.id.move_forward);
        moveBackForward = findViewById(R.id.move_back);
        moveLeft = findViewById(R.id.move_left);
        moveRight = findViewById(R.id.move_right);
        cameraUp = findViewById(R.id.camera_up);
        cameraDown = findViewById(R.id.camera_back);
        cameraLeft = findViewById(R.id.camera_left);
        cameraRight = findViewById(R.id.camera_right);

        moveForward.setOnTouchListener(this);
        moveBackForward.setOnTouchListener(this);
        moveRight.setOnTouchListener(this);
        moveLeft.setOnTouchListener(this);

        cameraLeft.setOnTouchListener(this);
        cameraRight.setOnTouchListener(this);
        cameraDown.setOnTouchListener(this);
        cameraUp.setOnTouchListener(this);
		
        initHeadTitle();
        final String url = SharePreferenceUtils.getInstance(getApplicationContext()).getCamUrl();

        back.setOnClickListener(this);

        mWebView.setVideoURI(Uri.parse(url));
        mWebView.setMediaController(new MediaController(this));
        mWebView.start();
        back.setOnClickListener(this);
        sendStartCommand();
        SocketEventObserver.getInstance().addObserve(listener);
        System.out.println("the url is " + url);

    }

    private void sendStartCommand() {
        ClientManager.getInstance().sendData("3#");
    }

    private void initHeadTitle() {
        title.setText("Humanity Move");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SocketEventObserver.getInstance().removeObserve(listener);
        sendStopCommand();
    }

    private void sendStopCommand() {
        ClientManager.getInstance().sendData("end3#");
    }


    //only this function need to be implementted separatedly,
    //becaues there are some other commands that need to be sent to the socket in this activity.
    @Override
    public void onClick(View v) {
        finish();
    }

    @Override
    //here the "switch-case" can be used.
    public boolean onTouch(View v, MotionEvent event) {
        String command = "";
        int what = 0;
        if (v == moveForward) {
            command = "movingForward#";
            what = MOVE_FORWARD_MSG;
        } else if (v == moveBackForward) {
            command = "movingBack#";
            what = MOVE_BACK_FORWARD_MSG;
        } else if (v == moveLeft) {
            command = "movingLeft#";
            what = MOVE_LEFT_MSG;
        } else if (v == moveRight) {
            command = "movingRight#";
            what = MOVE_RIGHT_MSG;
        } else if (v == cameraDown) {
            command = "camDown#";
            what = CAMERA_DOWN_MSG;
        } else if (v == cameraUp) {
            command ="camUp#";
            what = CAMERA_UP_MSG;
        } else if (v == cameraRight) {
            command ="camRight#";
            what = CAMERA_RIGHT_MSG;
        } else if (v == cameraLeft) {
            command ="camLeft#";
            what = CAMERA_LEFT_MSG;
        }
        Message msg = handler.obtainMessage();
        msg.what = what;
        msg.obj = command;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                handler.sendMessage(msg);
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                handler.removeMessages(what);
                break;
            default:
                break;

        }

        return true;
    }
}

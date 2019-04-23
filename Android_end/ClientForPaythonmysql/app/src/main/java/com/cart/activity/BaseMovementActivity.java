package com.cart.activity;

//The parent  iactivity is integrated by three simple moving modes（tracking, obstacle avoidance, presetting path）
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.cart.R;
import com.cart.listener.SimpleSocketEventListener;
import com.cart.manager.ClientManager;
import com.cart.observer.SocketEventObserver;
import com.cart.utils.SharePreferenceUtils;

import java.io.IOException;

public abstract class BaseMovementActivity extends FragmentActivity implements View.OnClickListener {
    private TextView temperature;
    private TextView humanity;
    protected TextView title;
    private ImageView back;
    private VideoView mWebView;
    private SimpleSocketEventListener listener = new SimpleSocketEventListener() {
        @Override
        public void socketReceviedData(String s) {
            super.socketReceviedData(s);
            try {
                if (s.endsWith("#")) {
                    //interpreter correct termperatrue and humidity value from socket String
                    String content = s.replaceAll("#", "");
                    String[] split = content.split(",");
                    String[] temp = split[0].split(":");
                    String[] human = split[1].split(":");
                    temperature.setText("Temperature: " + temp[1]+"(℃)");
                    humanity.setText("Humanity: " + human[1]+"(%)");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_movent);
        back = findViewById(R.id.title_back);
        title = findViewById(R.id.title_text);
        temperature = findViewById(R.id.temperature);
        humanity = findViewById(R.id.humanity);
        mWebView = findViewById(R.id.webview);

        initHeadTitle();
        final String url = SharePreferenceUtils.getInstance(getApplicationContext()).getCamUrl();

        back.setOnClickListener(this);
        //get video's url, now is webview
        sendStartCommand();
        SocketEventObserver.getInstance().addObserve(listener);
        System.out.println("the url is "+url);
        mWebView.setVideoURI(Uri.parse(url));
        mWebView.setMediaController(new MediaController(this));
        mWebView.start();
    }

    public abstract void initHeadTitle();
    public abstract void sendStartCommand();
    public abstract void sendStopCommand();
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SocketEventObserver.getInstance().removeObserve(listener);
        sendStopCommand();
        mWebView.stopPlayback();
    }

    @Override
    public void onClick(View v) {
        finish();
    }

}

package com.cart.activity;

//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.FragmentActivity;
//import android.view.View;
//import android.webkit.WebChromeClient;
//import android.webkit.WebResourceRequest;
//import android.webkit.WebSettings;
//import android.webkit.WebView;
//import android.webkit.WebViewClient;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.cart.R;
//import com.cart.observer.SocketEventObserver;
//import com.cart.listener.SimpleSocketEventListener;
import com.cart.manager.ClientManager;
import com.cart.utils.SharePreferenceUtils;
//override the basemovemnt activity, and after go into this activity, change the title to "tracking movement"and sen the command "1#" to the server
public class TrackMovmentActivity extends BaseMovementActivity  {


    @Override
    public void initHeadTitle() {
        title.setText("Track Movement");
    }

    @Override
    public void sendStartCommand() {
        ClientManager.getInstance().sendData("1#");
    }
//if user click on "back" button on left-top, go back to the homeActivity.
    @Override
    public void sendStopCommand() {
        ClientManager.getInstance().sendData("end1#");
    }
}

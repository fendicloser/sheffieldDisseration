package com.cart.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.cart.R;
import com.cart.listener.SimpleSocketEventListener;
import com.cart.observer.SocketEventObserver;
import com.cart.utils.SharePreferenceUtils;

public abstract class MysqlDataActivity extends FragmentActivity implements View.OnClickListener {
    private TextView temperature;
    private TextView temperature2;
    private TextView temperature3;
    private TextView temperature4;
    private TextView temperature5;
    private TextView temperature6;
    private TextView temperature7;
    private TextView temperature8;
    private TextView temperature9;
    private TextView temperature10;
    private TextView temperature11;
    private TextView temperature12;
    private TextView temperature13;
    private TextView temperature14;
    private TextView temperature15;
    private TextView temperature16;
    private TextView temperature17;
    private TextView temperature18;
    private TextView temperature19;
    private TextView temperature20;
    private TextView temperature21;





    protected TextView title;
    private ImageView back;

    private SimpleSocketEventListener listener = new SimpleSocketEventListener() {
        @Override
        public void socketReceviedData(String s) {
            super.socketReceviedData(s);
            //now  s=123#123#123#123#123#123#123#123#123#123#123#123#123#123#123#123
            //String content = s.replaceAll("#", "");
            //                    String[] split = content.split(",");
            //                    String[] temp = split[0].split(":");
            //                    String[] human = split[1].split(":");
            String[] dataArray= s.split("#");
            //identify the # in string, and 
            //System.out.println(s);
            temperature.setText(dataArray[0]);
            temperature2.setText(dataArray[1]);
            temperature3.setText(dataArray[2]);
            temperature4.setText(dataArray[3]);
            temperature5.setText(dataArray[4]);
            temperature6.setText(dataArray[5]);
            temperature7.setText(dataArray[6]);
            temperature8.setText(dataArray[7]);
            temperature9.setText(dataArray[8]);
            temperature10.setText(dataArray[9]);
            temperature11.setText(dataArray[10]);
            temperature12.setText(dataArray[11]);
            temperature13.setText(dataArray[12]);
            temperature14.setText(dataArray[13]);
            temperature15.setText(dataArray[14]);
            temperature16.setText(dataArray[15]);
            temperature17.setText(dataArray[16]);
            temperature18.setText(dataArray[17]);
            temperature19.setText(dataArray[18]);
            temperature20.setText(dataArray[19]);
            temperature21.setText(dataArray[20]);


            //String str = "abcde";
            //char[] ch = str.toCharArray();
            //String[] strarray = str2.split(" ");

            try {
                if (s.endsWith("#")) {
                    //Screen and filter temperature and humidity， decoding
                    String content = s.replaceAll("#", "");
                    String[] split = content.split(",");
                    String[] temp = split[0].split(":");
                    String[] human = split[1].split(":");
                    //temperature.setText("Temperature: " + temp[0]+"(℃)");
                    temperature.setText("Temperature: " + s+"(℃)");

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msql_data);
        back = findViewById(R.id.title_back);
        title = findViewById(R.id.title_text);
        // need to put time, temperature, humidity in 21 textarea（7 day）
        temperature = findViewById(R.id.mysqldata);
        temperature2=findViewById(R.id.mysqldata2);
        temperature3=findViewById(R.id.mysqldata3);
        temperature4=findViewById(R.id.mysqldata4);
        temperature5=findViewById(R.id.mysqldata5);
        temperature6=findViewById(R.id.mysqldata6);
        temperature7=findViewById(R.id.mysqldata7);
        temperature8=findViewById(R.id.mysqldata8);
        temperature9=findViewById(R.id.mysqldata9);
        temperature10=findViewById(R.id.mysqldata10);
        temperature11=findViewById(R.id.mysqldata11);
        temperature12=findViewById(R.id.mysqldata12);
        temperature13=findViewById(R.id.mysqldata13);
        temperature14=findViewById(R.id.mysqldata14);
        temperature15=findViewById(R.id.mysqldata15);
        temperature16=findViewById(R.id.mysqldata16);
        temperature17=findViewById(R.id.mysqldata17);
        temperature18=findViewById(R.id.mysqldata18);
        temperature19=findViewById(R.id.mysqldata19);
        temperature20=findViewById(R.id.mysqldata20);
        temperature21=findViewById(R.id.mysqldata21);





        initHeadTitle();
        final String url = SharePreferenceUtils.getInstance(getApplicationContext()).getCamUrl();

        back.setOnClickListener(this);

        sendStartCommand();
        SocketEventObserver.getInstance().addObserve(listener);
        System.out.println("the url is "+url);

    }

    public abstract void initHeadTitle();
    public abstract void sendStartCommand();
    public abstract void sendStopCommand();
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SocketEventObserver.getInstance().removeObserve(listener);
        sendStopCommand();

    }

    @Override
    public void onClick(View v) {
        finish();
    }

}

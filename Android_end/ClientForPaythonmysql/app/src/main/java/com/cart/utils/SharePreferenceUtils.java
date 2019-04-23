package com.cart.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by yinjinbiao on 2018/4/20.
 */

public class SharePreferenceUtils {
    private String name = "cart";
    private String key = "url";

    private SharedPreferences sp;
    private static SharePreferenceUtils instance;
    private SharedPreferences.Editor editor;
    //get camUrl from MainActivity
    private SharePreferenceUtils(Context context) {
        sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    public void saveUrl(String url){
        editor.putString(key,url);
        editor.commit();
    }

    public String getCamUrl(){
        return sp.getString(key,"");
    }

    public static SharePreferenceUtils getInstance(Context context) {
        if (instance == null) {
            synchronized (SharePreferenceUtils.class) {
                if (instance == null) {
                    instance = new SharePreferenceUtils(context);
                }
            }
        }
        return instance;
    }
    public void clear() {
        editor.clear();
        editor.commit();
    }
}

package com.example.viduratest.Utills;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.viduratest.R;

import java.util.Objects;

public class Utill {

    public static void sharedUserKey(Context context,String userKey){
        SharedPreferences sharedpreferences = context.getSharedPreferences(context.getResources().getString(R.string.shared_preference), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(context.getResources().getString(R.string.login_preference), userKey);
        editor.apply();
    }

    public static String getUserPref(Context mContext) {
        String userKey = "";
        try {
            SharedPreferences loginPreferences = mContext.getSharedPreferences(mContext.getResources().getString(R.string.shared_preference), Context.MODE_PRIVATE);
            userKey = loginPreferences.getString(mContext.getResources().getString(R.string.login_preference),"");
        } catch (Exception e) {
            Log.e("getUserPreferences", e.toString());
        }
        return userKey;
    }

    public static boolean isConnected(Context context) {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", Objects.requireNonNull(e.getMessage()));
        }
        return connected;
    }

}

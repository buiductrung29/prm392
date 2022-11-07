package com.example.prm_392_nho_ho_ban;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import androidx.room.Room;

import com.example.prm_392_nho_ho_ban.schedulingservice.InternetStateReceiver;

public class MyApplication extends Application {
    public static final String CHANNEL_ID = "PRM391";
    public static final String CHANNEL_NAME = "PRM391";
    public static AppDatabase dbRoom;
    public static boolean INTERNET_STATE;

    @Override
    public void onCreate() {
        super.onCreate();
        createChannelNotification();
        initInternetState();
        initDatabase();
    }

    private void initDatabase() {
        dbRoom = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "PRM").allowMainThreadQueries().build();
    }

    private void initInternetState() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            INTERNET_STATE = true;
        }
        else
            INTERNET_STATE = false;
    }

    private void createChannelNotification() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if(manager!=null){
                manager.createNotificationChannel(channel);
            }
        }
       
    }

}

package com.example.prm_392_nho_ho_ban.schedulingservice;

import static com.example.prm_392_nho_ho_ban.MyApplication.INTERNET_STATE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.example.prm_392_nho_ho_ban.activity.WelcomeActivity;
import com.example.prm_392_nho_ho_ban.bean.Note;
import com.example.prm_392_nho_ho_ban.dao.NoteDAO;

import java.util.ArrayList;

public class InternetStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (isOnline(context)) {
            INTERNET_STATE = true;
            Toast.makeText(context, "You're currently online", Toast.LENGTH_LONG).show();
            NoteDAO noteDAO = new NoteDAO();

            noteDAO.syncRoomToFirebase(new NoteDAO.FirebaseCallBack() {
                @Override
                public void onCallBack(ArrayList<Note> noteList) {
                }

                @Override
                public void onCallBack() {
                    WelcomeActivity.updateFragment();
                }

                @Override
                public void onCallBack(ArrayList<Note> noteList, ArrayList<Note> noteUnpinList) {

                }
            });

        } else {
            INTERNET_STATE = false;
            Toast.makeText(context, "You're currently offline", Toast.LENGTH_LONG).show();
        }
    }

    public boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }
}


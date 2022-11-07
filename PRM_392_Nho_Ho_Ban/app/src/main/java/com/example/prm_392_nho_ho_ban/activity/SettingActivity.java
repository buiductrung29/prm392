package com.example.prm_392_nho_ho_ban.activity;

import androidx.appcompat.widget.Toolbar;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;


import com.example.prm_392_nho_ho_ban.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingActivity extends OptionsMenuActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private TextView tvEmailDisplay;
    private static SharedPreferences sharedPreferences;

    private void bindingView() {
        toolbar = findViewById(R.id.toolbar);
        nvDrawer = findViewById(R.id.nvView);
        tvEmailDisplay = nvDrawer.getHeaderView(0).findViewById(R.id.tvEmailDisplay);
    }

    private void bindindAction() {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingFragment())
                .commit();
        sideNav();
    }

    private void sideNav() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_table_rows_24);
        getSupportActionBar().setTitle("Setting");
        setupDrawerContent(nvDrawer);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PreferenceManager.getDefaultSharedPreferences(getBaseContext()).registerOnSharedPreferenceChangeListener(this);
        super.onCreate(savedInstanceState);
        setThemeOfApp();
        setContentView(R.layout.activity_setting);
        bindingView();
        bindindAction();

    }

    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            tvEmailDisplay.setText(user.getEmail());
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("color_option")) {
            this.recreate();
        }
    }



    public static class SettingFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }

        @Override
        public boolean onPreferenceTreeClick(Preference preference) {

            if (preference.getKey().equals("Ringtone")) {

                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, Settings.System.DEFAULT_NOTIFICATION_URI);

                String existingValue = getRingtonePreferenceValue(); // TODO
                if (existingValue != null) {
                    Log.i("aaaaa", "onPreferenceTreeClickddddd: ");
                    if (existingValue.length() == 0) {
                        // Select "Silent"
                        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
                    } else {
                        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(existingValue));
                    }
                } else {
                    // No ringtone has been selected, set to the default
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Settings.System.DEFAULT_NOTIFICATION_URI);
                }

                startActivityForResult(intent, 1);
                return true;
            } else {
                return super.onPreferenceTreeClick(preference);
            }
        }
    }        @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && data != null) {
            Uri ringtone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (ringtone != null) {
                setRingtonePreferenceValue(ringtone.toString()); // TODO
            } else {
                // "Silent" was selected
                setRingtonePreferenceValue(""); // TODO
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void setRingtonePreferenceValue(String ringtone)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("KEY_RINGTONE_PREFERENCE", ringtone);
        editor.commit();
    }

    public static String getRingtonePreferenceValue()
    {
        Log.i("aaaaaa", "getRingtonePreferenceValue: " + sharedPreferences.getString("KEY_RINGTONE_PREFERENCE",""));
        return sharedPreferences.getString("KEY_RINGTONE_PREFERENCE", null);
    }

    public void setThemeOfApp() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        switch (sharedPreferences.getString("color_option", "DEFAULT")) {
            case "DEFAULT":
                setTheme(R.style.Theme_PRM_392_Nho_Ho_Ban);
                break;
            case "BLACK":
                setTheme(R.style.BlackTheme);
                break;
            case "RED":
                setTheme(R.style.RedTheme);
                break;
            case "GREEN":
                setTheme(R.style.GreenTheme);
                break;
            case "PINK":
                setTheme(R.style.PinkTheme);
                break;

        }

    }

}
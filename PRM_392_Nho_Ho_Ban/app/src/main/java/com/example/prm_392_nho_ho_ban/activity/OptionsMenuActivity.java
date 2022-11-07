package com.example.prm_392_nho_ho_ban.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.prm_392_nho_ho_ban.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class OptionsMenuActivity extends AppCompatActivity {
    private DrawerLayout mdrawer;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mdrawer = findViewById(R.id.layoutDrawer);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mdrawer.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(this::selectDrawerItem);
    }

    public void setupBottomNavContent(BottomNavigationView bottomNavigationView) {
        bottomNavigationView.setOnItemSelectedListener(this::selectBottomItem);
    }

    private boolean selectBottomItem(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.itemRecord:
                return true;
            case R.id.itemDraw:
                return true;
            case R.id.itemText:
                createNote();
                break;
        }
        return true;

    }

    @SuppressLint("NonConstantResourceId")
    public boolean selectDrawerItem(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.itemHome:
                onClickHome();
                return true;
            case R.id.itemAccount:
                onClickAccount();
                return true;
            case R.id.itemCalendar:
                onClickCalendar();
                return true;
            case R.id.itemSetting:
                onClickSetting();
                return true;
            case R.id.itemBin:
                onClickBin();
                return true;
            case R.id.itemInformation:
                onClickInfo();
                return true;
        }
        return true;
    }

    private void onClickAccount() {

        startActivity(new Intent(getApplicationContext(), AccountActivity.class));
        this.finish();
    }

    private void onClickBin() {
        startActivity(new Intent(getApplicationContext(), NoteBinActivity.class));
    }

    private void onClickHome() {
        startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
    }

    private void createNote() {
        startActivity(new Intent(getApplicationContext(), AddNoteActivity.class));
    }

    private void onClickCalendar() {
        startActivity(new Intent(this, CalendarActivity.class));
    }

    private void onClickSetting() {
        startActivity(new Intent(this, SettingActivity.class));
    }

    private void onClickInfo() {
        startActivity(new Intent(this, InfoActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
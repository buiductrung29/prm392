package com.example.prm_392_nho_ho_ban.activity;

import static com.example.prm_392_nho_ho_ban.MyApplication.dbRoom;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_392_nho_ho_ban.R;
import com.example.prm_392_nho_ho_ban.adapter.AccountListAdapter;
import com.example.prm_392_nho_ho_ban.bean.User;
import com.example.prm_392_nho_ho_ban.dao.RoomUserDAO;
import com.example.prm_392_nho_ho_ban.fragment.LoginFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class AccountActivity extends OptionsMenuActivity {
    private RecyclerView accountRecyclerView;
    private AccountListAdapter accountListAdapter;
    private ArrayList<User> userList;
    private final RoomUserDAO roomUserDAO = dbRoom.createUserDAO();
    private Button btnAddAccount;
    private NavigationView nvDrawer;
    private LoginFragment loginFragment;
    private FragmentManager fragmentManager;
    private SharedPreferences sharedPreferences;
    private TextView tvEmailDisplay;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setThemeOfApp();
        setContentView(R.layout.activity_account);
        bindingUI();
        sideNav();
        bindingAction();
        authorize();
    }
    private void bindingAction(){
        btnAddAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginFragment = new LoginFragment();
                loginFragment.show(fragmentManager, "LoginFragment");
            }
        });
    }
    private void bindingUI() {
        toolbar = findViewById(R.id.toolbar);
        fragmentManager = getSupportFragmentManager();
        userList = (ArrayList<User>) roomUserDAO.getAllUser();
        btnAddAccount = findViewById(R.id.btnAddAccount);
        accountRecyclerView = findViewById(R.id.accountRecycler);
        LinearLayoutManager verticalLayoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        accountRecyclerView.setLayoutManager(verticalLayoutManager);
        accountListAdapter = new AccountListAdapter(this,userList);
        accountRecyclerView.setAdapter(accountListAdapter);
        nvDrawer = findViewById(R.id.nvView);
        tvEmailDisplay = nvDrawer.getHeaderView(0).findViewById(R.id.tvEmailDisplay);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
    private void authorize() {
        if (User.USER == null) {
            startActivity(new Intent(this, LoginActivity.class));
        }
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
    private void sideNav() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_table_rows_24);
        getSupportActionBar().setTitle("Change Account");
        setupDrawerContent(nvDrawer);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            tvEmailDisplay.setText(user.getEmail());
        }
    }
}
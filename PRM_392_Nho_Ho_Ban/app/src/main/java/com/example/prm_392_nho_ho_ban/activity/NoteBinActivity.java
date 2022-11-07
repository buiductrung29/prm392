package com.example.prm_392_nho_ho_ban.activity;

import static com.example.prm_392_nho_ho_ban.MyApplication.dbRoom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_392_nho_ho_ban.R;
import com.example.prm_392_nho_ho_ban.adapter.BinListAdapter;
import com.example.prm_392_nho_ho_ban.adapter.NoteListAdapter;
import com.example.prm_392_nho_ho_ban.bean.Note;
import com.example.prm_392_nho_ho_ban.bean.User;
import com.example.prm_392_nho_ho_ban.dao.NoteDAO;
import com.example.prm_392_nho_ho_ban.dao.RoomNoteDAO;
import com.google.android.material.navigation.NavigationView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class NoteBinActivity extends OptionsMenuActivity {

    private RecyclerView noteRecyclerView;
    private BinListAdapter noteListAdapter;
    private NoteDAO n = new NoteDAO();
    private ArrayList<Note> noteBinList;
    RoomNoteDAO roomNoteDAO = dbRoom.createNoteDAO();

    private NavigationView nvDrawer;
    private Toolbar toolbar;

    private SharedPreferences sharedPreferences;

    private void bindingUI() {
        nvDrawer = findViewById(R.id.nvView);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_table_rows_24);
        getSupportActionBar().setTitle("Bin");
        setupDrawerContent(nvDrawer);

        noteRecyclerView = findViewById(R.id.BinRecyclerView);
        getBinList();

        LinearLayoutManager verticalLayoutManager
                = new LinearLayoutManager(NoteBinActivity.this, LinearLayoutManager.VERTICAL, false);
        noteRecyclerView.setLayoutManager(verticalLayoutManager);
        noteListAdapter = new BinListAdapter(NoteBinActivity.this,noteBinList);
        noteRecyclerView.setAdapter(noteListAdapter);
    }

    private void getBinList() {
        noteBinList = (ArrayList<Note>) roomNoteDAO.getNoteBin(User.USER.getUid(), false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getBinList();
        noteListAdapter.update(noteBinList);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setThemeOfApp();
        setContentView(R.layout.activity_note_bin);
        bindingUI();
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
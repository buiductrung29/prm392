package com.example.prm_392_nho_ho_ban.fragment;

import static com.example.prm_392_nho_ho_ban.MyApplication.dbRoom;

import android.app.SearchManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_392_nho_ho_ban.R;
import com.example.prm_392_nho_ho_ban.adapter.NoteListAdapter;
import com.example.prm_392_nho_ho_ban.bean.Note;
import com.example.prm_392_nho_ho_ban.bean.User;
import com.example.prm_392_nho_ho_ban.dao.RoomNoteDAO;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Date;

public class FragmentUpcomingNote extends Fragment {
    private RecyclerView noteRecyclerView;
    private RecyclerView pinRecyclerView;
    private NoteListAdapter noteListAdapter;
    private NoteListAdapter pinListAdapter;
    private TextView tvMes3;
    private Timestamp todayTimestamp;
    private ArrayList<Note> pinList;
    private ArrayList<Note> unPinList;
    RoomNoteDAO roomNoteDAO = dbRoom.createNoteDAO();
    private ArrayList<Note> emptyList = new ArrayList<>();
    private SearchView searchView = null;

    public FragmentUpcomingNote() {
        super(R.layout.fragment_upcoming_note);
    }

    public static FragmentUpcomingNote newInstance(String param1, String param2) {
        FragmentUpcomingNote fragment = new FragmentUpcomingNote();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private void bindingUI(View view) {
        getUnpinNote(todayTimestamp);
        getPinNote(todayTimestamp);
        tvMes3 = view.findViewById(R.id.tvMes3);
        noteRecyclerView = view.findViewById(R.id.noteListRecyclerView);
        pinRecyclerView = view.findViewById(R.id.PinListRecyclerView);

        LinearLayoutManager verticalLayoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        pinRecyclerView.setLayoutManager(verticalLayoutManager);
        pinListAdapter = new NoteListAdapter(getActivity(), pinList);
        pinRecyclerView.setAdapter(pinListAdapter);

        LinearLayoutManager verticalLayoutManagerr
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        noteRecyclerView.setLayoutManager(verticalLayoutManagerr);
        noteListAdapter = new NoteListAdapter(getActivity(), unPinList);
        noteRecyclerView.setAdapter(noteListAdapter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upcoming_note, container, false);
        todayTimestamp = new Timestamp(new Date());
        bindingUI(view);
        return view;
    }

    private void getPinNote(Timestamp today) {
        pinList = (ArrayList<Note>) roomNoteDAO.getAllUpcomingNote(today, true, User.USER.getUid(), true);
    }

    private void getUnpinNote(Timestamp today) {
        unPinList = (ArrayList<Note>) roomNoteDAO.getAllUpcomingNote(today, false, User.USER.getUid(), true);
    }

    public void updateAdapter() {
        getPinNote(todayTimestamp);
        getUnpinNote(todayTimestamp);

        if (pinListAdapter != null) {
            pinListAdapter.update(pinList);
        }
        if (noteListAdapter != null) {
            noteListAdapter.update(unPinList);
        }
        checkEmpty();
    }

    private void checkEmpty() {
        if (tvMes3 != null) {
            if (pinList.isEmpty() && unPinList.isEmpty()) {
                tvMes3.setVisibility(View.VISIBLE);
            } else {
                tvMes3.setVisibility(View.INVISIBLE);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        if (menuItem != null) {
            searchView = (SearchView) menuItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            searchView.setQueryHint("Type to search Note");
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (!newText.equals("")) {
                        pinList = emptyList;
                        unPinList = searchNote(newText);
                        pinListAdapter.update(pinList);
                        noteListAdapter.update(unPinList);
                    } else {
                        updateAdapter();
                    }
                    return false;
                }
            });
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    private ArrayList<Note> searchNote(String newText) {
        ArrayList<Note> myNote = (ArrayList<Note>) roomNoteDAO.searchNote(User.USER.getUid(), newText, true);
        return myNote;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateAdapter();
    }
}
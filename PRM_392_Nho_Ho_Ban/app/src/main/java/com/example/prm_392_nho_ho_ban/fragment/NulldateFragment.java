package com.example.prm_392_nho_ho_ban.fragment;

import static com.example.prm_392_nho_ho_ban.MyApplication.dbRoom;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
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
import com.example.prm_392_nho_ho_ban.dao.NoteDAO;
import com.example.prm_392_nho_ho_ban.dao.RoomNoteDAO;

import java.util.ArrayList;

public class NulldateFragment extends Fragment {
    @SuppressLint("SimpleDateFormat")
    private RecyclerView noteRecyclerView;
    private RecyclerView pinRecyclerView;
    private NoteListAdapter noteListAdapter;
    private NoteListAdapter pinListAdapter;
    private NoteDAO n = new NoteDAO();
    private TextView tvMes0;
    private ArrayList<Note> pinList;
    private ArrayList<Note> unPinList;
    RoomNoteDAO roomNoteDAO = dbRoom.createNoteDAO();
    private ArrayList<Note> emptyList = new ArrayList<>();
    private SearchView searchView = null;

    public NulldateFragment() {
        super(R.layout.fragment_all_note);
    }

    public static FragmentAllNote newInstance() {
        FragmentAllNote fragment = new FragmentAllNote();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private void bindingUI(View view) {
        getPinNote();
        getUnpinNote();

        tvMes0 = view.findViewById(R.id.tvMes0);
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
        View view = inflater.inflate(R.layout.fragment_nulldate, container, false);
        bindingUI(view);
        return view;
    }

    private void getPinNote() {
        pinList = (ArrayList<Note>) roomNoteDAO.getAllNotRemindNote(User.USER.getUid(), true, true);
    }

    private void getUnpinNote() {
        unPinList = (ArrayList<Note>) roomNoteDAO.getAllNotRemindNote(User.USER.getUid(), false, true);
    }

    public void updateAdapter() {
        getPinNote();
        getUnpinNote();
        if (pinListAdapter != null) {
            pinListAdapter.update(pinList);
        }
        if (noteListAdapter != null) {
            noteListAdapter.update(unPinList);
        }
        checkEmpty();
    }

    private void checkEmpty() {
        if (tvMes0 != null) {
            if (pinList.isEmpty() && unPinList.isEmpty()) {
                tvMes0.setVisibility(View.VISIBLE);
            } else {
                tvMes0.setVisibility(View.INVISIBLE);
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
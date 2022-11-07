package com.example.prm_392_nho_ho_ban.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.prm_392_nho_ho_ban.fragment.FragmentAllNote;
import com.example.prm_392_nho_ho_ban.fragment.FragmentTodayNote;
import com.example.prm_392_nho_ho_ban.fragment.FragmentUpcomingNote;
import com.example.prm_392_nho_ho_ban.fragment.NulldateFragment;

public class VPAdapter extends FragmentStateAdapter {
    FragmentAllNote a;
    FragmentUpcomingNote b;
    FragmentTodayNote c;
    NulldateFragment z;

    public VPAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return c = new FragmentTodayNote();
            case 2:
                return a = new FragmentAllNote();
            case 3:
                return b = new FragmentUpcomingNote();
            default:
                return z = new NulldateFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public Fragment getItemByPosition(int position) {
        switch (position) {
            case 1:
                return c;
            case 2:
                return a;
            case 3:
                return b;
            default:
                return z;
        }
    }
}

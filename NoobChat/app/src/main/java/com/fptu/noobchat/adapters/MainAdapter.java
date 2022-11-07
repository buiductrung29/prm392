package com.fptu.noobchat.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.fptu.noobchat.views.fragments.ChatFragment;
import com.fptu.noobchat.views.fragments.ProfileFragment;
import com.fptu.noobchat.views.fragments.UserFragment;

public class MainAdapter extends FragmentStateAdapter {


    public MainAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ChatFragment();
            case 1:
                return new UserFragment();
            default:
                return new ProfileFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
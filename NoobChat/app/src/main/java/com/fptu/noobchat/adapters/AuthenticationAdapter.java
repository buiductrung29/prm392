package com.fptu.noobchat.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.fptu.noobchat.views.fragments.AboutFragment;
import com.fptu.noobchat.views.fragments.LoginFragment;
import com.fptu.noobchat.views.fragments.RegisterFragment;

public class AuthenticationAdapter extends FragmentStateAdapter {


    public AuthenticationAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new LoginFragment();
            case 1:
                return new RegisterFragment();
            default:
                return new AboutFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
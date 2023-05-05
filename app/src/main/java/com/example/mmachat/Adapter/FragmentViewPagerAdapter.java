package com.example.mmachat.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.mmachat.Fragments.CallsFragment;
import com.example.mmachat.Fragments.ChatsFragment;
import com.example.mmachat.Fragments.StatusFragment;

import java.util.ArrayList;

public class FragmentViewPagerAdapter extends FragmentStateAdapter {
    ArrayList<Fragment> fragmentsList = new ArrayList<Fragment>(){{add(new ChatsFragment());  add(new StatusFragment()); add(new CallsFragment());}};
    public FragmentViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentsList.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentsList.size();
    }


/*    @NonNull
    @Override
    //Selection of Tab item
    public Fragment getItem(int position) {
        switch (position){
            case 0: return new ChatsFragment();
            case 1: return new StatusFragment();
            case 2: return new CallsFragment();
            default: return new ChatsFragment();
        }
    }

    //Tab item count
    @Override
    public int getCount() {
        return 3;
    }


    //Tab item names
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 0){
            title = "Chats";
        }if(position ==1){
            title = "Status";
        }if (position ==2){
            title = "Calls";
        }
        return title;
    }*/
}

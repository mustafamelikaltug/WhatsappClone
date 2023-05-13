package com.example.mmachat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.mmachat.Adapter.FragmentViewPagerAdapter;
import com.example.mmachat.databinding.ActivityMainBinding;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    FirebaseAuth mAuth;
    FragmentViewPagerAdapter fragmentViewPagerAdapter;
    ArrayList<String> tabTitleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize View Binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        //Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //Initialize tabs title list
        tabTitleList = new ArrayList<>();
        tabTitleList.add("Chats");
        tabTitleList.add("Status");
        tabTitleList.add("Calls");

        //Initialize View Pager Adapter
        fragmentViewPagerAdapter = new FragmentViewPagerAdapter(this);

        binding.viewPager.setAdapter(fragmentViewPagerAdapter);

        //Attach Tab Layout and View Pager together
        new TabLayoutMediator(binding.tabLayout,binding.viewPager,((tab, position) -> tab.setText(tabTitleList.get(position)))).attach();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();
        if (itemId == R.id.menu_action_group_chat){
            Intent intentGroupChat = new Intent(MainActivity.this, GroupChatActivity.class);
            intentGroupChat.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentGroupChat);
            return true;
        } else if (itemId == R.id.menu_action_settings) {
            Intent intentSettings = new Intent(MainActivity.this, SettingsActivity.class);
            intentSettings.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentSettings);
            return true;
        } else if (itemId == R.id.menu_action_log_out) {
            signOut();
            return true;
        }else {
            return super.onOptionsItemSelected(item);}
    }

    //User close the session
    public void signOut(){
        mAuth.signOut();
        //Check if user successfully signing out
        if (mAuth.getCurrentUser() == null){
            Toast.makeText(this, "Sign out successfully", Toast.LENGTH_SHORT).show();
            //Return to Sign in page
            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        }else {
            Toast.makeText(this, "Sign out failed. Try again", Toast.LENGTH_SHORT).show();
        }
    }

  /*  private class ViewPagerAdapter extends FragmentStateAdapter{

        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return null;
        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }
*/
}
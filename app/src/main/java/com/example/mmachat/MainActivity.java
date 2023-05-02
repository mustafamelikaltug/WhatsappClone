package com.example.mmachat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.mmachat.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize View Binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        //Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

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
        if (itemId == R.id.menu_action_settings) {
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
}
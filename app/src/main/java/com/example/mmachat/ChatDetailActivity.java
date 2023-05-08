package com.example.mmachat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.mmachat.Adapter.UsersAdapter;
import com.example.mmachat.databinding.ActivityChatDetailBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class ChatDetailActivity extends AppCompatActivity {

    ActivityChatDetailBinding binding;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseDatabase = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        String senderId = auth.getUid();
        String receiverId = getIntent().getStringExtra("userId");

        //Get users details in order to show on toolbar in during chatting
        String username = getIntent().getStringExtra("username");
        String profilePic = getIntent().getStringExtra("profilePic");


        binding.textViewToolbarUserName.setText(username);


        //Try to get users profile pic with URL that saved as String
        Picasso.get().load(profilePic)
                //If there is not any URL then show a default image as avatar
                .placeholder(R.drawable.avatar).into(binding.imageViewToolbarProfilePic);


        //Move to Main Activity when tap to back arrow
        binding.imageViewBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatDetailActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
}
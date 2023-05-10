package com.example.mmachat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.mmachat.Adapter.ChatAdapter;
import com.example.mmachat.Models.Message;
import com.example.mmachat.databinding.ActivityGroupChatBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class GroupChatActivity extends AppCompatActivity {
    private ActivityGroupChatBinding binding;
    private FirebaseDatabase firebaseDatabase;
    private ArrayList<Message> messageArrayList;
    private ChatAdapter chatAdapter;
    private String senderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        senderId = FirebaseAuth.getInstance().getUid();

        //Initialize
        returnMainActivity();
        sendMessage();
        binding.textViewToolbarUserName.setText("Group Chat");
        firebaseDatabase = FirebaseDatabase.getInstance();
        messageArrayList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageArrayList,this);
        binding.chatRecyclerView.setAdapter(chatAdapter);
        binding.chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        getMessages();




    }

    //Return Main Activity
    public void returnMainActivity(){
        binding.imageViewBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentBackToMain = new Intent(GroupChatActivity.this,MainActivity.class);
                intentBackToMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentBackToMain);
            }
        });
    }

    //Send message by clicking Send Button and store the message inside database
    public void sendMessage(){
    binding.imageViewSendButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String message = binding.editTextEnterMessage.getText().toString();
            Message messageModel = new Message(senderId,message);
            messageModel.setTimestampp(new Date().getTime());

            binding.editTextEnterMessage.setText("");

            firebaseDatabase.getReference().child("Group Chat").push().setValue(messageModel)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                        }
                    });
        }
    });
    }

    //Get all old and new messages
    public void getMessages(){
        firebaseDatabase.getReference().child("Group Chat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Clear messages in order to avoid store same messages
                messageArrayList.clear();
                for (DataSnapshot snapshot1: snapshot.getChildren()){
                    //Get all messages inside the database
                    Message messageModel = snapshot1.getValue(Message.class);
                    messageModel.setMessageId(snapshot1.getKey());
                    messageArrayList.add(messageModel);
                }
                //Compare and show if anything change
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
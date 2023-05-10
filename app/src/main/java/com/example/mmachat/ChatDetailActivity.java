package com.example.mmachat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.mmachat.Adapter.ChatAdapter;
import com.example.mmachat.Adapter.UsersAdapter;
import com.example.mmachat.Models.Message;
import com.example.mmachat.databinding.ActivityChatDetailBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class ChatDetailActivity extends AppCompatActivity {

    private ActivityChatDetailBinding binding;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth auth;
    private ArrayList<Message> messageArrayList;
    private ChatAdapter chatAdapter;
    private String senderId,receiverId,senderRoom,receiverRoom;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseDatabase = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        senderId = auth.getUid();
        receiverId = getIntent().getStringExtra("userId");

        //Initialize toolbar,back and send button
        getReceiverInfos();
        returnMainActivity();
        sendMessage();

        //Initialize ArrayList, Chat Adapter, and Recycler View
        messageArrayList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageArrayList,this,receiverId);

        binding.chatRecyclerView.setAdapter(chatAdapter);
        binding.chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Save the Ids acc to from->to
        senderRoom = senderId + receiverId;
        receiverRoom = receiverId + senderId;

        //Initialize to get messages
        getMessages();
    }


    //Return Main Activity
    public void returnMainActivity(){
        binding.imageViewBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentBackToMain = new Intent(ChatDetailActivity.this,MainActivity.class);
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

                //Save message
                Message messageModel = new Message(senderId,message);

                //Get time
                messageModel.setTimestampp(new Date().getTime());

                //Clear the text box
                binding.editTextEnterMessage.setText("");

                //Create a folder named by Chats, and create inner folder named by senderId+receiverId
                firebaseDatabase.getReference().child("Chats").child(senderRoom).push().setValue(messageModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        //if it is success create another inner folder named by receiverId+senderId
                        //in order to understand which message is sent and which message was received
                        firebaseDatabase.getReference().child("Chats").child(receiverRoom).push().setValue(messageModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        });
                    }
                });

            }
        });
    }

    //Get all old and new messages
    public void getMessages(){
        firebaseDatabase.getReference().child("Chats").child(senderRoom).addValueEventListener(new ValueEventListener() {
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

    //Get Receiver datas in order to place inside the toolbar
    public void getReceiverInfos(){
        //Get users details in order to show on toolbar in during chatting
        String username = getIntent().getStringExtra("username");
        String profilePic = getIntent().getStringExtra("profilePic");

        binding.textViewToolbarUserName.setText(username);


        //Try to get users profile pic with URL that saved as String
        Picasso.get().load(profilePic)
                //If there is not any URL then show a default image as avatar
                .placeholder(R.drawable.avatar).into(binding.imageViewToolbarProfilePic);
    }
}
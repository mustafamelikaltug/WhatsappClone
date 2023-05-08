package com.example.mmachat.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mmachat.Adapter.UsersAdapter;
import com.example.mmachat.Models.Users;
import com.example.mmachat.R;
import com.example.mmachat.databinding.FragmentChatsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ChatsFragment extends Fragment {

    FragmentChatsBinding binding;
    ArrayList<Users> usersArrayList = new ArrayList<>();
    FirebaseDatabase firebaseDatabase;
    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        //Initiaize View Binding
        binding = FragmentChatsBinding.inflate(inflater,container,false);


        //Initiaize View Binding
        firebaseDatabase = FirebaseDatabase.getInstance();

        //Initialize Adapter
        UsersAdapter usersAdapter = new UsersAdapter(getContext(),usersArrayList);
        binding.chatRecyclerView.setAdapter(usersAdapter);
        binding.chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //Initialize Database and get data changes like adding new user or profile pic update by listener
        firebaseDatabase.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Users users = dataSnapshot.getValue(Users.class);
                    users.setUserId(dataSnapshot.getKey());

                    //Remove the current user from the list
                    if (!users.getUserId().equals(FirebaseAuth.getInstance().getUid())){
                        usersArrayList.add(users);
                    }
                }
                usersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return binding.getRoot();
    }
}
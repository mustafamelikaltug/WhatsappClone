package com.example.mmachat.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mmachat.ChatDetailActivity;
import com.example.mmachat.Models.Users;
import com.example.mmachat.R;
import com.example.mmachat.databinding.CardViewUserBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder> {
    private Context mContext;
    private ArrayList<Users> usersArrayList;

    public UsersAdapter(Context mContext, ArrayList<Users> usersArrayList) {
        this.mContext = mContext;
        this.usersArrayList = usersArrayList;
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder {
        CardViewUserBinding binding;
        public UsersViewHolder(@NonNull CardViewUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public UsersAdapter.UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardViewUserBinding binding = CardViewUserBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new UsersViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersAdapter.UsersViewHolder holder, @SuppressLint("RecyclerView") int position) {
        loadProfilePic(holder,position);

        //Set user name
        holder.binding.textViewFriendUserName.setText(usersArrayList.get(position).getUsername());

        getLastMessage(holder,position);

        moveToChatDetail(holder,position);
    }

    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }


    public void getLastMessage(@NonNull UsersAdapter.UsersViewHolder holder, @SuppressLint("RecyclerView") int position){
        Users users = usersArrayList.get(position);

        //Get last message form the database acc. to time
        FirebaseDatabase.getInstance().getReference().child("Chats").child(FirebaseAuth.getInstance().getUid()+users.getUserId())
                .orderByChild("timestampp").limitToLast(1).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChildren()){
                            for (DataSnapshot snapshot1:snapshot.getChildren()){
                                //Set last message
                                holder.binding.textViewFriendMessage.setText(snapshot1.child("message").getValue().toString());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    //Go to chat detail with user infos by tapping user
    public void moveToChatDetail(@NonNull UsersAdapter.UsersViewHolder holder, @SuppressLint("RecyclerView") int position){
        holder.binding.cardViewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ChatDetailActivity.class);
                intent.putExtra("userId",usersArrayList.get(position).getUserId());
                intent.putExtra("profilePic",usersArrayList.get(position).getProfilePic());
                intent.putExtra("username",usersArrayList.get(position).getUsername());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mContext.startActivity(intent);
            }
        });
    }

    public void loadProfilePic(@NonNull UsersAdapter.UsersViewHolder holder, @SuppressLint("RecyclerView") int position){
        //Try to get users profile pic with URL that saved as String
        Picasso.get().load(usersArrayList.get(position).getProfilePic())
                //If there is not any URL then show a default image as avatar
                .placeholder(R.drawable.avatar).into(holder.binding.imageViewProfilePic);
    }

}

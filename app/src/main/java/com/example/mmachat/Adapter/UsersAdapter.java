package com.example.mmachat.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.CpuUsageInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mmachat.ChatDetailActivity;
import com.example.mmachat.Fragments.ChatsFragment;
import com.example.mmachat.Models.Users;
import com.example.mmachat.R;
import com.example.mmachat.databinding.CardViewUserBinding;
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
        //Try to get users profile pic with URL that saved as String
        Picasso.get().load(usersArrayList.get(position).getProfilePic())
                //If there is not any URL then show a default image as avatar
                .placeholder(R.drawable.avatar).into(holder.binding.imageViewProfilePic);

        //Set user name
        holder.binding.textViewFriendUserName.setText(usersArrayList.get(position).getUsername());

        //Set last message
        holder.binding.textViewFriendMessage.setText(usersArrayList.get(position).getLastMessage());

        //move to Chat Detail Activity when tap to username on Chat Tab
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

    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }

}

package com.example.mmachat.Adapter;

import android.content.Context;
import android.os.CpuUsageInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
    public void onBindViewHolder(@NonNull UsersAdapter.UsersViewHolder holder, int position) {
        //Try to get users profile pic with URL that saved as String
        Picasso.get().load(usersArrayList.get(position).getProfilePic())
                //If there is not any URL then show a default image as avatar
                .placeholder(R.drawable.avatar).into(holder.binding.imageViewProfilePic);

        //Set user name
        holder.binding.textViewFriendUserName.setText(usersArrayList.get(position).getUsername());

        //Set last message
        holder.binding.textViewFriendMessage.setText(usersArrayList.get(position).getLastMessage());
    }

    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }

}

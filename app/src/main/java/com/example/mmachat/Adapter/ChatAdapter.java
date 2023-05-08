package com.example.mmachat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mmachat.Models.Message;
import com.example.mmachat.databinding.SampleReceiverBinding;
import com.example.mmachat.databinding.SampleSenderBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter {

    ArrayList<Message> messageArrayList;
    Context mContext;

    int SENDER_VIEW_TYPE = 1;
    int RECEIVER_VIEW_TYPE = 2;


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Check who sent message and initialize view binding according the that situation
        if (viewType == SENDER_VIEW_TYPE){
            SampleSenderBinding senderBinding = SampleSenderBinding.inflate(LayoutInflater.from(mContext),parent,false);
            return new SenderViewHolder(senderBinding);
        }else {
            SampleReceiverBinding receiverBinding = SampleReceiverBinding.inflate(LayoutInflater.from(mContext),parent,false);
            return new RecieverViewHolder(receiverBinding);
        }
    }


    //We have to create this method due to we have two different view holders.
    @Override
    public int getItemViewType(int position) {
        //Check if is message from current user or not
        if (messageArrayList.get(position).getUserId().equals(FirebaseAuth.getInstance().getUid())){
            return SENDER_VIEW_TYPE;
        }else {
            return RECEIVER_VIEW_TYPE;
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getClass() == SenderViewHolder.class){
            ((SenderViewHolder) holder).senderBinding.textViewSenderText.setText(messageArrayList.get(position).getMessage());
        }else {
            ((RecieverViewHolder) holder).receiverBinding.textViewReceiverText.setText(messageArrayList.get(position).getMessage());
        }
    }


    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }

    //Initialize SampleRecieverViewHolder
    public class RecieverViewHolder extends RecyclerView.ViewHolder{
        SampleReceiverBinding receiverBinding;
        public RecieverViewHolder(@NonNull SampleReceiverBinding receiverBinding) {
            super(receiverBinding.getRoot());
            this.receiverBinding = receiverBinding;
        }
    }


    //Initialize SampleSenderViewHolder
    public class SenderViewHolder extends RecyclerView.ViewHolder{
       SampleSenderBinding senderBinding;
        public SenderViewHolder(@NonNull SampleSenderBinding senderBinding) {
            super(senderBinding.getRoot());
            this.senderBinding = senderBinding;
        }
    }

}

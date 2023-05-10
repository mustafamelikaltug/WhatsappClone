package com.example.mmachat.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mmachat.Models.Message;
import com.example.mmachat.databinding.SampleReceiverBinding;
import com.example.mmachat.databinding.SampleSenderBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatAdapter extends RecyclerView.Adapter {

    ArrayList<Message> messageArrayList;
    Context mContext;
    String receiveId;

    int SENDER_VIEW_TYPE = 1;
    int RECEIVER_VIEW_TYPE = 2;

    public ChatAdapter(ArrayList<Message> messageArrayList, Context mContext) {
        this.messageArrayList = messageArrayList;
        this.mContext = mContext;
    }

    public ChatAdapter(ArrayList<Message> messageArrayList, Context mContext, String receiveId) {
        this.messageArrayList = messageArrayList;
        this.mContext = mContext;
        this.receiveId = receiveId;
    }

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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
       holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
           @Override
           public boolean onLongClick(View v) {
               new AlertDialog.Builder(mContext).setTitle("Delete")
                       .setMessage("Are you sure you want to delete this message?")
                       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                       String senderRoom = FirebaseAuth.getInstance().getUid() + receiveId;
                       firebaseDatabase.getReference().child("Chats").child(senderRoom)
                               .child(messageArrayList.get(position)
                                       .getMessageId()).setValue(null);
                   }
               }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dialog.cancel();
                   }
               }).show();

               return false;
           }
       });


        //Convert to timestamp to date hour-min-am or pm
        Long timestamp = messageArrayList.get(position).getTimestampp();
        Date date = new Date(timestamp);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a");


        if (holder.getClass() == SenderViewHolder.class){
            ((SenderViewHolder) holder).senderBinding.textViewSenderText.setText(messageArrayList.get(position).getMessage());
            ((SenderViewHolder) holder).senderBinding.textViewSendTime.setText(simpleDateFormat.format(date));
        }else {
            ((RecieverViewHolder) holder).receiverBinding.textViewReceiverText.setText(messageArrayList.get(position).getMessage());
            ((RecieverViewHolder) holder).receiverBinding.textViewRecieveTime.setText(simpleDateFormat.format(date));
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

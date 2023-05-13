package com.example.mmachat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.mmachat.Models.Users;
import com.example.mmachat.databinding.ActivitySettingsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    private FirebaseStorage firebaseStorage;
    private FirebaseAuth auth;
    private FirebaseDatabase firebaseDatabase;
    private String buttonText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.toolbar2.setTitle("Settings");
        setSupportActionBar(binding.toolbar2);
        //Set back button on toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        buttonText = "";

        //Initialize Firebase
        auth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        //Initialize methods
        editButton();
        selectPictureButton();
        getUserDatasFromDatabase();

    }

    //Set return to main function on Toolbar back arrow
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


//Edit for updating user profile
    public void editButton(){
        binding.buttonEditSave.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                binding.buttonEditSave.setText("Save");
                binding.editTextUserName.setEnabled(true);
                binding.editTextUserName.setBackgroundTintList(ColorStateList.valueOf(R.color.colorPrimary));
                binding.editTextStatus.setEnabled(true);
                binding.editTextStatus.setBackgroundTintList(ColorStateList.valueOf(R.color.colorPrimary));
                buttonText = binding.buttonEditSave.getText().toString();


                if (buttonText.equals("Save")){

                    binding.buttonEditSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            //Get users updated profile
                            String username = binding.editTextUserName.getText().toString().trim();
                            String status = binding.editTextStatus.getText().toString().trim();
                            if (!username.isEmpty() && !status.isEmpty()){

                                HashMap<String, String> stringHashMap = new HashMap<>();
                                stringHashMap.put("username",username);
                                stringHashMap.put("status",status);

                                //Pleace inside the database and update datas
                                firebaseDatabase.getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).updateChildren(Collections.unmodifiableMap(stringHashMap),
                                        new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                                Toast.makeText(SettingsActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();

                                                binding.buttonEditSave.setText("Edit");
                                                binding.editTextUserName.setBackgroundResource(android.R.color.transparent);
                                                binding.editTextUserName.setEnabled(false);
                                                binding.editTextStatus.setBackgroundResource(android.R.color.transparent);
                                                binding.editTextStatus.setEnabled(false);

                                            }
                                        });
                            }else {
                                Toast.makeText(SettingsActivity.this, "Please enter Username and Status", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }

            }
        });
    }


    public void getPictureFromGallery(){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //All type of images
        intent.setType("image/*");
        //req code can be any integer
        startActivityForResult(intent,25);
    }

    //A button going to gallery
    public void selectPictureButton(){
        binding.imageViewAddPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionForGallery(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        });
    }

    //Upload the picture that we get from gallery to FireStorage and Database
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Check the user selected a data
        if (data.getData()!=null){

            //Get Uri of the picture
            Uri sFile = data.getData();

            binding.imageViewUserProfilePic.setImageURI(sFile);


            //Create a Folder on Firebase Storage with name of profile_pic, and as inner file with the name of userID
            StorageReference storageReference = firebaseStorage.getReference().child("profile_pic").child(FirebaseAuth.getInstance().getUid());


            //Put the file inside Firebase Storage folder
            storageReference.putFile(sFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    //If putting file inside storage have succeed then get a downloadable link for that picture
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //If downloadable link got then put that link inside the users database
                            firebaseDatabase.getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("profilePic").setValue(uri.toString());
                            Toast.makeText(SettingsActivity.this, "Profile picture changed successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

        }
    }

    public void getUserDatasFromDatabase(){
        //Check Database if new data exist
        firebaseDatabase.getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Save every data of users inside the database to the Users class
                Users users = snapshot.getValue(Users.class);

                //Set picture
                Picasso.get().load(users.getProfilePic()).placeholder(R.drawable.avatar).into(binding.imageViewUserProfilePic);

                //Set username
                binding.editTextUserName.setText(users.getUsername());
                //Set user status
                binding.editTextStatus.setText(users.getStatus());

                if (users.getStatus()==null){
                    binding.editTextStatus.setText("Hello! I am using MMAChat :)");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    //Check user permission to move gallery
    public void checkPermissionForGallery(String permission){
        //If the permission is not granted
        if (ContextCompat.checkSelfPermission(SettingsActivity.this,permission)!= PackageManager.PERMISSION_GRANTED){
            //Check if showing permission detail is necessary
            if (ActivityCompat.shouldShowRequestPermissionRationale(SettingsActivity.this,permission)){
                //Show detail of permission to user
                Snackbar.make(binding.getRoot(),"Permission needed in order to get picture",Snackbar.LENGTH_INDEFINITE)
                        .setAction("Give Permission", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            requestPermission(permission);
                            }
                        }).show();
            }else {
                //If permission detail showing not necessary then go to the application permissions
                Toast.makeText(this, "Permission needed!", Toast.LENGTH_SHORT).show();

                //Go to Mobile Settings and let user give permission
                Intent intentPermission = new Intent();
                intentPermission.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package",SettingsActivity.this.getPackageName(),null);
                intentPermission.setData(uri);
                startActivity(intentPermission);
            }
        }else {
            getPictureFromGallery();
        }
    }
    public void requestPermission(String permission){
        ActivityCompat.requestPermissions(SettingsActivity.this,new String[]{permission},0);
    }
}

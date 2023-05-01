package com.example.mmachat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.mmachat.Models.Users;
import com.example.mmachat.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    ActivitySignUpBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize View Binding
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize Firebase Database
        firebaseDatabase = FirebaseDatabase.getInstance();


        binding.buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               signUp();
            }
        });

        binding.textViewAlreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alreadyHaveAccount();
            }
        });

    }

    // Create user and show message about sign up status
    public void signUp(){
        //Check if necessary areas are empty
        if (!binding.editTextUsername.getText().toString().isEmpty() && !binding.editTextEmailAdress.getText().toString().isEmpty()
                && !binding.editTextPassword.getText().toString().isEmpty()){
            String username = binding.editTextUsername.getText().toString().trim();
            String email = binding.editTextEmailAdress.getText().toString().trim();
            String password = binding.editTextPassword.getText().toString().trim();
            binding.progressBarSignUp.setVisibility(View.VISIBLE);

            //Signing un
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    binding.progressBarSignUp.setVisibility(View.GONE);
                    if (task.isSuccessful()){
                        Users user = new Users(username,email,password);

                        //Get user ID in order to save in database
                        String userUID = task.getResult().getUser().getUid();

                        //Save user parameters inside the database with the folder name of Users and userUID
                        firebaseDatabase.getReference().child("Users").child(userUID).setValue(user);



                        //Show message if Sign Up successful
                        Toast.makeText(SignUpActivity.this, "Sign Up completed succesfully", Toast.LENGTH_SHORT).show();
                    }else {
                        //Show message if Sign Up unsuccessful
                        Toast.makeText(SignUpActivity.this,"Sign Up Failed : " +task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            //Show message for enter credentials
            Toast.makeText(SignUpActivity.this, "Enter Credentials", Toast.LENGTH_SHORT).show();
        }
    }

    //Move to Sign In Activity
    public void alreadyHaveAccount(){
        Intent intentSignIn = new Intent(SignUpActivity.this,SignInActivity.class);
        //Close all the other activites except the last one
        intentSignIn.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intentSignIn);
    }
}
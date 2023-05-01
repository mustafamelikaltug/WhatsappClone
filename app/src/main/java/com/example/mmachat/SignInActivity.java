package com.example.mmachat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.mmachat.databinding.ActivitySignInBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity {
    ActivitySignInBinding binding;
    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null){
            // Check if user already signed in
            reload();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize View Binding
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Initialize Firebase auth
        mAuth = FirebaseAuth.getInstance();

        //Initialize Firebase Database
        firebaseDatabase = FirebaseDatabase.getInstance();


        binding.buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
            });


        binding.textViewClickToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickToSignUp();
            }
        });

    }

    public void signIn(){
        //Check if necessary areas are empty
        if (!binding.editTextEmailAdress.getText().toString().isEmpty() && !binding.editTextPassword.getText().toString().isEmpty()){
            String email = binding.editTextEmailAdress.getText().toString().trim();
            String password = binding.editTextPassword.getText().toString().trim();
            binding.progressBarSignIn.setVisibility(View.VISIBLE);

            //Signing in
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    binding.progressBarSignIn.setVisibility(View.GONE);
                    if (task.isSuccessful()){
                        reload();
                        //Show to user signing in status
                        Toast.makeText(SignInActivity.this, "Signing In successfully", Toast.LENGTH_SHORT).show();
                    }else {
                        //Show to user signing in status
                        Toast.makeText(SignInActivity.this, "Signing In failed : " +task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            //Show message for enter credentials
            Toast.makeText(SignInActivity.this, "Enter Credentials", Toast.LENGTH_SHORT).show();
        }
    }

    //Move to SignUp Activity
    public void clickToSignUp(){
        Intent intentSignUp = new Intent(SignInActivity.this,SignUpActivity.class);
        //Close all the other activites except the last one
        intentSignUp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intentSignUp);

    }

    //Move to Main Activity
    public void reload(){
        Intent intentMainActivity = new Intent(SignInActivity.this,MainActivity.class);
        startActivity(intentMainActivity);
        finish();
    }
}
package com.example.mmachat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.mmachat.Models.Users;
import com.example.mmachat.databinding.ActivitySignInBinding;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity {
    private ActivitySignInBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest, signUpRequest;

    private static final int REQ_ONE_TAP = 2;  // Can be any integer unique to the Activity.
    private boolean showOneTapUI = true;

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            // Check if user already signed in
            accessToMain();

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

        //Initialize One Tap
        oneTapClientInitializer();




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

        binding.buttonGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oneTapSignUpUI();
            }
        });

    }

    public void signIn() {
        //Check if necessary areas are empty
        if (!binding.editTextEmailAdress.getText().toString().isEmpty() && !binding.editTextPassword.getText().toString().isEmpty()) {
            String email = binding.editTextEmailAdress.getText().toString().trim();
            String password = binding.editTextPassword.getText().toString().trim();
            binding.progressBarSignIn.setVisibility(View.VISIBLE);

            //Signing in
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    binding.progressBarSignIn.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        accessToMain();
                        //Show to user signing in status
                        Toast.makeText(SignInActivity.this, "Signing in successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        //Show to user signing in status
                        Toast.makeText(SignInActivity.this, "Signing in failed : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            //Show message for enter credentials
            Toast.makeText(SignInActivity.this, "Enter Credentials", Toast.LENGTH_SHORT).show();
        }
    }

    //Move to SignUp Activity
    public void clickToSignUp() {
        Intent intentSignUp = new Intent(SignInActivity.this, SignUpActivity.class);
        //Close all the other activites except the last one
        intentSignUp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intentSignUp);

    }

    //Move to Main Activity
    public void accessToMain() {
        Intent intentMainActivity = new Intent(SignInActivity.this, MainActivity.class);
        startActivity(intentMainActivity);
        finish();
    }

    //Initialize oneTap, Sign in request and Sign up request
    public void oneTapClientInitializer() {
        oneTapClient = Identity.getSignInClient(this);
        signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.default_web_client_id))
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(true)
                        .build())
                // Automatically sign in when exactly one credential is retrieved.
                .setAutoSelectEnabled(true)
                .build();

        signUpRequest = BeginSignInRequest.builder().setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                //Your server's clinet ID,not your Android client ID
                .setServerClientId(getString(R.string.default_web_client_id))
                //Show all accounts on the device.
                .setFilterByAuthorizedAccounts(false).build()).build();
    }

    //Showing One Tap Sign Up UI
    public void oneTapSignUpUI() {

      /*  ActivityResultLauncher<IntentSenderRequest> intentSender = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    try {
                        SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(result.getData());
                        String idToken = credential.getGoogleIdToken();
                        if (idToken != null) {
                            AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                            mAuth.signInWithCredential(firebaseCredential).addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SignInActivity.this, "Signing in success", Toast.LENGTH_SHORT).show();
                                        FirebaseUser user = mAuth.getCurrentUser();
                                    } else {
                                        Toast.makeText(SignInActivity.this, "Signing in failure : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    } catch (ApiException e) {
                        e.printStackTrace();
                    }
                }
            }
        });*/

        oneTapClient.beginSignIn(signUpRequest).addOnSuccessListener(this, new OnSuccessListener<BeginSignInResult>() {
            @Override
            public void onSuccess(BeginSignInResult beginSignInResult) {
                try {
                    startIntentSenderForResult(beginSignInResult.getPendingIntent().getIntentSender(), REQ_ONE_TAP,null, 0, 0, 0);
                  /*  IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(beginSignInResult.getPendingIntent()).build();
                    intentSender.launch(intentSenderRequest);*/
                } catch (IntentSender.SendIntentException e) {
                    Toast.makeText(SignInActivity.this, "Sign in with Google failure - Couldn't start One Tap UI: " +e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }


            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //No Google Accounts found. Just continue presenting the signed-out UI.
                Toast.makeText(SignInActivity.this, "Sign in with Google failure - No Google Accounts found: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                Log.d("Google sign up failure : ", e.getLocalizedMessage());
            }
        });

    }


    //Sign up with googleIdToken
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_ONE_TAP:
                try {
                    SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                    String idToken = credential.getGoogleIdToken();
                    if (idToken != null) {
                        //Got an ID token from Google. Use it to authenticate with Firebase.
                        AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                        mAuth.signInWithCredential(firebaseCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser firebaseUser = mAuth.getCurrentUser();

                                    //Create a user
                                    Users user = new Users();
                                    user.setUserId(firebaseUser.getUid());
                                    user.setUsername(firebaseUser.getDisplayName());
                                    user.setProfilePic(firebaseUser.getPhotoUrl().toString());

                                    //Save the user infos inside the database
                                    firebaseDatabase.getReference().child("Users").child(firebaseUser.getUid()).setValue(user);

                                    Toast.makeText(SignInActivity.this, "Signed in successfully with Google Account as : " + firebaseUser.getDisplayName(), Toast.LENGTH_SHORT).show();
                                    accessToMain();
                                } else {
                                    Toast.makeText(SignInActivity.this, "Signing in failure : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                       Log.d("TAG","Got ID token");
                    }
                } catch (ApiException e) {
                    if (e.getStatusCode() == CommonStatusCodes.CANCELED) {
                        Log.d("Api Exception status code : ", "One-tap dialog was closed");
                        //Don't re-prompt the user
                        showOneTapUI = false;
                        break;
                    } else if (e.getStatusCode() == CommonStatusCodes.NETWORK_ERROR) {
                        Log.d("Api Exception status code : ", "One-tap encountered a network error.");
                        break;
                    } else {
                        Log.d("Api Exception status code : ", "Couldn't get credential from result = " + e.getLocalizedMessage());
                        break;
                    }
                }
                break;
        }
    }
}

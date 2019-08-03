package com.simcoder.uber;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DriverLoginActivity extends AppCompatActivity {
    private EditText mEmail, mPassword;
    private Button mLogin, mRegistration;
    ProgressBar mProgressBar;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    private static final int REQUEST_LOCATION_PERMISSIONS_ID = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_login);

        mAuth = FirebaseAuth.getInstance();

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null){
                    Intent intent = new Intent(DriverLoginActivity.this, DriverMapActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        mProgressBar=findViewById(R.id.vP_adl_progress_bar);
        mProgressBar.setVisibility(View.GONE);

        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);

        mLogin = (Button) findViewById(R.id.login);
        mRegistration = (Button) findViewById(R.id.registration);

        mRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                /*if (email.isEmpty() || password.isEmpty()) {
                    Snackbar.make(mRegistration, "Registration fields should not be empty", Snackbar.LENGTH_LONG).show();
                } else if (email.length() < 6 || password.length() < 8) {
                    Snackbar.make(mRegistration, "please enter av valid email/password", Snackbar.LENGTH_LONG).show();
                } */
                if(!validateEmail())
                {
                    //invalid email
                    return;
                }
                else if(!validatePassword())
                {
                    //invalid password
                    return;
                }
                if(mProgressBar!=null)
                {
                    if(!mProgressBar.isShown())
                    {
                        mProgressBar.setVisibility(View.VISIBLE);

                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(DriverLoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(mProgressBar.isShown())
                                {
                                    mProgressBar.setVisibility(View.GONE);
                                }
                                if (!task.isSuccessful()) {
                                    Toast.makeText(DriverLoginActivity.this, "sign up error", Toast.LENGTH_SHORT).show();
                                } else {
                                    String user_id = mAuth.getCurrentUser().getUid();
                                    DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(user_id).child("name");
                                    current_user_db.setValue(email);

                                    SharedPreferences loginpref=getSharedPreferences("LOGIN_PREFERENCE",MODE_PRIVATE);
                                    SharedPreferences.Editor editor=loginpref.edit();
                                    editor.putString("userTupe","driver");
                                    editor.putBoolean("isLogedIn",true);

                                    editor.putString("userId",FirebaseAuth.getInstance().getCurrentUser().getUid());

                                    editor.apply();

                                    Intent intent = new Intent(DriverLoginActivity.this, DriverMapActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                    }
                    else {
                        Toast.makeText(DriverLoginActivity.this, "Please wait a moment", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                if (email.isEmpty() || password.isEmpty()) {
                    Snackbar.make(mRegistration, "Registration fields should not be empty", Snackbar.LENGTH_LONG).show();
                } else if (email.length() < 6 || password.length() < 8) {
                    Snackbar.make(mRegistration, "please enter av valid email/password", Snackbar.LENGTH_LONG).show();
                }
                if(mProgressBar!=null)
                {
                    if(!mProgressBar.isShown())
                    {
                        mProgressBar.setVisibility(View.VISIBLE);

                        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(DriverLoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(mProgressBar.isShown())
                                {
                                    mProgressBar.setVisibility(View.GONE);
                                }
                                if (!task.isSuccessful()) {
                                    Toast.makeText(DriverLoginActivity.this, "sign in error", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    SharedPreferences loginpref=getSharedPreferences("LOGIN_PREFERENCE",MODE_PRIVATE);
                                    SharedPreferences.Editor editor=loginpref.edit();
                                    editor.putString("userTupe","driver");
                                    editor.putBoolean("isLogedIn",true);

                                    if(FirebaseAuth.getInstance().getCurrentUser().getUid()!=null)
                                    {
                                        editor.putString("userId",FirebaseAuth.getInstance().getCurrentUser().getUid());

                                    }
                                    editor.apply();

                                    Intent intent = new Intent(DriverLoginActivity.this, DriverMapActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                    }
                    else {
                        Toast.makeText(DriverLoginActivity.this, "Please wait a moment", Toast.LENGTH_SHORT).show();

                    }
                }


            }
        });
    }
    /*//email fiels validation*///
    private boolean validateEmail() {
        String email = mEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            Snackbar.make(mRegistration,"Please enter a valid email", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //password field validation
    private boolean validatePassword() {
        if (mPassword.getText().toString().trim().isEmpty()) {
            Snackbar.make(mRegistration,"Password field should not be empty", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        else  if(mPassword.getText().toString().trim().length()<8){
            Snackbar.make(mRegistration,"password should be atleast 8 characters", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }



    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }
    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }



}

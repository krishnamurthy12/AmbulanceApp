package com.simcoder.uber;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private Button mDriver, mCustomer;

    boolean isLoggedIn=false;
    String userType;

    String userId;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    FirebaseUser user;

    @Override
    protected void onStart() {
        super.onStart();
        auth=FirebaseAuth.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDriver = (Button) findViewById(R.id.driver);
        mCustomer = (Button) findViewById(R.id.customer);

        init();
        //startService(new Intent(MainActivity.this, onAppKilled.class));
        mDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DriverLoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        mCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CustomerLoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
    }

    private void init() {

        SharedPreferences loginPreference=getSharedPreferences("LOGIN_PREFERENCE",MODE_PRIVATE);
        isLoggedIn=loginPreference.getBoolean("isLogedIn",false);
        userType=loginPreference.getString("userTupe",null);
        gotoNextPage();
    }

    private void gotoNextPage() {
        if(isLoggedIn)
        {
            if(userType.equalsIgnoreCase("driver"))
            {
                Intent intent = new Intent(MainActivity.this, DriverMapActivity.class);
                startActivity(intent);
                finish();

            }
            else if(userType.equalsIgnoreCase("customer"))
            {
                Intent intent = new Intent(MainActivity.this, CustomerMapActivity.class);
                startActivity(intent);
                finish();
            }

        }
    }


}

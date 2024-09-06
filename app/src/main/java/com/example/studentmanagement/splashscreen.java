package com.example.studentmanagement;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studentmanagement.auth.views.LoginPage;

public class splashscreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        new Thread(()->{
            try {
                Thread.sleep(3000);

                Intent loginIntent = new Intent(splashscreen.this, LoginPage.class);
                startActivity(loginIntent);
                finish();

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

    }
}
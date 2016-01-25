package com.myapplication.tas;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class MainActivity extends AppCompatActivity {
    CountDownTimer Count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        create_counter();
        Count.start();
    }



    public void startAutomationActivity(){
        Intent it = new Intent(this,ShaventActivity.class);
        startActivity(it);
        Count.cancel();
        finish();
    }


    public void create_counter() {
        Count = new CountDownTimer(2000, 500) {

            // Action to check at every tic
            public void onTick(long millisUntilFinished) {
            }

            // Reset of the game when the timeout goes to 0
            public void onFinish() {
                startAutomationActivity();
            }
        };
    }
}

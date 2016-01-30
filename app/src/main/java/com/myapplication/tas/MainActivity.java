package com.myapplication.tas;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class MainActivity extends AppCompatActivity {
    CountDownTimer Count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //If the activity is launched from another activity bypass the home screen.
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            startShaventActivity();
        }else {
            setContentView(R.layout.activity_main);

            create_counter();
            Count.start();
        }
    }


    public void startShaventActivity(){
        setContentView(R.layout.blank_layout);
        final FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_container, new ShaventActivity(), new ShaventActivity().getClass().getName());

        fragmentTransaction.commit();
        try {
            Count.cancel();
        }catch (java.lang.NullPointerException e){
            e.printStackTrace();
        }
    }


    public void create_counter() {
        Count = new CountDownTimer(2000, 500) {

            // Action to check at every tic
            public void onTick(long millisUntilFinished) {
            }

            // Reset of the game when the timeout goes to 0
            public void onFinish() {
                startShaventActivity();
            }
        };
    }
}

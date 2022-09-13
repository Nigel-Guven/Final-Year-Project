package com.example.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class RegistryChooseActivity extends AppCompatActivity {

    ImageView mdriver, mpassenger;
    int driver_flag = 0;
    Animation animBlink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registry_choose);

        mdriver = findViewById(R.id.imageView5);
        mpassenger = findViewById(R.id.imageView6);
        animBlink = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);

        mpassenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                driver_flag = 0;
                mpassenger.startAnimation(animBlink);
                Intent i = new Intent(RegistryChooseActivity.this,RegisterNameActivity.class);
                i.putExtra("DRIVER_KEY", driver_flag);
                mpassenger.startAnimation(animBlink);
                startActivity(i);
            }
        });
        mdriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                driver_flag = 1;
                Intent i = new Intent(RegistryChooseActivity.this,RegisterNameActivity.class);
                i.putExtra("DRIVER_KEY", driver_flag);
                mdriver.startAnimation(animBlink);
                startActivity(i);
            }
        });

    }
}

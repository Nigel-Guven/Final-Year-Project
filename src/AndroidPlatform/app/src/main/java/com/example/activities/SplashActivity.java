package com.example.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity
{


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView tv = findViewById(R.id.textView3);
        ImageView iv = findViewById(R.id.imageView1);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.splashtransition);

        tv.startAnimation(fadeIn);
        iv.startAnimation(fadeIn);

        final Intent intentToMain = new Intent(this, LoginActivity.class);
        Thread diffThread = new Thread()
        {
            public void run()
            {
                try
                {
                    sleep(3000);
                }
                catch(InterruptedException err)
                {
                    err.printStackTrace();
                }
                finally
                {
                    startActivity(intentToMain);
                    finish();
                }
            }
        };

        diffThread.start();

    }
}

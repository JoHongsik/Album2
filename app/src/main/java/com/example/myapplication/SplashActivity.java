package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();
        handler.postDelayed(new splashhandler(),1500);

    }
    private class splashhandler implements Runnable{
        public void run(){
            startActivity(new Intent(SplashActivity.this,MainActivity.class));
            SplashActivity.this.finish();
        }
    }
}
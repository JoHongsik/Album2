package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.io.FileNotFoundException;

public class LocalImageActivity extends Activity {
    ImageView imageView;
    Uri targetUri;
    Bitmap bitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localimg_detail);
        imageView = (ImageView) findViewById(R.id.localImageview);


        Intent intent = getIntent();
        this.targetUri = intent.getParcelableExtra("targetUri");

        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));

            imageView.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

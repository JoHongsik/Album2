package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.myapplication.MainActivity.URL;
import static com.example.myapplication.MainActivity.comparePage;
import static com.example.myapplication.MainActivity.page;

public class PictureDetail extends Activity implements OnPictureAreaClickedListener {
    private ViewPager viewpager;
    private ImagePagerAdapter imagePagerAdapter;
    private int position;
    public ImageButton close_btn;
    public ImageButton save_btn;
    public ImageButton share_btn;
    private String FileName;
    private ArrayList<URLData> urlDataList;
    private String result;
    private int splitLength;
    private  ArrayList<String> findURL;
    private ProgressDialog dialog;
    private boolean setURLflag = true;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imageviewpager);

        // Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // widzet들 선언.
        viewpager = (ViewPager) findViewById(R.id.viewPager);
        close_btn = (ImageButton) findViewById(R.id.close_btn);
        save_btn = (ImageButton) findViewById(R.id.save_btn);
        share_btn = (ImageButton) findViewById(R.id.share_btn);

        // intent로 데이터 받아오기.
        Intent intent = getIntent();
        position = intent.getExtras().getInt("position");
        urlDataList = (ArrayList<URLData>) intent.getSerializableExtra("url");

        dialog = new ProgressDialog(PictureDetail.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Data Loading..");
        dialog.setCancelable(false);

        //close_btn 클릭시 detail페이지 나가기.
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // WRITE_EXTERNAL_STORAGE 권한 허가 요청
                CheckPermission();

                // gilde로 bitmap형식의 image파일 준비 후 saveimage 메소드 호출(이미지 저장)
                Glide.with(PictureDetail.this)
                        .load(urlDataList.get(position).getURL())
                        .asBitmap()
                        .into(new SimpleTarget<Bitmap>() {
                            @RequiresApi(api = Build.VERSION_CODES.M)
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                if(CheckPermission())
                                    saveImage(resource);
                            }
                        });
            }
        });

        share_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        imagePagerAdapter = new ImagePagerAdapter(this, urlDataList, position);
        imagePagerAdapter.setOnPictureAreaClickedListener(this);

        viewpager.setAdapter(imagePagerAdapter);
        viewpager.setCurrentItem(position);

        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float positionOffset, int i1) {
                Log.d("positionOffsetPixels",""+i1);
                if (i == (urlDataList.size()-1) && i1==0){
                    dialog.show();
                    Log.d("comparePage",""+comparePage);
                    Log.d("page",""+page);
                    if(setURLflag) {
                        setURLflag = false;
                        setURL();
                    }
                }
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private String saveImage(Bitmap image) {
        String savedImagePath = null;

        //FileName 정하기 (split으로 url이름 잘라서)
        FileName = urlDataList.get(position).getURL().split("-picture")[0];
        FileName = FileName.split("/photos/")[1];
        String imageFileName = FileName + ".jpg";

        // 파일경로 설정.
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + "/TestAlbum");
        boolean success = true;

        // 파일경로에 폴더가 존재하지 않으면 디렉토리 생성.
        if (!storageDir.exists()) {
            success = storageDir.mkdirs();
        }

        // Image파일의 경로와 이름 설정한 파일 생성. (/storage/emulated/0/Pictures/TestAlbum/enjoying-the-fresh-sea-air.jpg)
        if (success) {
            File imageFile = new File(storageDir, imageFileName);
            savedImagePath = imageFile.getAbsolutePath(); ///storage/emulated/0/Pictures/TestAlbum/enjoying-the-fresh-sea-air.jpg
            try {
                OutputStream fOut = new FileOutputStream(imageFile);
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);  // 비트맵 파일을 파일로 저장.
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Add the image to the system gallery , 미디어스캔 실행.
            galleryAddPic(savedImagePath);
            Toast.makeText(PictureDetail.this, "IMAGE SAVED", Toast.LENGTH_LONG).show();
        }
        return savedImagePath;
    }

    // 미디어 스캔 실행.
    private void galleryAddPic(String imagePath) { // https://underground2.tistory.com/43

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(imagePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }

    public boolean CheckPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.d("test","Permission is granted");
                return true;
            } else {
                Log.d("test","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.d("test","Permission is granted");
            return true;
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("test", "Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission
        }
    }

    private void setURL(){
        OkHttpClient client = new OkHttpClient();
        Log.d("setURL","setURL");
        Request request = new Request.Builder()
                .url(URL)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                result = response.body().string();

                splitLength = (result.split(MainActivity.findString).length);  // 찾는 문자열의 개수 저장.

                findURL = new ArrayList<>();
                String[] findURL1 = new String[splitLength]; // String.split한 배열을 저장하기 위한 배열 선언 (사이즈 61)
                findURL1 = result.split((MainActivity.findString));

                if (result.contains(MainActivity.findString)) {
                    for (int i = 1; i < splitLength; i++)
                        findURL.add(findURL1[i].split("\"")[1]);
                }


                PictureDetail.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setData();
                    }
                });
            }
        });
    }

    private void setData(){
        Log.d("test",""+findURL.size());
        for(int i=0; i<splitLength-1; i++){
            URLData urlData = new URLData(findURL.get(i),MainActivity.URLNumber++);
            urlDataList.add(urlData);
        }

        imagePagerAdapter.notifyDataSetChanged();
        viewpager.setAdapter(imagePagerAdapter);
        viewpager.setCurrentItem(urlDataList.size()-splitLength);

        setURLflag = true;
        dialog.dismiss();
    }

    @Override
    public void onPictureAreaClicked() {
        Log.d("onPictureAreaClicked","onPictureAreaClicked");
        if(close_btn.getVisibility() == View.VISIBLE) {
           close_btn.setVisibility(View.INVISIBLE);
            share_btn.setVisibility(View.INVISIBLE);
            save_btn.setVisibility(View.INVISIBLE);
        }
        else{
            close_btn.setVisibility(View.VISIBLE);
            share_btn.setVisibility(View.VISIBLE);
            save_btn.setVisibility(View.VISIBLE);
        }
    }
}



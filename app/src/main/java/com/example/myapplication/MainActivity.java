package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    public static int URLNumber = 1;
    private  ArrayList<String> findURL;

    // page를 넘겨주기 위한 변수(page), 스크롤 시 한번만 내리게 하기 위한 변수(comparePage)
    public static int page = 1;
    public static int comparePage = 2;

    // RecyclerView를 만들기 위한 변수들
    public static RecyclerView recyclerView;
    private Adapter Adapter;
    private RecyclerView.LayoutManager layoutManager;

    // url주소와 Split하기 위한 string 변수
    public static String URL =
            "https://www.gettyimages.com/photos/free?sort=mostpopular&mediatype=photography&phrase=free&license=rf,rm&page="+page+"&recency=anydate&suppressfamilycorrection=true";
    public static final String findString = "asset__thumb\"";

    // 가져온 페이지 소스를 저장할 string 변수
    private String result;

    // 사진이 들어있는 url을 저장하고 있는 List
    public ArrayList<URLData> urlDataList;

    // 페이지 소스에서 asset__thumb를 몇개 가지고 있는지
    private int splitLength;

    // Pinch zoomIn zoomOut을 하기 위한 변수들
    private ScaleGestureDetector scaleGestureDetector;
    private float detectFactor;
    private int spanCount = 3;
    private int compareSpanCount = 3;   // spanCount를 한번만 가져오기 위한 변수.

    //dialog
    private ProgressDialog dialog;

    //toolbar 관련 변수들
    private Toolbar toolbar;
    private boolean isChecked = false;  //isChecked가 false이면 toolbar에 사진 저장하기 버튼이 없음.
    private MenuItem saveItem;


    protected void onRestart() {
        super.onRestart();
        Log.d("onRestart","onRestart2");
    }

    protected void onPause() {
        super.onPause();
        Log.d("onPause","onPause");

    }

    protected void onStop() {
        super.onStop();
        Log.d("onStop","onStop");
    }

    protected void onDestroy() {
        super.onDestroy();
        Log.d("onDestroy","onDestroy");
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("onCreate","onCreate");

        // toolbart 세팅
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //화면 세로로 고정
       setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //adpater에 보낼 arrayList (이미지의 url값들이 들어있는 arrayList)
        urlDataList = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        //progress dialog 설정
        dialog = new ProgressDialog(MainActivity.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Data Loading..");
        dialog.setCancelable(false);

        // recyclerview setting
        setRecyclerView();

        // 크롤링해온 URL값 setting 후 view refresh
        setURL();

        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                detectFactor = detector.getScaleFactor();

                Log.d("getTimeDelta()",""+detector.getScaleFactor());
                return true;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                if(compareSpanCount!=spanCount) {
                    layoutManager = new GridLayoutManager(MainActivity.this, spanCount);
                    recyclerView.setLayoutManager(layoutManager);
                    Adapter.notifyDataSetChanged();
                    compareSpanCount = spanCount;
                }
            }
        });

        recyclerView.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // recyclerview에 들어오는 터치 이벤트를 파라미터로 사용해서 scaleGestureDetector 활성화
                scaleGestureDetector.onTouchEvent(event);

                // zoom out
                if(detectFactor < 0.995) {
                    if(spanCount == 3 && (compareSpanCount == spanCount)) {
                        spanCount++;
                    }
                    else if(spanCount == 2 && (compareSpanCount == spanCount)) {
                        spanCount++;
                    }
                }

                // zoom in
                else if(detectFactor > 1.011) {
                    Log.d("spanCount= ",""+spanCount);
                    Log.d("compareSpanCount= ",""+compareSpanCount);
                    if(spanCount == 4 && (compareSpanCount == spanCount)) {
                        spanCount--;
                    }
                    else if(spanCount == 3 && (compareSpanCount == spanCount)) {
                        spanCount--;
                    }
                }
                return false;
            }
        });

        // recyclerview 끝지점시 listener -> Progress dialog -> setURL 및 recyclerview 데이터 업데이트
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(!recyclerView.canScrollVertically(1)){
                    URL = "https://www.gettyimages.com/photos/free?sort=mostpopular&mediatype=photography&phrase=free&license=rf,rm&page="+page+"&recency=anydate&suppressfamilycorrection=true";
                    dialog.show();
                    // scroll을 여러번해서 page가 두번이상 넘어가는것을 방지하기 위한 if문
                    if(comparePage == page) {
                        comparePage++;
                        setURL();
                    }
                }
            }
        });
    }

    private void setRecyclerView(){
        recyclerView.setHasFixedSize(true);

        Adapter = new Adapter(urlDataList,this);
        layoutManager = new GridLayoutManager(this,spanCount);

        recyclerView.setAdapter(Adapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void setURL(){
        OkHttpClient client = new OkHttpClient();
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

                splitLength = (result.split(findString).length);  // 찾는 문자열의 개수 저장.

                findURL = new ArrayList<>();
                String[] findURL1 = new String[splitLength]; // String.split한 배열을 저장하기 위한 배열 선언 (사이즈 61)
                findURL1 = result.split((findString));

                if (result.contains(findString)) {
                    for (int i = 1; i < splitLength; i++)
                        findURL.add(findURL1[i].split("\"")[1]);

                }
                dialog.dismiss();

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        page++;
                        setData();
                    }
                });
            }
        });
    }

    private void setData(){
        for(int i=0; i<splitLength-1; i++){
            URLData urlData = new URLData(findURL.get(i),URLNumber++);
            urlDataList.add(urlData);
        }
        Adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if(isChecked) {
            Adapter.settingClicked();
            Adapter.setCheckedNull();

            isChecked = !isChecked;
            saveItem.setVisible(isChecked);

        }
        else
            super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);

        saveItem = menu.findItem(R.id.save);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                if(isChecked) {
                    isChecked = false;
                    saveItem.setVisible(isChecked);
                    Adapter.setCheckedNull();
                }
                else {
                    isChecked = true;
                    saveItem.setVisible(isChecked);
                }
                    Adapter.settingClicked();
                return true;
            case R.id.save:
                Adapter.saveClicked();

        }

        return super.onOptionsItemSelected(item);
    }
}
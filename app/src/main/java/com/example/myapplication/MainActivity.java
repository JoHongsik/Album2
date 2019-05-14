package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
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
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private  ArrayList<String> findURL;

    // page를 넘겨주기 위한 변수(page), 스크롤 시 한번만 내리게 하기 위한 변수(comparePage) , 처음 시작해서 setURL을 하면 둘의 숫자가 같아짐.
    public int page = 1;
    public int comparePage = 2;

    // RecyclerView를 만들기 위한 변수들
    public RecyclerView recyclerView;
    private RecyclerviewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    // url주소와 Split하기 위한 string 변수
    public String URL =
            "https://www.gettyimages.com/photos/free?sort=mostpopular&mediatype=photography&phrase=free&license=rf,rm&page="+page+"&recency=anydate&suppressfamilycorrection=true";
    public String findString = "asset__thumb\"";

    // 가져온 페이지 소스를 저장할 string 변수
    private String result;

    // 사진이 들어있는 url을 저장하고 있는 List
    public static ArrayList<URLData> urlDataList;

    // 페이지 소스에서 asset__thumb를 몇개 가지고 있는지
    private int splitLength;

    // Pinch zoomIn zoomOut을 하기 위한 변수들
    private ScaleGestureDetector scaleGestureDetector;
    private float detectFactor;
    private int spanCount = 3;
    private int compareSpanCount = 3;   // spanCount를 한번만 가져오기 위한 변수.

    //toolbar 관련 변수들
    private Toolbar toolbar;
    private boolean isChecked = false;  //isChecked가 false이면 toolbar에 사진 저장하기 버튼이 없음.
    private MenuItem saveItem;

    private int currentPosition;

    private ProgressBar progressBar;

    private ArrayList<Integer> seenArray;

    private String kind;

    @Override
    protected void onRestart() {
        super.onRestart();

        Log.d("onRestart","onRestart2");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("onPause","onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("onStop","onStop");
    }

    @Override
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

        kind = "mostpopular";

        //화면 세로로 고정
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //adpater에 보낼 arrayList (이미지의 url값들이 들어있는 arrayList)
        urlDataList = new ArrayList<>();

        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        // recyclerview setting
        setRecyclerView();

        // 크롤링해온 URL값 setting 후 view refresh
        setURL();

        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                detectFactor = detector.getScaleFactor();

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
                    adapter.notifyDataSetChanged();
                    compareSpanCount = spanCount;
                }
            }
        });
    }

    private void setRecyclerView(){
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new RecyclerviewAdapter(this);
        layoutManager = new GridLayoutManager(this,spanCount);

        // recyclerview 끝지점시 listener -> Progressbar -> setURL 및 recyclerview 데이터 업데이트
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                Log.d("size", ""+urlDataList.size());

                if(!recyclerView.canScrollVertically(1)){
                    URL = String.format("https://www.gettyimages.com/photos/free?sort=%s&mediatype=photography&phrase=free&license=rf," +
                            "rm&page=%d&recency=anydate&suppressfamilycorrection=true",kind,page);

                    // scroll을 여러번해서 page가 두번이상 넘어가는것을 방지하기 위한 if문
                    if(comparePage == page ) {
                        comparePage++;
                        setURL();
                    }
                }
            }
        });

        recyclerView.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // recyclerview에 들어오는 터치 이벤트를 파라미터로 사용해서 scaleGestureDetector 활성화
                scaleGestureDetector.onTouchEvent(event);

                // zoom out
                if(detectFactor < 0.96) {
                    if(spanCount == 3 && (compareSpanCount == spanCount)) {
                        spanCount++;
                    }
                    else if(spanCount == 2 && (compareSpanCount == spanCount)) {
                        spanCount++;
                    }
                }

                // zoom in
                else if(detectFactor > 1.02) {
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
    }

    private void setURL(){
        Log.d("setURL","setURL");

        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URL)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                progressBar.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Log.d("onFailure",e.getMessage());

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "인터넷 연결을 해주세요!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                Log.d("onResponse","onResponse"+page);

                result = response.body().string();

                splitLength = (result.split(findString).length);  // 찾는 문자열의 개수 저장.

                findURL = new ArrayList<>();
                String[] findURL1 = new String[splitLength]; // String.split한 배열을 저장하기 위한 배열 선언 (사이즈 61)
                findURL1 = result.split((findString));

                if (result.contains(findString)) {
                    for (int i = 1; i < splitLength; i++)
                        findURL.add(findURL1[i].split("\"")[1]);
                }


                MainActivity.this.runOnUiThread(new Runnable() {
        @Override
        public void run() {
            progressBar.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            page++;
            comparePage = page;
            setData();
            }
        });
        }
        });
    }

    private void setData(){
        Log.d("setData","setData");

        for(int i=0; i<splitLength-1; i++){
            URLData urlData = new URLData(findURL.get(i));
            urlDataList.add(urlData);
        }

        adapter.setPage(page);

        if(page<=2) {
            Log.d("Page","page"+page);
            Log.d("Page","page"+comparePage);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(layoutManager);
        }

        else{
            adapter.notifyItemRangeInserted(urlDataList.size()-(splitLength-1), splitLength-1);
        }
    }

    @Override
    public void onBackPressed() {
        if(isChecked) {
            adapter.settingClicked();
            adapter.setCheckedNull();

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
                    adapter.setCheckedNull();
                }
                else {
                    isChecked = true;
                    saveItem.setVisible(isChecked);
                }
                    adapter.settingClicked();
                break;

            case R.id.save:
                adapter.saveClicked();
                saveImage();
                break;
            case R.id.filter1:
                if(!kind.equals("mostpopular")) {
                    kind = "mostpopular";
                    refresh();
                    break;
                }
                else{
                    Toast.makeText(this, "Already here", Toast.LENGTH_SHORT).show();
                    break;
                }
            case R.id.filter2:
                if(!kind.equals("newest")) {
                    kind = "newest";
                    refresh();
                    break;
                }
                else{
                    Toast.makeText(this, "Already here", Toast.LENGTH_SHORT).show();
                    break;
                }

            case R.id.filter3:
                if(!kind.equals("best")) {
                    kind = "best";
                    refresh();
                    break;
                }
                else{
                    Toast.makeText(this, "Already here", Toast.LENGTH_SHORT).show();
                    break;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveImage(){
        for(int j=0; j<urlDataList.size(); j++){
            if(urlDataList.get(j).getCheckBoxState())
                Log.d("몇번째가 체크됬어?", ""+j);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case 3000:
                    this.page = data.getExtras().getInt("page");
                    this.currentPosition = data.getExtras().getInt("currentPosition");
                    this.seenArray = data.getExtras().getIntegerArrayList("seenArray");

                    if(comparePage!=page) {
                        adapter.notifyItemRangeInserted(urlDataList.size() - (splitLength - 1), splitLength - 1);
                        recyclerView.smoothScrollToPosition(currentPosition);
                        comparePage = page;
                    }
                    else
                        recyclerView.smoothScrollToPosition(currentPosition);

                    // 음영효과 주기 위한 for문
                    // seenArray는 사용자가 이미 본 이미지의 url number의 array
                    // urlDataList 전체를 검색 할 for문 돌릴 필요가 없음
                    for(int j=0; j<seenArray.size(); j++){
                        adapter.notifyItemChanged(seenArray.get(j));
                    }
            }
        }
    }

    public void refresh(){
        seenArray = new ArrayList<>();
        page = 1;
        comparePage = 2;
        spanCount = 3;

        URL = String.format("https://www.gettyimages.com/photos/free?sort=%s&mediatype=photography&phrase=free&license=rf," +
                "rm&page=%d&recency=anydate&suppressfamilycorrection=true",kind,page);

        currentPosition = 0;

        recyclerView.getRecycledViewPool().clear();

        urlDataList = new ArrayList<>();

        setRecyclerView();
        setURL();
    }
}
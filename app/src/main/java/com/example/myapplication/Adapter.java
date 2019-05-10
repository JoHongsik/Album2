package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ImgViewHolder> {
    public ArrayList<URLData> urlDataList;
    public Context context;
    public boolean checkboxVis = false;
    public ArrayList<Integer> CheckedNo;

    public Adapter(ArrayList<URLData> urlDataList, Context context){
        this.urlDataList = urlDataList;
        this.context = context;
        CheckedNo = new ArrayList<>();  // test
    }

    @NonNull
    @Override
    public ImgViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recyclerview,viewGroup,false);
        return new ImgViewHolder(view);
    }

    //View의 내용을 해당 포지션의 데이터로 바꾼다.
    @Override
    public void onBindViewHolder(@NonNull ImgViewHolder ViewHolder, int i) {
        ViewHolder.checkBox.setChecked(urlDataList.get(i).getCheckBoxState());

        if(checkboxVis) {
            ViewHolder.checkBox.setVisibility(View.VISIBLE);
        }
        else {
            ViewHolder.checkBox.setVisibility(View.GONE);
        }

        Glide.with(context)
                .load(urlDataList.get(i).getURL())
                .into(ViewHolder.imageView);



        ViewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            // Adapter -> setOnCheckedChangeListener를 사용하면
            // notify를 하면 원래 체크박스들을 uncheck함
            // 그래서 그냥 setOnClickListener를 사용 함.
            public void onClick(View v) {
                urlDataList.get(i).setCheckBoxState(ViewHolder.checkBox.isChecked());
                ViewHolder.checkBox.setChecked(ViewHolder.checkBox.isChecked());
            }
        });

        // imageview 버튼을 누르면 checkbox도 체크.
        ViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkboxVis){
                    urlDataList.get(i).setCheckBoxState(!urlDataList.get(i).getCheckBoxState()); //urldataList의 i번째 boolean state가 false이면
                    ViewHolder.checkBox.setChecked(urlDataList.get(i).getCheckBoxState());
                }
                else{
                    Intent intent = new Intent(context, PictureDetailActivity.class);
                    intent.putExtra("url", urlDataList);
                    intent.putExtra("position", i);
                    context.startActivity(intent);
                }
            }
        });
    }

    // 데이터의 크기를 리턴
    @Override
    public int getItemCount() {
        return urlDataList.size();
    }

    // v에 존재하는 위젯들을 바인딩.
    class ImgViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public CheckBox checkBox;
        public ImgViewHolder(View v){
            super(v);
            imageView = (ImageView) v.findViewById(R.id.ImageView);
            checkBox = (CheckBox) v.findViewById(R.id.checkbox);
        }
    }

    public void settingClicked(){
        if(checkboxVis == false)
            checkboxVis = true;
        else
            checkboxVis = false;

        notifyDataSetChanged();
    }

    // 추가버튼 눌렀을 때 이벤트
    public void saveClicked(){
        for(int j=0; j<urlDataList.size(); j++){





            /*if(urlDataList.get(j).getCheckBoxState())
                Log.d("몇번째가 체크됬어?", ""+j);*/
        }
    }

    // 체크된거 리셋하기
    public void setCheckedNull(){
        for(int j=0; j<urlDataList.size(); j++){
            urlDataList.get(j).setCheckBoxState(false);
        }
    }
}
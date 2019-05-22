package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.GrayscaleTransformation;

import static com.example.myapplication.MainActivity.urlDataList;

public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.ImgViewHolder> {

    public Context context;
    public boolean checkboxVis = false;
    private int page = 0;
    private String FileName;
    ImgViewHolder viewHolder;
    private int AdapterPosition;

    public RecyclerviewAdapter( Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ImgViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recyclerview, viewGroup, false);
        return new ImgViewHolder(view);
    }

    //View의 내용을 해당 포지션의 데이터로 바꾼다.
    @Override
    public void onBindViewHolder(@NonNull ImgViewHolder ViewHolder, int i) {
        this.viewHolder = ViewHolder;
        ViewHolder.checkBox.setChecked(urlDataList.get(i).getCheckBoxState());
        urlDataList.get(i).setURLNo(i);

        //FileName 정하기 (split으로 url이름 잘라서)
        Log.d("FileName",urlDataList.get(i).getURL());
        FileName = urlDataList.get(i).getURL().split("-picture")[0];
        FileName = FileName.split("/photos")[1];

        if(FileName.equals("/"))
            FileName = "Non-name";
        else
            FileName = FileName.split("/")[1];

        urlDataList.get(i).setFileName(FileName);


        Log.d("adsf",""+((ImgViewHolder)ViewHolder).imageView.getMeasuredHeight());

        if (checkboxVis) {
            ViewHolder.checkBox.setVisibility(View.VISIBLE);
        } else {
            ViewHolder.checkBox.setVisibility(View.GONE);
        }

        ViewHolder.imageView.getHeight();
        if(urlDataList.get(i).getHaveSeen()) {
            if (ViewHolder.imageView.getMeasuredHeight() == 0)
                Glide.with(context)
                        .load(urlDataList.get(i).getURL())
                        .bitmapTransform(new GrayscaleTransformation(context))
                        .into(ViewHolder.imageView);
            else
                Glide.with(context)
                        .load(urlDataList.get(i).getURL())
                        .bitmapTransform(new GrayscaleTransformation(context))
                        .override(ViewHolder.imageView.getMeasuredWidth(), ViewHolder.imageView.getMeasuredHeight())
                        .into(ViewHolder.imageView);
        }

        else{
            if (ViewHolder.imageView.getMeasuredHeight() == 0)
                Glide.with(context)
                        .load(urlDataList.get(i).getURL())
                        .into(ViewHolder.imageView);
            else
                Glide.with(context)
                        .load(urlDataList.get(i).getURL())
                        .override(ViewHolder.imageView.getMeasuredWidth(), ViewHolder.imageView.getMeasuredHeight())
                        .into(ViewHolder.imageView);
        }

    }

    // 데이터의 크기를 리턴
    @Override
    public int getItemCount() {
        return urlDataList.size();
    }

    // v에 존재하는 위젯들을 바인딩.
    class ImgViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public CheckBox checkBox;
        public ImgViewHolder(View v) {
            super(v);

            imageView = (ImageView) v.findViewById(R.id.ImageView);
            checkBox = (CheckBox) v.findViewById(R.id.checkbox);

            AdapterPosition = getAdapterPosition();

            Log.d("ImgViewHolder called","ImgViewHolder called");

            checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            // RecyclerviewAdapter -> setOnCheckedChangeListener를 사용하면
            // notify를 하면 원래 체크박스들을 uncheck함
            // 그래서 그냥 setOnClickListener를 사용 함.
            public void onClick(View v) {
                urlDataList.get(getAdapterPosition()).setCheckBoxState(checkBox.isChecked());
                checkBox.setChecked(checkBox.isChecked());
            }
        });

        // imageview 버튼을 누르면 checkbox도 체크.
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                      if (checkboxVis) {
                          urlDataList.get(getAdapterPosition()).setCheckBoxState(!urlDataList.get(getAdapterPosition()).getCheckBoxState()); //urldataList의 i번째 boolean state가 false이면
                          checkBox.setChecked(urlDataList.get(getAdapterPosition()).getCheckBoxState());
                     } else {
                        Intent intent = new Intent(context, PictureDetailActivity.class);
                        intent.putExtra("url", urlDataList);
                        intent.putExtra("position", getAdapterPosition());
                        intent.putExtra("page", page);
                        ((Activity) context).startActivityForResult(intent, 3000);
                     }
                 }
                });
        }
    }


    public void settingClicked() {
        if (checkboxVis == false)
            checkboxVis = true;
        else
            checkboxVis = false;

        notifyDataSetChanged();
    }

    // 추가버튼 눌렀을 때 이벤트
    public void saveClicked() {

    }

    // 체크된거 리셋하기
    public void setCheckedNull() {
        for (int j = 0; j < urlDataList.size(); j++) {
            urlDataList.get(j).setCheckBoxState(false);
        }
    }

    public void setPage(int page) {
        this.page = page;
    }
}
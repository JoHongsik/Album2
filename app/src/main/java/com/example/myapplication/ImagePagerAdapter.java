package com.example.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Picture;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import java.util.ArrayList;

public class ImagePagerAdapter extends PagerAdapter {
    private Context context;
    private ImageView imageView;
    private ArrayList<URLData> urlDataList;
    private int urlposition;


    public ImagePagerAdapter(Context context,ArrayList<URLData> urlDataList, int position ){
        this.context = context;
        this.urlDataList = urlDataList;
        this.urlposition = position;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = null;

        if (context != null) {
            // LayoutInflater를 통해 "/res/layout/page.xml"을 뷰로 생성.
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.activity_picture_detail, container, false);

            imageView = (ImageView) view.findViewById(R.id.detailImage2);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkVisibility();
                }
            });

            // viewpage에서 보고있는곳으로 scroll
            MainActivity.recyclerView.smoothScrollToPosition(position);

            // 만약 urlDataList.size()가 position과 같으면 viewpager, recyclerview 업데이트.
            Glide.with(context)
                    //.load(urlDataList.get(urlposition + position).getURL())
                    .load(urlDataList.get(position).getURL())
                    .placeholder(new ColorDrawable(Color.BLACK))
                    .into(imageView);
        }

        // 뷰페이저에 추가.
        container.addView(view);

        return view;
    }

    @Override
    public int getCount() {
        return urlDataList.size();

        /*if(urlDataList.size()-urlposition>10)   // size가 60일 때, 51(50)번째 사진까지만 viewpager 크기를 10
            return 10;
        else
            return urlDataList.size()-urlposition;  //size가 60일 때, 52(51)번째 사진일 경우 viewpager 크기를 9*/
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return (view == (View)o);
    }

    @Override
    public void destroyItem(View container, int position, Object object){
        ((ViewPager)container).removeView((View)object);
    }

    public void checkVisibility(){
        if(PictureDetail.close_btn.getVisibility() == View.VISIBLE) {
            PictureDetail.close_btn.setVisibility(View.INVISIBLE);
            PictureDetail.share_btn.setVisibility(View.INVISIBLE);
            PictureDetail.save_btn.setVisibility(View.INVISIBLE);
        }
        else{
            PictureDetail.close_btn.setVisibility(View.VISIBLE);
            PictureDetail.share_btn.setVisibility(View.VISIBLE);
            PictureDetail.save_btn.setVisibility(View.VISIBLE);
        }
    }
}

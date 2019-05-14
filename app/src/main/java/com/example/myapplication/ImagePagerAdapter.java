package com.example.myapplication;

import android.content.Context;
import android.graphics.Color;
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

import static com.example.myapplication.MainActivity.urlDataList;


public class ImagePagerAdapter extends PagerAdapter {
    private Context context;
    private ImageView imageView;

    private int urlposition;
    private OnPictureAreaClickedListener listener;
    private TextView filenametxt;

    public ImagePagerAdapter(Context context, int position){
        this.context = context;
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
            filenametxt = (TextView) view.findViewById(R.id.filename_txt);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(listener != null) {
                        Log.d("ImageView","setOnClickListener");
                        listener.onPictureAreaClicked(position);
                    }
                }
            });

            // 만약 urlDataList.size()가 position과 같으면 viewpager, recyclerview 업데이트.
            Glide.with(context)
                    .load(urlDataList.get(position).getURL())
                    .placeholder(new ColorDrawable(Color.BLACK))
                    .into(imageView);

            filenametxt.setText(urlDataList.get(position).getFileName());

        }

        // 뷰페이저에 추가.
        container.addView(view);

        return view;
    }

    @Override
    public int getCount() {
        return urlDataList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return (view == (View)o);
    }

    @Override
    public void destroyItem(View container, int position, Object object){
        ((ViewPager)container).removeView((View)object);
    }

    // listener는 PictureDetailActivity
    public void setOnPictureAreaClickedListener (OnPictureAreaClickedListener listener) {
        this.listener = listener;
    }


}

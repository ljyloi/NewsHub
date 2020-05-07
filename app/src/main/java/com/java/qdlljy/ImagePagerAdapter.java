package com.java.qdlljy;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;

import java.util.List;

public class ImagePagerAdapter extends PagerAdapter {
    private List<String> imageList;

    private Context context;

    public ImagePagerAdapter(Context context, List<String> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return imageList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem (ViewGroup container, int position) {
        MyImageView imageView = new MyImageView(context);
        Glide.with(context).load(imageList.get(position)).into(imageView);
//        imageView.setImageURL(imageList.get(position));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        container.addView(imageView);
        Log.d("here", "instantiateItem: succeed");
        return imageView;
    }

    @Override
    public void destroyItem (ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }
}


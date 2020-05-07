package com.java.qdlljy;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.java.qdlljy.NewsUtil.News;
import com.java.qdlljy.util.QrcodeGenerator;
import com.java.qdlljy.R;

public class ShareView extends FrameLayout {
    private final int IMAGE_WIDTH = 720;
    private final int IMAGE_HEIGHT = 1280;

    private ImageView myimage;
    private TextView mytitle;
    private TextView mycontent;
    private ImageView myqrcode;

    public ShareView(Context context) {
        super(context);
        init();
    }


    private void init() {
        View layout = View.inflate(getContext(), R.layout.share_view_layout, this);
        mytitle = (TextView) layout.findViewById(R.id.share_title);
        mycontent = (TextView) layout.findViewById(R.id.share_content);
        myimage = (ImageView) layout.findViewById((R.id.share_image));
        myqrcode = (ImageView) layout.findViewById(R.id.qrcode);
    }
    public void setInfo(News news, final Share share) {
        mytitle.setText(news.title);
        mycontent.setText(news.content);
        myqrcode.setImageBitmap(QrcodeGenerator.createQRCodeBitmap(news.newsURL, 150, 150, "UTF-8", "H", "0", Color.BLACK, Color.WHITE ));

        if (news.Images.size() > 0) {
//            myimage.setImageResource(R.drawable.icon);
//            Glide.with(this).load(news.Images.get(0)).into(myimage);
//            Glide.with(this).load(news.Images.get(0)).into(myimage);
            Glide.with(this)
                    .asBitmap()
                    .load(news.Images.get(0))
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            myimage.setImageBitmap(resource);
                            share.imageUri = share.saveImage(createImage());
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable placeholder) {
                            myimage.setImageResource(R.drawable.icon_horizontal);
                            share.imageUri = share.saveImage(createImage());
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });
//            Glide.with(this).asBitmap().load(news.Images.get(0)).listener(new RequestListener<BitmapDrawable>() {
//                @Override
//                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<BitmapDrawable> target, boolean isFirstResource) {
//                    myimage.setImageResource(R.drawable.icon_horizontal);
//
//                    share.imageUri = share.saveImage(createImage());
////                    final Bitmap image = createImage();
//////                    Log.d("fuck you", "createShareImage: ");
////                    final File path = share.saveImage(image);
//////        Log.d("share", path);
////                    share.shareSingleImage(path);
////                    if (image != null && !image.isRecycled()) {
////                        image.recycle();
////                    }
//                    return false;
//                }
//
//                @Override
//                public boolean onResourceReady(Drawable resource, Object model, Target<BitmapDrawable> target, DataSource dataSource, boolean isFirstResource) {
//
//                    //if you want to convert the drawable to ImageView
//                    Bitmap bitmapImage  = ((BitmapDrawable) resource).getBitmap();
//                    myimage.setImageBitmap(bitmapImage);
//                    share.imageUri = share.saveImage(createImage());
////                    final Bitmap image = createImage();
////                    final File path = share.saveImage(image);
////                    share.shareSingleImage(path);
////                    if (image != null && !image.isRecycled()) {
////                        image.recycle();
////                    }
//
//                    return true;
//                }
//            }).into(myimage);

        }
        else {
            myimage.setImageResource(R.drawable.icon_horizontal);

            share.imageUri = share.saveImage(createImage());
//            final Bitmap image = createImage();
//            final File path = share.saveImage(image);
//            share.shareSingleImage(path);
//            if (image != null && !image.isRecycled()) {
//                image.recycle();
//            }
        }
    }

    public Bitmap createImage() {
        int widthMeasureSpec = MeasureSpec.makeMeasureSpec(IMAGE_WIDTH, MeasureSpec.EXACTLY);
        int heightMeasureSpec = MeasureSpec.makeMeasureSpec(IMAGE_HEIGHT, MeasureSpec.EXACTLY);

        measure(widthMeasureSpec, heightMeasureSpec);
        layout(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
        Bitmap bitmap = Bitmap.createBitmap(IMAGE_WIDTH, IMAGE_HEIGHT, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        draw(canvas);

        return bitmap;
    }
}

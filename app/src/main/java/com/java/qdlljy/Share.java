package com.java.qdlljy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.core.content.FileProvider;

import com.java.qdlljy.NewsUtil.News;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Share {
    private Context context;
    public Uri imageUri;
    Share(Context context) {
        this.context = context;
    }
    public void createShareImage(Context context, News news) {
        ShareView shareView = new ShareView(context);
        shareView.setInfo(news, this);
//        imageUri = saveImage(shareView.createImage());
    }

    public Uri saveImage(Bitmap bitmap) {
        String filename = "shareImage.png";
        File path = context.getCacheDir();
        File file = new File(path, filename);
        if (file.exists())
            file.delete();
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        Log.d("fuck me", "saveImage: " + file.getAbsolutePath());
        return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID+".provider", file);
    }

    public void shareSingleImage() {
//        Log.d(path, "shareSingleImage: " + path);
        //由文件得到uri
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("image/*");
        context.startActivity(Intent.createChooser(shareIntent, "分享到"));
    }
}
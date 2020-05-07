package com.java.qdlljy.NewsUtil;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DataOperator {
    private NewsDataHelper datahelper;
    private SQLiteDatabase db;

    public DataOperator(Context context)
    {
        datahelper = new NewsDataHelper(context, "NewsDatabase", null, 1);
        db = datahelper.getWritableDatabase();
    }

    public void Insert(News news)
    {
        db.execSQL("insert or replace into News(newsID, title, content, publisher, publishTime, " +
                "category, imageURL, videoURL, keyword, newsURL, isCollect) values(?,?,?,?,?,?,?,?,?,?,?)",
                new Object[]{ news.newsID, news.title, news.content, news.publisher, news.publishTime,
                              news.category, news.imageURL, news.videoURL, news.KeywordString, news.newsURL, news.isCollect});
    }

    public void DeleteID(String newsID)
    {
        db.execSQL("delete from News where newsID=?", new String[]{newsID});
    }

    public void DeleteNews(News news)
    {
        this.DeleteID(news.newsID);
    }

    public void DeleteAll()
    {
        db.execSQL("delete from News");
    }

    public boolean isExit(String newsID) {
        Cursor ret = db.rawQuery("select * from News where newsID= ?", new String[]{newsID});
        if (ret.moveToFirst() == false) return false;
        return true;
    }

    public boolean isCollect(String newsID) {
        Cursor ret = db.rawQuery("select * from News where newsID= ?", new String[]{newsID});
        if (ret.moveToFirst() == false) return false;
        int t = ret.getInt(10);
        return t > 0;
    }

    public int getSize()
    {
        Cursor cursor = db.rawQuery("select count(*)  from News",null);
        cursor.moveToFirst();
        long retSize = cursor.getLong(0);
        cursor.close();
        return (int)retSize;
    }

    public News GetNews(String newsID)
    {
        News tnews = new News();
        Cursor ret = db.rawQuery("select * from News where newsID= ?", new String[]{newsID});
        if (ret.moveToFirst() == false) {
            tnews.newsID = "";
            return tnews;
        }
        tnews.newsID = ret.getString(0);
        tnews.title = ret.getString(1);
        tnews.content = ret.getString(2);
        tnews.publisher = ret.getString(3);
        tnews.publishTime = ret.getString(4);
        tnews.category = ret.getString(5);

        tnews.imageURL = ret.getString(6);
        String[] ad = tnews.imageURL.split(",");
        for (int j = 0; j < ad.length; ++j) if (ad[j].length() > 5) tnews.Images.add(ad[j]);

        tnews.videoURL = ret.getString(7);

        String tmps = ret.getString(8);
        tnews.KeywordString = tmps;
        String[] bd = tmps.split(",");
        for (int j = 0; j < bd.length; ++j) tnews.keywords.add(bd[j]);

        tnews.newsURL = ret.getString(9);

        tnews.isCollect = ret.getInt(10);
        tnews.isRead = 1;
        return tnews;
    }

    public List<News> GetAllNews()
    {
        List<News> retlist = new ArrayList<>();
        Cursor ret = db.rawQuery("select * from News order by publishTime",null);
        while (ret.moveToNext())
        {
            News tnews = new News();
            tnews.newsID = ret.getString(0);
            tnews.title = ret.getString(1);
            tnews.content = ret.getString(2);
            tnews.publisher = ret.getString(3);
            tnews.publishTime = ret.getString(4);
            tnews.category = ret.getString(5);

            tnews.imageURL = ret.getString(6);
            String[] ad = tnews.imageURL.split(",");
            for (int j = 0; j < ad.length; ++j) if (ad[j].length() > 5) tnews.Images.add(ad[j]);

            tnews.videoURL = ret.getString(7);

            String tmps = ret.getString(8);
            tnews.KeywordString = tmps;
            String[] bd = tmps.split(",");
            for (int j = 0; j < bd.length; ++j) tnews.keywords.add(bd[j]);

            tnews.newsURL = ret.getString(9);
            tnews.isCollect = ret.getInt(10);
            tnews.isRead = 1;
            retlist.add(tnews);
        }
        return retlist;
    }

    public List<News> FindCategory(String cate)
    {
        List<News> retlist = new ArrayList<>();
        Cursor ret = db.rawQuery("select * from News where category=?",new String[]{cate});
        while (ret.moveToNext())
        {
            News tnews = new News();
            tnews.newsID = ret.getString(0);
            tnews.title = ret.getString(1);
            tnews.content = ret.getString(2);
            tnews.publisher = ret.getString(3);
            tnews.publishTime = ret.getString(4);
            tnews.category = ret.getString(5);

            tnews.imageURL = ret.getString(6);
            String[] ad = tnews.imageURL.split(",");
            for (int j = 0; j < ad.length; ++j) if (ad[j].length() > 5) tnews.Images.add(ad[j]);

            tnews.videoURL = ret.getString(7);

            String tmps = ret.getString(8);
            tnews.KeywordString = tmps;
            String[] bd = tmps.split(",");
            for (int j = 0; j < bd.length; ++j) tnews.keywords.add(bd[j]);

            tnews.newsURL = ret.getString(9);
            tnews.isCollect = ret.getInt(10);
            tnews.isRead = 1;
            retlist.add(tnews);
        }
        return retlist;
    }

    public List<News> GetCollectedNews()
    {
        List<News> retlist = new ArrayList<>();
        Cursor ret = db.rawQuery("select * from News where isCollect=? order by publishTime",new String[]{"1"});
        while (ret.moveToNext())
        {
            News tnews = new News();
            tnews.newsID = ret.getString(0);
            tnews.title = ret.getString(1);
            tnews.content = ret.getString(2);
            tnews.publisher = ret.getString(3);
            tnews.publishTime = ret.getString(4);
            tnews.category = ret.getString(5);

            tnews.imageURL = ret.getString(6);
            String[] ad = tnews.imageURL.split(",");
            for (int j = 0; j < ad.length; ++j) if (ad[j].length() > 5) tnews.Images.add(ad[j]);

            tnews.videoURL = ret.getString(7);

            String tmps = ret.getString(8);
            tnews.KeywordString = tmps;
            String[] bd = tmps.split(",");
            for (int j = 0; j < bd.length; ++j) tnews.keywords.add(bd[j]);

            tnews.newsURL = ret.getString(9);
            tnews.isCollect = ret.getInt(10);
            tnews.isRead = 1;
            retlist.add(tnews);
        }
        return retlist;
    }

    public List<News> GetNewsWithCnt(int cnt)
    {
        List<News> retlist = new ArrayList<>();
        int tot = 0;
        Cursor ret = db.rawQuery("select * from News order by publishTime desc",null);
        while (ret.moveToNext())
        {
            News tnews = new News();
            tnews.newsID = ret.getString(0);
            tnews.title = ret.getString(1);
            tnews.content = ret.getString(2);
            tnews.publisher = ret.getString(3);
            tnews.publishTime = ret.getString(4);
            tnews.category = ret.getString(5);

            tnews.imageURL = ret.getString(6);
            String[] ad = tnews.imageURL.split(",");
            for (int j = 0; j < ad.length; ++j) if (ad[j].length() > 5) tnews.Images.add(ad[j]);

            tnews.videoURL = ret.getString(7);

            String tmps = ret.getString(8);
            tnews.KeywordString = tmps;
            String[] bd = tmps.split(",");
            for (int j = 0; j < bd.length; ++j) tnews.keywords.add(bd[j]);

            tnews.newsURL = ret.getString(9);
            tnews.isCollect = ret.getInt(10);
            tnews.isRead = 1;
            retlist.add(tnews);
            if (++tot >= cnt) break;
        }
        return retlist;
    }


    public void DeleteAllReadNews(){
        db.execSQL("delete from News where isCollect = ?", new String[]{"0"});
    }
}
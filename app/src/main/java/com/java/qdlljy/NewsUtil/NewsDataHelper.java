package com.java.qdlljy.NewsUtil;

import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;


public class NewsDataHelper extends SQLiteOpenHelper {
    public static final String CREATE_DB = "create table News("
                + "newsID text primary key,"
                +"title text,"
                +"content text,"
                +"publisher text,"
                +"publishTime text,"
                +"category text,"
                +"imageURL text,"
                +"videoURL text,"
                +"keyword text,"
                +"newsURL text,"
                +"isCollect integer)";

    public NewsDataHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
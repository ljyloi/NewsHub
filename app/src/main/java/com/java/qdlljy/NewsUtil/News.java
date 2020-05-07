package com.java.qdlljy.NewsUtil;

import java.io.Serializable;
import java.util.*;

public class News implements Serializable {
    public String newsID;
    public String title;
    public String content;
    public String publisher;
    public String publishTime;
    public String category;
    public String imageURL;
    public String videoURL;
    public String KeywordString;
    public List<String> keywords;
    public List<String> Images;
    public int isRead;
    public int isCollect;
    public String newsURL;

    public News()
    {
        this.isRead = 0;
        this.isCollect = 0;
        keywords = new ArrayList<>();
        Images = new ArrayList<>();
    }

    public String getSummary() {
        StringBuilder sum = new StringBuilder();
        for (int i = 0; i < content.length(); ++i) {
            sum.append(content.charAt(i));
            if (i == 100) break;
        }
        return sum.toString();
    }

    public void Read()
    {
        this.isRead = 1;
    }
    public void Collect() {this.isCollect = 1;}
    public void UnCollect() {this.isCollect = 0;}

    public boolean isImageEmpty()
    {
        return this.imageURL.length() <= 2;
    }

    public boolean isVideoEmpty()
    {
        return this.videoURL.length() <= 2;
    }

    public boolean equals(Object o){
        if(this == o) return true;
        if(o instanceof News){
            News student = (News) o;
            if(student.newsID.equals(this.newsID)) return true;
        }

        return false;
    }

    public int hashCode(){
        return this.newsID.hashCode();
    }


}
package com.java.qdlljy.NewsUtil;

import android.content.Context;
import android.icu.text.SimpleDateFormat;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.*;
import java.io.*;
import java.net.URLEncoder;

public class NewsOperator {

    //public HashSet<News> AllNews;
    public HashMap<String, String> timeMap;

    public DataOperator DB;

    public List<News> AllLocalNews;
    public int Localpos;
    public int Collectpos;

    //what can be in hashMap
    public String[] cates = {"","娱乐","军事","教育","文化","健康","财经","体育","汽车","科技","社会","搜索","推荐"};

    public NewsOperator(Context context) {

        //AllNews = new HashSet<>();
        timeMap = new HashMap<>();
        DB = new DataOperator(context);
        //Log.d("badwoman",DB_Size+"");
        AllLocalNews = DB.GetAllNews();
        Collectpos = Localpos = AllLocalNews.size() - 1;
    }

    public boolean isChineseChar(char c) {
        return String.valueOf(c).matches("[\u4e00-\u9fa5]");
    }

    public String Zhuan(String ch)
    {
        try
        {
            String tmpS = "";
            for (int i = 0; i < ch.length(); ++i) {
                char c = ch.charAt(i);
                if (isChineseChar(c)) {
                    String encode = URLEncoder.encode(c + "", "UTF-8");
                    tmpS = tmpS + encode;
                } else tmpS = tmpS + c;
            }
            return tmpS;
        } catch (UnsupportedEncodingException e)
        {
            return "";
        }
    }

    public void ReadNews(News news) {
       if (AllLocalNews.contains(news)) return;
        news.Read();
        DB.Insert(news);
        AllLocalNews.add(news);
    }

    public void CollectNews(News news) {
        //if (news.isCollect == 1) return;
        int pd = AllLocalNews.lastIndexOf(news);
        if (pd != -1) AllLocalNews.get(pd).Collect();
        news.Read();
        news.Collect();
        DB.Insert(news);
    }

    public void UnCollectNews(News news) {
        //if (news.isCollect == 0) return;
        int pd = AllLocalNews.lastIndexOf(news);
        if (pd != -1) AllLocalNews.get(pd).UnCollect();
        news.UnCollect();
        DB.Insert(news);
    }

    public void DeleteNews(News news) {
        //AllNews.remove(news);
        if (news.isRead == 1) DB.DeleteNews(news);
        AllLocalNews.remove(news);
    }

    /*
    public List<News> GetMemNews(String category)
    {
        List<News> retNews = new ArrayList<>();
        for (News news: AllNews)
        {
            if (news.category.equals(category)) retNews.add(news);
        }
        return retNews;
    }
    */

    public JsonObject ToJsonObject(String text)
    {
        JsonParser parse =new JsonParser();
        JsonObject json = (JsonObject) parse.parse(text);
        return json;
    }

    public List<News> Refresh(String category, String jsonStr)
    {
        List<News> retNews = new ArrayList<>();

        JsonObject json =ToJsonObject(jsonStr);
        JsonArray newslist = json.get("data").getAsJsonArray();
        for (int i = 0; i < newslist.size(); ++i) {
                JsonObject tmpJson = newslist.get(i).getAsJsonObject();
                News tmpNews = new News();
                tmpNews.newsID = tmpJson.get("newsID").getAsString();
                tmpNews.title = tmpJson.get("title").getAsString();
                tmpNews.content = tmpJson.get("content").getAsString();
                tmpNews.publisher = tmpJson.get("publisher").getAsString();
                tmpNews.publishTime = tmpJson.get("publishTime").getAsString();
                tmpNews.category = tmpJson.get("category").getAsString();
                tmpNews.newsURL = tmpJson.get("url").getAsString();

                String iurl = tmpJson.get("image").getAsString();
                            //System.out.println(iurl);
                iurl = iurl.replaceAll("\\[", "");
                iurl = iurl.replaceAll("]", "");
                iurl = iurl.replaceAll("\\s", "");
                tmpNews.imageURL = iurl;
                String[] ad = iurl.split(",");
                for (int j = 0; j < ad.length; ++j) if (ad[j].length() > 5) tmpNews.Images.add(ad[j]);

                    tmpNews.videoURL = tmpJson.get("video").getAsString();

                    List<String> keys = new ArrayList<String>();
                    String KS = "";
                    JsonArray keylist = tmpJson.get("keywords").getAsJsonArray();
                    for (int j = 0; j < keylist.size(); ++j) {
                        JsonObject keyJson = keylist.get(j).getAsJsonObject();
                        String ch = keyJson.get("word").getAsString();
                        keys.add(ch);
                        KS = KS + ch + ",";
                    }
                    tmpNews.keywords = keys;
                    tmpNews.KeywordString = KS;
                    //if (AllNews.contains(tmpNews)) continue;
                    retNews.add(tmpNews);
            }

        for (int i = 0; i < retNews.size(); ++i) {
            News tmp = retNews.get(i);
            if (DB.isExit(tmp.newsID)) {
                retNews.get(i).Read();
            }
            if (DB.isCollect(tmp.newsID)) retNews.get(i).Collect();
        }
        //for (int i = 0; i < retNews.size(); ++i) AllNews.add(retNews.get(i));
        if (retNews.size() > 0) {
            String baddate = retNews.get(retNews.size()-1).publishTime;
            SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            try {
                Date da = simple.parse(baddate);
                da.setTime(da.getTime() - 1000);
                timeMap.put(category,simple.format(da));
            } catch (Exception e) {

            }
        }
        return retNews;
    }

    public List<News> LoadMore(String category, String jsonStr)
    {
        String gtime = timeMap.get(category);
        List<News> retNews = new ArrayList<>();

        JsonObject json = ToJsonObject(jsonStr);
        JsonArray newslist = json.get("data").getAsJsonArray();
        if (newslist.size() > 0) {

            for (int i = 0; i < newslist.size(); ++i) {
                JsonObject tmpJson = newslist.get(i).getAsJsonObject();
                News tmpNews = new News();
                tmpNews.newsID = tmpJson.get("newsID").getAsString();
                tmpNews.title = tmpJson.get("title").getAsString();
                tmpNews.content = tmpJson.get("content").getAsString();
                tmpNews.publisher = tmpJson.get("publisher").getAsString();
                tmpNews.publishTime = tmpJson.get("publishTime").getAsString();
                tmpNews.category = tmpJson.get("category").getAsString();
                tmpNews.newsURL = tmpJson.get("url").getAsString();

                //if(gtime.equals(tmpNews.publishTime)) continue;

                String iurl = tmpJson.get("image").getAsString();
                                //System.out.println(iurl);
                iurl = iurl.replaceAll("\\[", "");
                iurl = iurl.replaceAll("]", "");
                iurl = iurl.replaceAll("\\s", "");
                tmpNews.imageURL = iurl;
                String[] ad = iurl.split(",");
                for (int j = 0; j < ad.length; ++j) if (ad[j].length() > 5) tmpNews.Images.add(ad[j]);
                                //System.out.println("\n"+iurl+"\n---------------------\n");

                tmpNews.videoURL = tmpJson.get("video").getAsString();

                List<String> keys = new ArrayList<String>();
                String KS = "";
                JsonArray keylist = tmpJson.get("keywords").getAsJsonArray();
                for (int j = 0; j < keylist.size(); ++j) {
                    JsonObject keyJson = keylist.get(j).getAsJsonObject();
                    String ch = keyJson.get("word").getAsString();
                    keys.add(ch);
                    KS = KS + ch + ",";
                }
                tmpNews.keywords = keys;
                tmpNews.KeywordString = KS;

                retNews.add(tmpNews);
                //if (retNews.size() == 5) break;
            }
        }



        for (int i = 0; i < retNews.size(); ++i) {
            News tmp = retNews.get(i);
            if (DB.isExit(tmp.newsID)) {
                retNews.get(i).Read();
            }
            if (DB.isCollect(tmp.newsID)) retNews.get(i).Collect();
        }
        //for (int i = 0; i < retNews.size(); ++i) AllNews.add(retNews.get(i));

        if (retNews.size() > 0) {
            String baddate = retNews.get(retNews.size()-1).publishTime;
            SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            try {
                Date da = simple.parse(baddate);
                da.setTime(da.getTime() - 1000);
                timeMap.put(category,simple.format(da));
            } catch (Exception e) {

            }
        }
        //System.out.println(debugInt+": I'm so so Bad!!!!!!  "+retNews.size());
        return retNews;
    }

    public List<News> GetDatabaseCollect()
    {
        List<News> retNews = DB.GetCollectedNews();
        return retNews;
    }

    /*
    public List<News> GetMemCollect()
    {
        List<News> retNews = new ArrayList<>();
        for (News news: AllNews)
        {
            if (news.isCollect == 1) retNews.add(news);
        }
        return retNews;
    }
    */


    public List<News> Search(String word, String jsonStr)
    {
        final List<News> retNews = new ArrayList<>();

        JsonObject json = ToJsonObject(jsonStr);
        JsonArray newslist = json.get("data").getAsJsonArray();
        for (int i = 0; i < newslist.size(); ++i) {
            JsonObject tmpJson = newslist.get(i).getAsJsonObject();
            News tmpNews = new News();
            tmpNews.newsID = tmpJson.get("newsID").getAsString();
            tmpNews.title = tmpJson.get("title").getAsString();
            tmpNews.content = tmpJson.get("content").getAsString();
            tmpNews.publisher = tmpJson.get("publisher").getAsString();
            tmpNews.publishTime = tmpJson.get("publishTime").getAsString();
            tmpNews.category = tmpJson.get("category").getAsString();
            tmpNews.newsURL = tmpJson.get("url").getAsString();

            String iurl = tmpJson.get("image").getAsString();
                            //System.out.println(iurl);
            iurl = iurl.replaceAll("\\[", "");
            iurl = iurl.replaceAll("]", "");
            iurl = iurl.replaceAll("\\s", "");
            tmpNews.imageURL = iurl;
            String[] ad = iurl.split(",");
            for (int j = 0; j < ad.length; ++j) if(ad[j].length() > 5) tmpNews.Images.add(ad[j]);
                            //System.out.println("\n"+iurl+"\n---------------------\n");

            tmpNews.videoURL = tmpJson.get("video").getAsString();

            List<String> keys = new ArrayList<String>();
            String KS = "";
            JsonArray keylist = tmpJson.get("keywords").getAsJsonArray();
            for (int j = 0; j < keylist.size(); ++j) {
                JsonObject keyJson = keylist.get(j).getAsJsonObject();
                String ch = keyJson.get("word").getAsString();
                keys.add(ch);
                KS = KS + ch + ",";
            }
            tmpNews.keywords = keys;
            tmpNews.KeywordString = KS;
            retNews.add(tmpNews);

        }


        for (int i = 0; i < retNews.size(); ++i) {
            News tmp = retNews.get(i);
            if (DB.isExit(tmp.newsID)) {
                retNews.get(i).Read();
            }
            if (DB.isCollect(tmp.newsID)) retNews.get(i).Collect();
        }
        //for (int i = 0; i < retNews.size(); ++i) AllNews.add(retNews.get(i));
        if (retNews.size() > 0) {
            String baddate = retNews.get(retNews.size()-1).publishTime;
            SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            try {
                Date da = simple.parse(baddate);
                da.setTime(da.getTime() - 1000);
                timeMap.put("搜索",simple.format(da));
            } catch (Exception e) {

            }
        }
        //System.out.println(debugInt+": I'm so so Bad!!!!!!  "+retNews.size());
        return retNews;
    }


    public List<News> LocalNews(String category) {
        return DB.FindCategory(category);
    }

    public List<News> GetNewsWithCnt(int cnt)
    {
        return DB.GetNewsWithCnt(cnt);
    }

    public List<News> RefreshRead() {
        List<News> retlist = new ArrayList<>();
        Localpos = AllLocalNews.size() - 1;
        if (Localpos < 0) return retlist;
        for (int i = Localpos; i >= Math.max(Localpos-4, 0); --i) retlist.add(AllLocalNews.get(i));
        Localpos -= 5;
        return retlist;
    }

    public List<News> LoadMoreRead()
    {
        List<News> retlist = new ArrayList<>();
        if (Localpos < 0) return retlist;
        for (int i = Localpos; i >= Math.max(Localpos-4, 0); --i) retlist.add(AllLocalNews.get(i));
        Localpos -= 5;
        return retlist;
    }

    public List<News> RefreshCollected()
    {
        List<News> retlist = new ArrayList<>();
        Collectpos = AllLocalNews.size() - 1;
        if (Collectpos < 0) return retlist;
        int tot = 0;
        for (int i = Collectpos; i >= 0; --i) {
            News tmp = AllLocalNews.get(i);
            if (tmp.isCollect == 0) continue;
            retlist.add(tmp);
            Collectpos = i;
            if (++tot == 5) break;
        }
        Collectpos -= 1;
        return retlist;
    }

    public List<News> LoadMoreCollected()
    {
        List<News> retlist = new ArrayList<>();
        if (Collectpos < 0) return retlist;
        int tot = 0;
        for (int i = Collectpos; i >= 0; --i) {
            News tmp = AllLocalNews.get(i);
            if (tmp.isCollect == 0) continue;
            retlist.add(tmp);
            Collectpos = i;
            if (++tot == 5) break;
        }
        Collectpos -= 1;
        return retlist;
    }

    public void DeleteAllReadNews() {
        DB.DeleteAllReadNews();
        List<News> tmplist = new ArrayList<>();
        for (int i = 0; i < AllLocalNews.size(); ++i) {
            News tmp = AllLocalNews.get(i);
            if (tmp.isCollect == 1) tmplist.add(tmp);
        }
        AllLocalNews = tmplist;
    }
}
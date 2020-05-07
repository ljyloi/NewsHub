/*
 * MIT License
 *
 * Copyright (c) 2018 Soojeong Shin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.java.qdlljy.MyFragments;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.java.qdlljy.EmptyRecyclerView;
import com.java.qdlljy.MyApplication;
import com.java.qdlljy.NewsAdapter;
import com.java.qdlljy.NewsUtil.News;
import com.java.qdlljy.R;
import com.scwang.smartrefresh.header.DeliveryHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Query;


public class MyArticlesFragment extends Fragment {
    private List<News> newsList = new ArrayList<>();
//    private static final String LOG_TAG = BaseArticlesFragment.class.getName();

    private String myCategory, myTitle;
    private int idFragment;
    private NewsAdapter myAdapter;

    private TextView mEmptyStateTextView;


    private View mLoadingIndicator;

    private SmartRefreshLayout refreshLayout;

    interface NewsApi{
        @GET("queryNewsList")
        Call<ResponseBody> downloadNews(@Query("size") String size, @Query("startDate") String startDate,
                                        @Query("endDate") String endDate, @Query("words") String words, @Query("categories") String categories);
    }


    class NetOperator {
        final private String Base_Url = "https://api2.newsminer.net/svc/news/";
        Retrofit retro;
        //NewsApi nApi;

        private String size;
        private String endDate;

        private StringBuilder GWords;
        private StringBuilder GCategories;
        private HashMap<String, Integer> wordCount;
        private HashMap<String, Integer> cateCount;
        private List<String> smallWords;
        private int wordTurn;

        NetOperator() {
            retro = new Retrofit.Builder()
                    .baseUrl(Base_Url)//要访问的网络地址域名，如http://www.zhihu.com
                    //.addConverterFactory(GsonConverterFactory.create())
                    .build();


            size = "5";

            Date date = new Date(); String time = date.toString();
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String sim = dateformat.format(date);
            this.endDate = sim;
            GWords = new StringBuilder(); GCategories = new StringBuilder();
            wordCount = new HashMap<>(); cateCount = new HashMap<>();
            smallWords = new ArrayList<>(); wordTurn = 0;
        }

        private void RefreshTime() {
            Date date = new Date(); String time = date.toString();
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String sim = dateformat.format(date);
            this.endDate = sim;
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
                Log.d("testing", "Zhuan: "+tmpS);
                return tmpS;
            } catch (UnsupportedEncodingException e)
            {
                return "";
            }
        }

        public void Refresh(final String categories) {
//            Log.d("testing", "Refresh: "+categories);
//            String cate = Zhuan(categories);
//            Log.d("testing", "Refresh: "+cate);
            RefreshTime();
            NewsApi nApi = retro.create(NewsApi.class);
            Call<ResponseBody> reCall = nApi.downloadNews(size,"",endDate,"",categories);
            reCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@Nullable Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    try {

                        //Log.d("fuck","have respect" );
                        String jsonStr = new String(response.body().bytes());//把原始数据转为字符串
                        //Log.d("testing", "onResponse: "+jsonStr);
                        List<News> newsList = MyApplication.newsOperator.Refresh(categories, jsonStr);
                        myAdapter.clear();
                        myAdapter.addList(newsList);
                        myAdapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                    Log.d("请求失败！", t.toString());
                    List<News> newsList = MyApplication.newsOperator.LocalNews(categories);
                    myAdapter.clear();
                    myAdapter.addList(newsList);
                    myAdapter.notifyDataSetChanged();
                }
            });
        }

        public void LoadMore(final String categories) {
//            String cate = Zhuan(categories);
            endDate = MyApplication.newsOperator.timeMap.get(categories);
            NewsApi nApi = retro.create(NewsApi.class);
            Call<ResponseBody> reCall = nApi.downloadNews("5","",endDate,"",categories);
            reCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@Nullable Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    try {
                        String jsonStr = new String(response.body().bytes());//把原始数据转为字符串
//                        Log.d("fuck", jsonStr);
                        List<News> newsList = MyApplication.newsOperator.LoadMore(categories, jsonStr);
//                        Log.d("fuck", newsList.size()+"");
                        myAdapter.addList(newsList);
                        myAdapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                    Log.d("请求失败！", t.toString());
                    //List<News> newsList = newsOperator.LocalNews(categories);
                }
            });
        }

        public void Search(final String words) {
            String cwords = Zhuan(words); RefreshTime();
            NewsApi nApi = retro.create(NewsApi.class);
            Call<ResponseBody> reCall = nApi.downloadNews("5","",endDate,cwords,"");
            reCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@Nullable Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    try {

                        String jsonStr = new String(response.body().bytes());//把原始数据转为字符串
                        List<News> newslist = MyApplication.newsOperator.Search(words, jsonStr);
                        myAdapter.clear();
                        myAdapter.addList(newsList);
                        myAdapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                    Log.d("请求失败！", t.toString());
                    //List<News> newsList = newsOperator.LocalNews(categories);
                }
            });
        }

        private void GetCount(int cnt)
        {
            //wordTurn = 0;
            if (GWords.length() > 0) GWords.setLength(0);
            if (GCategories.length() > 0) GCategories.setLength(0);
            cateCount.clear(); wordCount.clear();
            List<News> newslist = MyApplication.newsOperator.GetNewsWithCnt(cnt);
            for (int i = 0; i < newslist.size(); ++i)
            {
                News tmp = newslist.get(i);
                //Log.d("worstwoman",tmp.category);
                if (!cateCount.containsKey(tmp.category)) {
                    cateCount.put(tmp.category, 1);
                } else {
                    Integer t = cateCount.get(tmp.category);
                    cateCount.put(tmp.category, t + 1);
                    int sz = Math.min(tmp.keywords.size(), 4);
                }

                for (int j = 0; j < tmp.keywords.size(); ++j) {
                    String gg = tmp.keywords.get(j);
                    if (!wordCount.containsKey(gg)) {
                        wordCount.put(gg,1);
                        continue;
                    }
                    Integer tt = wordCount.get(gg);
                    wordCount.put(gg, tt+1);
                }
            }

            //cateCount
            for (int i = 0; i < 3; ++i)
            {
                if (cateCount.size() == 0) break;
                String tmpS = ""; Integer tmpT = 0;
                for (Map.Entry<String, Integer> entry : cateCount.entrySet())
                {
                    Integer nowt = entry.getValue();
                    if (nowt > tmpT) {
                        tmpS = entry.getKey();
                        tmpT = nowt;
                    }
                }
                GCategories.append(tmpS+",");
                cateCount.remove(tmpS);
            }
            if (GCategories.length() > 0) GCategories.deleteCharAt(GCategories.length()-1);

            //words
            for (int i = 0; i < 5; ++i)
            {
                if (wordCount.size() == 0) break;
                String tmpS = ""; Integer tmpT = 0;
                for (Map.Entry<String, Integer> entry: wordCount.entrySet())
                {
                    Integer nowt = entry.getValue();
                    if (nowt > tmpT) {
                        tmpS = entry.getKey();
                        tmpT = nowt;
                    }
                }
                GWords.append(tmpS+",");
                smallWords.add(tmpS);
                wordCount.remove(tmpS);
            }
            if(GWords.length() > 0) GWords.deleteCharAt(GWords.length()-1);
        }

        public void NextTurn() {
            Random rand = new Random();
            int t = 1; if(smallWords.size() > 0) t = smallWords.size();
            wordTurn = rand.nextInt(t);
        }

        public void IntroRefresh()
        {
            RefreshTime(); NextTurn();
            GetCount(10);
            String cwords = ""; if (smallWords.size() > 0) cwords = smallWords.get(wordTurn);
            String cCategories = ""; if(GCategories.length() > 0) cCategories = GCategories.toString();
            NewsApi nApi = retro.create(NewsApi.class);
            Call<ResponseBody> reCall = nApi.downloadNews(size,"",endDate,cwords,cCategories);
            //Log.d("badwoman",GWords.toString() + " ///// " + GCategories.toString());
            reCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@Nullable Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    try {

                        //Log.d("fuck","have respect" );
                        String jsonStr = new String(response.body().bytes());//把原始数据转为字符串
                        //Log.d("fuck", "onResponse: "+jsonStr);
                        //Log.d("testing", "onResponse: "+jsonStr);
                        List<News> newsList = MyApplication.newsOperator.Refresh("推荐", jsonStr);
                        myAdapter.clear();
                        myAdapter.addList(newsList);
                        myAdapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                    Log.d("请求失败！", t.toString());
                }
            });
        }

        public void IntroLoadMore()
        {
            endDate = MyApplication.newsOperator.timeMap.get("推荐");
            NextTurn();
            String cwords = ""; if (smallWords.size() > 0) cwords = smallWords.get(wordTurn);
            String cCategories = ""; if(GCategories.length() > 0) cCategories = GCategories.toString();
            GetCount(10);
            NewsApi nApi = retro.create(NewsApi.class);
            Call<ResponseBody> reCall = nApi.downloadNews(size,"",endDate,cwords,cCategories);
            //Log.d("goodwoman",GWords.toString() + " //// "+wordTurn+" " + smallWords.get(wordTurn) + " " + GCategories.toString());
            reCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@Nullable Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    try {

                        //Log.d("fuck","have respect" );
                        String jsonStr = new String(response.body().bytes());//把原始数据转为字符串
                        //Log.d("testing", "onResponse: "+jsonStr);
                        List<News> newsList = MyApplication.newsOperator.Refresh("推荐", jsonStr);
                        myAdapter.addList(newsList);
                        myAdapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                    Log.d("请求失败！", t.toString());
                }
            });
        }
    }

    private NetOperator netOperator;

    MyArticlesFragment() {
        super();
        idFragment = 0;
    }

    public MyArticlesFragment(String myTitle, String myCategory) {
        super();
        this.myCategory = myCategory;
        this.myTitle = myTitle;
    }
    public String getTitle() {return myTitle;}
//    public String getCategory() {
//        return myCategory;
//    }





    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);


        netOperator = new NetOperator();

        EmptyRecyclerView mRecyclerView = rootView.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);

        refreshLayout = rootView.findViewById(R.id.refresh_layout);
        // Set up OnRefreshListener that is invoked when the user performs a swipe-to-refresh gesture.
        refreshLayout.setRefreshHeader(new DeliveryHeader(getContext()));
        refreshLayout.setRefreshFooter(new BallPulseFooter(getContext()).setSpinnerStyle(SpinnerStyle.Scale));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
//                Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout");
//                Log.d("fuck", myCategory);
                if (!myCategory.equals("推荐"))
                    netOperator.Refresh(myCategory);
                else {
//                    Log.d("fuck", "onLoadMore: 推荐");
                    netOperator.IntroRefresh();
                }
                refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败

            }
        });

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
//                Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout");
//                loadMore();
                refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示刷新失败

                if (!myCategory.equals("推荐"))
                    netOperator.LoadMore(myCategory);
                else {

                    netOperator.IntroLoadMore();
                }
//                netOperator.LoadMore(myCategory);
//                myAdapter.notifyDataSetChanged();
            }
        });

        mLoadingIndicator = rootView.findViewById(R.id.loading_indicator);
        mLoadingIndicator.setVisibility(View.GONE);

        mEmptyStateTextView = rootView.findViewById(R.id.empty_view);

        mRecyclerView.setEmptyView(mEmptyStateTextView);

        myAdapter = new NewsAdapter(newsList);
        mRecyclerView.setAdapter(myAdapter);
        if (!myCategory.equals("推荐"))
            netOperator.Refresh(myCategory);
        else {

            netOperator.IntroRefresh();
        }
        // Check for network connectivity and initialize the loader

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}

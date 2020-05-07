package com.java.qdlljy;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.java.qdlljy.NewsUtil.News;
import com.java.qdlljy.R;
import com.scwang.smartrefresh.header.DeliveryHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Query;
import scut.carson_ho.searchview.SearchView;

public class SearchResultActivity extends AppCompatActivity {
    private List<News> newsList = new ArrayList<>();

    private String searchText;
    private SearchView searchView;
    private NewsAdapter myAdapter;
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

        public void RefreshSearch(final String words) {
            //String cwords = Zhuan(words);
            RefreshTime();
            NewsApi nApi = retro.create(NewsApi.class);
            Call<ResponseBody> reCall = nApi.downloadNews("5","",endDate,words,"");
            reCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@Nullable Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    try {

                        String jsonStr = new String(response.body().bytes());//把原始数据转为字符串
                        List<News> newslist = MyApplication.newsOperator.Search(words, jsonStr);
                        myAdapter.clear();
                        myAdapter.addList(newslist);
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

        public void LoadMoreSearch(final String words) {
            //String cwords = Zhuan(words);
            endDate = MyApplication.newsOperator.timeMap.get("搜索");
            NewsApi nApi = retro.create(NewsApi.class);
            Call<ResponseBody> reCall = nApi.downloadNews("5","",endDate,words,"");
            reCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@Nullable Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    try {
                        String jsonStr = new String(response.body().bytes());//把原始数据转为字符串
                        List<News> newslist = MyApplication.newsOperator.Search(words, jsonStr);
                        myAdapter.addList(newslist);
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


    }

    NetOperator netOperator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchText = getIntent().getStringExtra("searchText");
        setContentView(R.layout.activity_searchresult);

        EmptyRecyclerView mRecyclerView = findViewById(R.id.search_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);

        myAdapter = new NewsAdapter(newsList);
        mRecyclerView.setAdapter(myAdapter);
        netOperator = new NetOperator();

        refreshLayout = findViewById(R.id.search_refresh_layout);
        // Set up OnRefreshListener that is invoked when the user performs a swipe-to-refresh gesture.
        refreshLayout.setRefreshHeader(new DeliveryHeader(this));
        refreshLayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale));
//        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
//            @Override
//            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
//
//            }
//        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(2000/*,false*/);
                netOperator.LoadMoreSearch(searchText);
            }
        });

        netOperator.RefreshSearch(searchText);
    }

}

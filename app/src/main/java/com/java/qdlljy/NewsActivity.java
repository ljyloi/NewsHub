package com.java.qdlljy;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import com.java.qdlljy.NewsUtil.News;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.java.qdlljy.R;
import com.shuyu.gsyvideoplayer.GSYBaseActivityDetail;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

public class NewsActivity extends GSYBaseActivityDetail<StandardGSYVideoPlayer> {
    News news;
    private Context context;
    private Share share;
    public int state;
    StandardGSYVideoPlayer detailPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this;
        super.onCreate(savedInstanceState);
        state = 0;
        news = (News) getIntent().getSerializableExtra("news_data");
        setContentView(R.layout.detail_news);
//        TextView title = findViewById(R.id.news_title);
//        title.setText(news.getTitle());
        TextView author = findViewById(R.id.news_content_author);
        author.setText(news.publisher);

        TextView date = findViewById(R.id.news_content_date);
        date.setText(news.publishTime);

        Toolbar toolbar = findViewById(R.id.news_toolbar);
        toolbar.setTitle(news.title);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        ViewPager viewPager = findViewById(R.id.photo_viewpager);
//        MyImageView my_image = findViewById(R.id.my_image);

        if (news.Images.size() > 0) {
            ImagePagerAdapter adapter = new ImagePagerAdapter(this, news.Images);
            viewPager.setAdapter(adapter);
//            my_image.setImageURL(news.Images.get(0));
            Log.d("fuckyou", "onCreate: "+news.Images.get(0));
        }
        else viewPager.setVisibility(View.GONE);

        detailPlayer = (StandardGSYVideoPlayer) findViewById(R.id.detail_player);
        //增加title
        detailPlayer.getTitleTextView().setVisibility(View.GONE);
        detailPlayer.getBackButton().setVisibility(View.GONE);
        initVideoBuilderMode();
        if (news.videoURL.equals("")) {
            detailPlayer.setVisibility(View.GONE);
        }


        TextView content = findViewById(R.id.news_content_title);
        content.setText(news.content);

        FloatingActionsMenu fabMenu = (FloatingActionsMenu) findViewById(R.id.fab_menu);
        final FloatingActionButton bt_heart = findViewById(R.id.heart);

        if (news.isCollect == 0)
            bt_heart.setImageResource(R.drawable.heart_add);
        else bt_heart.setImageResource(R.drawable.heart_sub);

        bt_heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                bt_heart.set
                if (news.isCollect == 0) {
                    bt_heart.setImageResource(R.drawable.heart_sub);
                    MyApplication.newsOperator.CollectNews(news);
//                    news.isCollect = 1;
                }
                else {
//                    news.isCollect = 0;
                    bt_heart.setImageResource(R.drawable.heart_add);
                    MyApplication.newsOperator.UnCollectNews(news);
                }
//                bt_heart.setIcon();

            }
        });

        final FloatingActionButton bt_share = findViewById(R.id.share);
        bt_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share.shareSingleImage();
            }

        });

        share = new Share(context);
        Log.d("listened", "onClick: ");
        share.createShareImage(context, news);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public StandardGSYVideoPlayer getGSYVideoPlayer() {
        return detailPlayer;
    }

    @Override
    public GSYVideoOptionBuilder getGSYVideoOptionBuilder() {
        //内置封面可参考SampleCoverVideo
//        ImageView imageView = new ImageView(this);
//        loadCover(imageView, "https://vjs.zencdn.net/v/oceans.mp4");
        return new GSYVideoOptionBuilder()
//                .setThumbImageView(imageView)
                .setUrl(news.videoURL)
                .setCacheWithPlay(true)
                .setVideoTitle(" ")
                .setIsTouchWiget(true)
                .setRotateViewAuto(false)
                .setLockLand(false)
                .setShowFullAnimation(false)
                .setNeedLockFull(true)
                .setSeekRatio(1);
    }

    @Override
    public void clickForFullScreen() {

    }

    @Override
    public boolean getDetailOrientationRotateAuto() {
        return true;
    }

}

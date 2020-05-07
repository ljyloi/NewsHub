package com.java.qdlljy.MyFragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.java.qdlljy.CategoryFragmentPagerAdapter;
import com.java.qdlljy.MyChannel;
import com.java.qdlljy.util.GsonUtil;
import com.google.android.material.tabs.TabLayout;
import com.java.qdlljy.R;
import com.trs.channellib.channel.channel.helper.ChannelDataHelepr;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment implements ChannelDataHelepr.ChannelDataRefreshListenter {
    ChannelDataHelepr<MyChannel> dataHelepr;
    private ViewPager viewPager;
    View switch_view;
    CategoryFragmentPagerAdapter adapter;
    List<MyChannel> myChannels;
    Context context;
    private int needShowPosition=-1;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_main, container, false);


        dataHelepr = new ChannelDataHelepr(getActivity(), this, rootview.findViewById(R.id.fragment_head));

        myChannels = new ArrayList<>();
        loadData();

        dataHelepr.setSwitchView(rootview.findViewById(R.id.fragment_head));
        viewPager = rootview.findViewById(R.id.viewpager);
        adapter = new CategoryFragmentPagerAdapter(getContext(), getChildFragmentManager(), myChannels);
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = rootview.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        Log.d("fuckit", "onCreateView: "+dataHelepr.toString());
        return rootview;
    }

    private void loadData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String data = getFromRaw();
                List<MyChannel> alldata = GsonUtil.jsonToBeanList(data, MyChannel.class);
                //过滤数据，如果有新的频道会自动订阅并保存到数据库。
                final List<MyChannel> showChannels = dataHelepr.getShowChannels(alldata);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        myChannels.clear();
                        myChannels.addAll(showChannels);
                        adapter.notifyDataSetChanged();
                        if(needShowPosition!=-1){
                            viewPager.setCurrentItem(needShowPosition);
                            needShowPosition=-1;
                        }
                    }
                });

            }
        }).start();
    }

    @Override
    public void updateData() {
        loadData();
    }

    @Override
    public void onChannelSeleted(boolean update,final int posisiton) {
        //如果频道没有改变，则立即调整，否则记录下需要调整的position，在数据更新后调整
        if(!update) {
            viewPager.setCurrentItem(posisiton);
        }else {
            needShowPosition=posisiton;
        }
    }

    private String getFromRaw() {
        String result = "";
        try {
            InputStream input = getResources().openRawResource(R.raw.news_list);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = input.read(buffer)) != -1) {
                output.write(buffer, 0, length);
            }
            output.close();
            input.close();

            return output.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}

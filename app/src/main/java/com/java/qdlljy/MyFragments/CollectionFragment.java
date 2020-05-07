package com.java.qdlljy.MyFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import java.util.ArrayList;
import java.util.List;

public class CollectionFragment extends Fragment {

    private List<News> newsList = new ArrayList<>();

    private NewsAdapter myAdapter;
    private View mLoadingIndicator;
    private SmartRefreshLayout refreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_collection, container, false);

        EmptyRecyclerView mRecyclerView = rootView.findViewById(R.id.collection_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);

        myAdapter = new NewsAdapter(newsList);


        refreshLayout = rootView.findViewById( R.id.collection_refresh_layout);
        // Set up OnRefreshListener that is invoked when the user performs a swipe-to-refresh gesture.
        refreshLayout.setRefreshHeader(new DeliveryHeader(getContext()));
        refreshLayout.setRefreshFooter(new BallPulseFooter(getContext()).setSpinnerStyle(SpinnerStyle.Scale));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(2000/*,false*/);
                myAdapter.clear();
                myAdapter.addList(MyApplication.newsOperator.RefreshCollected());
                myAdapter.notifyDataSetChanged();

            }
        });

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(2000/*,false*/);
                myAdapter.addList(MyApplication.newsOperator.LoadMoreCollected());
                myAdapter.notifyDataSetChanged();
            }
        });

        mRecyclerView.setAdapter(myAdapter);

        myAdapter.addList(MyApplication.newsOperator.RefreshCollected());
        myAdapter.notifyDataSetChanged();
        return rootView;
    }
}

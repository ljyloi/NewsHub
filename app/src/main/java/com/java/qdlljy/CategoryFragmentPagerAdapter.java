package com.java.qdlljy;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.java.qdlljy.MyFragments.MyArticlesFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryFragmentPagerAdapter extends FragmentPagerAdapter {
    private Context mycontext;
    List<MyChannel> channels;
    int id=1;
    Map<String,Integer> IdsMap=new HashMap<>();
    List<String> preIds=new ArrayList<>();
    public CategoryFragmentPagerAdapter(Context context, FragmentManager fragmentManager, @NonNull List<MyChannel> channels) {
        super(fragmentManager);
        mycontext = context;
        this.channels = channels;
    }




    @Override
    public Fragment getItem(int position) {
        MyArticlesFragment fragment=new MyArticlesFragment(channels.get(position).getTitle(), channels.get(position).getCategory());
//        Bundle bundle=new Bundle();
//        bundle.putString(SimpleTitleFragment.KEY_TITLE,channels.get(position).getTitle());
//        bundle.putString(SimpleTitleFragment.KEY_URL,channels.get(position).getUrl());
//        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return channels.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return channels.get(position).getTitle();
    }

    @Override
    public long getItemId(int position) {
        return IdsMap.get(getPageTitle(position));
    }

    @Override
    public int getItemPosition(Object object) {
        MyArticlesFragment fragment= (MyArticlesFragment) object;
        String title=fragment.getTitle();
        int preId = preIds.indexOf(fragment.getTitle());
        int newId=-1;
        int i=0;
        int size=getCount();
        for(;i<size;i++){
            if(getPageTitle(i).equals(fragment.getTitle())){
                newId=i;
                break;
            }
        }
        if(newId!=-1&&newId==preId){
            return POSITION_UNCHANGED;
        }
        if(newId!=-1){
            return newId;
        }
        return POSITION_NONE;
    }

    @Override
    public void notifyDataSetChanged() {
        for(MyChannel info:channels){
            if(!IdsMap.containsKey(info.getTitle())){
                IdsMap.put(info.getTitle(),id++);
            }
        }
        super.notifyDataSetChanged();
        preIds.clear();
        int size=getCount();
        for(int i=0;i<size;i++){
            preIds.add((String) getPageTitle(i));
        }
    }

}

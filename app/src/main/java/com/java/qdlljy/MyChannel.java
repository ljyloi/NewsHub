package com.java.qdlljy;

import com.trs.channellib.channel.channel.ChannelEntity;

public class MyChannel implements ChannelEntity.ChannelEntityCreater {

    private String title;

    private String category;

    private int channelType;

    private int isFix;

    private String url;

    private int isSubscrible;

    public int getIsSubscrible() {
        return isSubscrible;
    }

    public void setIsSubscrible(int isSubscrible) {
        this.isSubscrible = isSubscrible;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getChannelType() {
        return channelType;
    }

    public void setChannelType(int channelType) {
        this.channelType = channelType;
    }

    public int getIsFix() {
        return isFix;
    }

    public void setIsFix(int isFix) {
        this.isFix = isFix;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    @Override
    public ChannelEntity createChannelEntity() {
        ChannelEntity entity=new ChannelEntity();
        entity.setFixed(isFix==1);
        entity.setName(title);
        return entity;
    }
}


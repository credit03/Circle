package com.guoyi.circle.been;

import java.io.Serializable;

/**
 * Created by Credit on 2017/3/6.
 */

public class ImageBean implements Serializable {
    private int Id;
    private String Url;
    private String Size;
    private String Name;
    private PostBean Belong;

    private boolean localUrl = false;

    public boolean isLocalUrl() {
        return localUrl;
    }

    public void setLocalUrl(boolean localUrl) {
        this.localUrl = localUrl;
    }

    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String Url) {
        this.Url = Url;
    }

    public String getSize() {
        return Size;
    }

    public void setSize(String Size) {
        this.Size = Size;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public PostBean getBelong() {
        return Belong;
    }

    public void setBelong(PostBean belong) {
        Belong = belong;
    }
}

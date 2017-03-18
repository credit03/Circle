package com.guoyi.circle.been;

import java.io.Serializable;

/**
 * Created by Credit on 2017/3/6.
 */

public class CommentBean implements Serializable {

    public static final int PUBLIC = 0;
    public static final int REPLAY = 1;
    private int Id;
    private int Type;
    private UserBean User;
    private UserBean ToReplayUser;
    private String Content;
    private PostBean Belong;

    public int getId() {
        return Id;
    }


    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public void setId(int id) {
        Id = id;
    }

    public UserBean getUser() {
        return User;
    }

    public void setUser(UserBean user) {
        User = user;
    }

    public UserBean getToReplayUser() {
        return ToReplayUser;
    }

    public void setToReplayUser(UserBean toReplayUser) {
        ToReplayUser = toReplayUser;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public PostBean getBelong() {
        return Belong;
    }

    public void setBelong(PostBean belong) {
        Belong = belong;
    }
}

package com.guoyi.circle.been;

import java.io.Serializable;

/**
 * Created by Credit on 2017/3/6.
 */

public class FavortsBean implements Serializable {

    private int Id;
    private UserBean User;

    private PostBean Belong;

    public int getId() {
        return Id;
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

    public PostBean getBelong() {
        return Belong;
    }

    public void setBelong(PostBean belong) {
        Belong = belong;
    }
}

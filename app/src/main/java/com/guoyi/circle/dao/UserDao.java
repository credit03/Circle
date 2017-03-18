package com.guoyi.circle.dao;

import android.util.Log;

import com.guoyi.circle.MyApplication;
import com.guoyi.circle.been.UserBean;
import com.guoyi.circle.request.GsonTools;

/**
 * Created by Credit on 2017/3/3.
 * 用户信息保存dao
 */

public class UserDao {

    private static final String TAG = "UserDao";

    private static UserDao instance;

    private static OfflineACache aCache;

    private UserDao() {
    }


    public synchronized static UserDao getInstance() {
        if (instance == null) {
            instance = new UserDao();
            aCache = OfflineACache.get(MyApplication.getContext());
        }
        return instance;
    }

    public String getUserId() {
        return aCache.getAsString("id");
    }

    public String getName() {
        return aCache.getAsString("name");
    }

    public String getMobile() {
        return aCache.getAsString("mobile");
    }

    public String getSex() {
        return aCache.getAsString("sex");
    }

    public int getAge() {
        return Integer.parseInt(aCache.getAsString("age"));
    }

    public String getBirth() {
        return aCache.getAsString("birth");
    }

    public String getAddress() {
        return aCache.getAsString("address");
    }

    public String getPwd() {
        return aCache.getAsString("pwd");
    }

    public String getPic() {
        return aCache.getAsString("pic");
    }

    public UserBean getUser() {
        UserBean user = GsonTools.changeGsonToBean(aCache.getAsString("user"), UserBean.class);
        if (user == null) {
            user = new UserBean();
        }
        return user;
    }

    public void saveUser(UserBean user) {
        aCache.put("id", user.getId() + "");
        aCache.put("name", user.getName());
        aCache.put("mobile", user.getMobile());
        aCache.put("age", user.getAge() + "'");
        aCache.put("pic", user.getPic());
        aCache.put("pwd", user.getPwd());
        aCache.put("sex", user.isSex() ? "女" : "男");
        aCache.put("address", user.getAddress());
        aCache.put("birth", user.getBirth());
        String json = GsonTools.createGsonString(user);
        aCache.put("user", json);
        Log.e(TAG, "保存用户信息saveUser: " + user);
    }

    public void clearUser() {
        aCache.put("id", "");
        aCache.put("name", "");
        aCache.put("mobile", "");
        aCache.put("age", "");
        aCache.put("pic", "");
        aCache.put("pwd", "");
        aCache.put("sex", "");
        aCache.put("address", "");
        aCache.put("birth", "");
        aCache.put("user", "");
    }
}

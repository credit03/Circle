package com.guoyi.circle.mvp.model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.guoyi.circle.been.ReturnMsg;
import com.guoyi.circle.been.UserBean;
import com.guoyi.circle.request.GsonTools;
import com.guoyi.circle.request.RequestApi;
import com.guoyi.circle.request.RetrofitProvider;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Credit on 2017/2/28.
 */

public class UserModel {
    private static final String TAG = "UserModel";

    private static UserModel instance;

    private UserModel() {
    }

    public synchronized static UserModel getInstance() {
        if (instance == null) {
            instance = new UserModel();
        }
        return instance;
    }

    public void login(@NonNull String mobile, @NonNull String pwd, @NonNull Observer<UserBean> observer) {

        RequestApi api = RetrofitProvider.get().create(RequestApi.class);
        api.login(mobile, pwd).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(returnMsg -> {
                    Log.e(TAG, "flatMap: 返回key  " + returnMsg.getIs());
                    if (returnMsg.getIs() == RequestApi.SUCCESS) {
                        return GsonTools.getBeanObservable(returnMsg, UserBean.class);
                    } else {
                        return error(returnMsg);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

    }

    public void register(@NonNull String mobile, @NonNull String pwd, @NonNull Observer<UserBean> observer) {

        RequestApi api = RetrofitProvider.get().create(RequestApi.class);
        api.register(mobile, pwd)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(returnMsg -> {
                    Log.e(TAG, "注册 返回key  " + returnMsg.getIs());
                    if (returnMsg.getIs() == RequestApi.SUCCESS) {
                        UserBean user = GsonTools.changeGsonToBean(returnMsg.getData(), UserBean.class);
                        return api.login(user.getMobile(), user.getPwd()).subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.io())
                                .flatMap(r -> {
                                    Log.e(TAG, "flatMap: 返回key  " + r.getIs());
                                    if (r.getIs() == RequestApi.SUCCESS) {
                                        /**
                                         * 把数据ReturnMsg 转换为UserBean返回
                                         */
                                        return GsonTools.getBeanObservable(r, UserBean.class);
                                    } else {
                                        return error(r);
                                    }
                                });

                    } else {
                        return error(returnMsg);
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }


    public void logout(Observer<ReturnMsg> consumer) {
        RequestApi api = RetrofitProvider.get().create(RequestApi.class);
        api.logout()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
    }


    private Observable<UserBean> error(ReturnMsg o) {
        UserBean user = new UserBean();
        user.setReturnMsg(o);
        return Observable.just(user);
    }
}

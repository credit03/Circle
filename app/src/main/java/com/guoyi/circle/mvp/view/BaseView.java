package com.guoyi.circle.mvp.view;

/**
 * Created by Credit on 2017/2/28.
 */

public interface BaseView {


    void showLoadProgress(String msg);

    void showErrorProgress(String msg);

    void showNoDataProgress();

}

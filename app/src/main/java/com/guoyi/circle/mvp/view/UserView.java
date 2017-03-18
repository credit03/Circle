package com.guoyi.circle.mvp.view;

import com.guoyi.circle.been.UserBean;

/**
 * Created by suneee on 2016/7/15.
 */
public interface UserView {

    interface IxUserView extends BaseView {
        static int LOGIN_CODE = 0;
        static int REGISTER_CODE = 1;

        void onSuccess(int code, UserBean user);

        void onError(int code, String msg);


    }

    interface Presenter extends BasePresenter {

        void login(String mobile, String pwd);

        void register(String mobile, String pwd);
    }
}

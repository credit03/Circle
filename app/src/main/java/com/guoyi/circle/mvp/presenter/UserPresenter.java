package com.guoyi.circle.mvp.presenter;

import android.support.annotation.NonNull;
import android.util.Log;

import com.guoyi.circle.been.ReturnMsg;
import com.guoyi.circle.been.UserBean;
import com.guoyi.circle.mvp.model.UserModel;
import com.guoyi.circle.mvp.view.UserView;
import com.guoyi.circle.mvp.view.UserView.IxUserView;
import com.guoyi.circle.request.RequestApi;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


/**
 * @ClassName: CirclePresenter
 * @Description: 通知model请求服务器和通知view更新
 */
public class UserPresenter extends LoadFalgs implements UserView.Presenter {

    private static final String TAG = "UserPresenter";
    private UserModel circleModel;
    private IxUserView view;


    private Disposable disposable;

    public UserPresenter(@NonNull IxUserView view) {
        circleModel = UserModel.getInstance();
        this.view = view;
    }


    public UserPresenter() {
        circleModel = UserModel.getInstance();
    }


    public void logout(Observer<ReturnMsg> consumer) {
        circleModel.logout(consumer);
    }

    @Override
    public void login(@NonNull String mobile, @NonNull String pwd) {
        if (LOAD_STATE == NOTE_LOAD) {
            this.view.showLoadProgress("正在登录");
        }
        circleModel.login(mobile, pwd, getObserver(IxUserView.LOGIN_CODE));
    }

    @Override
    public void register(String mobile, String pwd) {
        if (LOAD_STATE == NOTE_LOAD) {
            this.view.showLoadProgress("正在注册");
        }
        circleModel.register(mobile, pwd, getObserver(IxUserView.REGISTER_CODE));

    }

    public Observer<UserBean> getObserver(final int code) {
        return new Observer<UserBean>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
                Log.d(TAG, "onSubscribe: 关联了");
            }

            @Override
            public void onNext(UserBean user) {
                Log.d(TAG, "onNext: " + user);
                if (user.getIs() == RequestApi.SUCCESS) {
                    LOAD_STATE = ALREADEY_LOAD;
                    view.onSuccess(code, user);
                } else {
                    view.onError(code, user.getMsg());
                }
                if (disposable != null && !disposable.isDisposed()) {
                    disposable.dispose();
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>onError: 出错" + e.getMessage());
                if (code == IxUserView.LOGIN_CODE) {
                    view.showErrorProgress("登录失败");
                } else {
                    view.showErrorProgress("注册失败");
                }
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: 完成");
            }
        };
    }


    /**
     * 清除对外部对象的引用，反正内存泄露。
     */
    public void recycle() {
        this.view = null;
    }

}

package com.guoyi.circle.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.guoyi.circle.R;
import com.guoyi.circle.been.UserBean;
import com.guoyi.circle.dao.UserDao;
import com.guoyi.circle.mvp.presenter.UserPresenter;
import com.guoyi.circle.mvp.view.UserView;
import com.guoyi.circle.utils.DialogUtil;
import com.guoyi.circle.utils.StringUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity implements UserView.IxUserView {
    private static final String TAG = "LoginActivity";
    @InjectView(R.id.fl)
    FrameLayout mFl;
    @InjectView(R.id.input1)
    LinearLayout mInput1;
    @InjectView(R.id.et_username)
    EditText mEtUsername;
    @InjectView(R.id.et_password)
    EditText mEtPassword;
    @InjectView(R.id.btn_login)
    Button mBtnLogin;
    @InjectView(R.id.tv_sms)
    TextView mTvSms;


    private UserPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // butter
        ButterKnife.inject(this);
        presenter = new UserPresenter(this);

        mEtUsername.setText("13800138000");
        mEtPassword.setText("123456");
    }


    @OnClick(R.id.btn_login)
    public void login(View v) {
        String mobile = mEtUsername.getText().toString();
        String pwd = mEtPassword.getText().toString();

        if (TextUtils.isEmpty(mobile) || TextUtils.isEmpty(pwd)) {
            Toast.makeText(this, "帐号或密码不能为空哦", Toast.LENGTH_SHORT).show();
            return;
        }
        if (StringUtils.isPhoneNumber(mobile)) {
            presenter.login(mobile, pwd);
        } else {
            Toast.makeText(this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
        }

    }

    @OnClick(R.id.tv_sms)
    public void register(View v) {
        startActivity(new Intent(this, RegisterActivity.class));

    }


    @Override
    public void onSuccess(int code, UserBean user) {
        DialogUtil.dismissToatLoadingDialog();
        Log.e(TAG, user.getIs() + ":onSuccess: 登录成功返回 +" + user);
        UserDao.getInstance().saveUser(user);
        startActivity(new Intent(this, CircleActivity.class));
        finish();
    }

    @Override
    public void onError(int code, String msg) {
        Log.e(TAG, "onError: 登录失败返回 +" + msg);
        DialogUtil.dismissToatLoadingDialog();
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        mEtUsername.setText("");
        mEtPassword.setText("");
        mEtUsername.setFocusable(true);
        mEtUsername.requestFocus();
    }

    @Override
    public void showLoadProgress(String msg) {
        DialogUtil.showToastClickLoadingDialog(this, msg, false, view -> {
            DialogUtil.dismissToatLoadingDialog();
        });
    }

    @Override
    public void showErrorProgress(String msg) {
        DialogUtil.showToastSureBtnErrDialog(msg, "确定");
    }

    @Override
    public void showNoDataProgress() {

    }
}

package com.guoyi.circle.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.guoyi.circle.R;
import com.guoyi.circle.been.UserBean;
import com.guoyi.circle.mvp.presenter.UserPresenter;
import com.guoyi.circle.mvp.view.UserView;
import com.guoyi.circle.utils.DialogUtil;
import com.guoyi.circle.utils.StringUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity implements UserView.IxUserView {
    private static final String TAG = "RegisterActivity";
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
    private UserPresenter presenter;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.inject(this);
        presenter = new UserPresenter(this);
        this.setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("注册用户");
        }
        toolbar.setNavigationOnClickListener(view -> finish());
    }


    @OnClick(R.id.btn_login)
    public void register(View v) {
        String mobile = mEtUsername.getText().toString();
        String pwd = mEtPassword.getText().toString();

        if (TextUtils.isEmpty(mobile) || TextUtils.isEmpty(pwd)) {
            Toast.makeText(this, "帐号或密码不能为空哦", Toast.LENGTH_SHORT).show();
            return;
        }
        if (StringUtils.isPhoneNumber(mobile)) {
            presenter.register(mobile, pwd);
        } else {
            Toast.makeText(this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onSuccess(int code, UserBean user) {
        DialogUtil.dismissToatLoadingDialog();
        Log.e(TAG, user.getIs() + ":onSuccess: 注册成功返回 +" + user);
    }

    @Override
    public void onError(int code, String msg) {
        DialogUtil.dismissToatLoadingDialog();
        Log.e(TAG, "onError: 注册失败返回 >>+" + msg);
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

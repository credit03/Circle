package com.guoyi.circle.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.guoyi.circle.R;
import com.guoyi.circle.dao.UserDao;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Observable.just(UserDao.getInstance().getUser()).subscribeOn(Schedulers.io()).flatMap(R -> {
            if (R != null && !TextUtils.isEmpty(R.getMobile()) && !TextUtils.isEmpty(R.getPwd())) {
                return Observable.just(new Boolean(true)).delay(5, TimeUnit.SECONDS).subscribeOn(Schedulers.newThread());
            } else {
                return Observable.just(new Boolean(false)).delay(5, TimeUnit.SECONDS).subscribeOn(Schedulers.newThread());
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(r -> {
            if (r) {
                startActivity(new Intent(SplashActivity.this, CircleActivity.class));
                SplashActivity.this.finish();
            } else {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                SplashActivity.this.finish();
            }
        });
    }

}

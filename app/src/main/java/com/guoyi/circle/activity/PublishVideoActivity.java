package com.guoyi.circle.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.guoyi.circle.R;
import com.guoyi.circle.been.PostBean;
import com.guoyi.circle.been.ReturnMsg;
import com.guoyi.circle.dao.UserDao;
import com.guoyi.circle.mvp.model.CircleModel;
import com.guoyi.circle.request.GsonTools;
import com.guoyi.circle.request.RequestApi;
import com.guoyi.circle.utils.DialogUtil;
import com.guoyi.circle.utils.NetUtils;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import mabeijianxi.camera.MediaRecorderActivity;

public class PublishVideoActivity extends AppCompatActivity {
    private static final String TAG = "PublishVideoActivity";
    @InjectView(R.id.activity_publish_video)
    LinearLayout mActivityPublishVideo;
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.btn_toobar)
    Button mBtnToobar;
    @InjectView(R.id.content)
    EditText mContent;
    @InjectView(R.id.icon)
    ImageView mIcon;

    private String output_directory;
    private String video_uri;
    private String video_screenshot;

    private CircleModel model;
    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_video);
        ButterKnife.inject(this);
        model = CircleModel.getInstance();

        this.setSupportActionBar(mToolbar);
        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initEvent();
        setData();


    }

    public void setData() {
        /**
         *          intent = new Intent(this, Class.forName(getIntent().getStringExtra(OVER_ACTIVITY_NAME)));
         intent.putExtra(MediaRecorderActivity.OUTPUT_DIRECTORY, mMediaObject.getOutputDirectory());
         intent.putExtra(MediaRecorderActivity.VIDEO_URI, mMediaObject.getOutputTempTranscodingVideoPath());
         intent.putExtra(MediaRecorderActivity.VIDEO_SCREENSHOT, mMediaObject.getOutputVideoThumbPath());
         intent.putExtra("go_home",GO_HOME);
         */
        Intent intent = getIntent();
        if (intent != null) {
            output_directory = intent.getStringExtra(MediaRecorderActivity.OUTPUT_DIRECTORY);
            video_uri = intent.getStringExtra(MediaRecorderActivity.VIDEO_URI);
            video_screenshot = intent.getStringExtra(MediaRecorderActivity.VIDEO_SCREENSHOT);

            if (!TextUtils.isEmpty(video_screenshot)) {
                Glide.with(this).load(new File(video_screenshot)).into(mIcon);
            }
        }
    }

    public void initEvent() {
        mToolbar.setNavigationOnClickListener((view -> {
            onBackPressed();
        }));
    }

    @OnClick(R.id.icon)
    public void seeVideo(View v) {
        if (!TextUtils.isEmpty(video_uri)) {
            Intent intent = new Intent(this, VideoPreviewActivity.class);
            intent.putExtra(VideoPreviewActivity.VIDEO_PATH, video_uri);
            startActivity(intent);
        }
    }

    @OnClick(R.id.btn_toobar)
    public void publish(View v) {
        if (!NetUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "网络不可用", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(video_uri) || TextUtils.isEmpty(video_screenshot)) {
            Toast.makeText(this, "获取视频路径失败", Toast.LENGTH_SHORT).show();
            return;
        }
        String s = mContent.getText().toString();
        v.setEnabled(false);
        DialogUtil.showToastLoadingDialog(this, "正在上传视频", false);
        model.addVideoPost(UserDao.getInstance().getUserId(), s, 3, video_uri, video_screenshot, new Observer<ReturnMsg>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(ReturnMsg returnMsg) {
                DialogUtil.dismissToatLoadingDialog();
                if (returnMsg.getIs() == RequestApi.SUCCESS) {
                    setResult(returnMsg);
                } else {
                    v.setEnabled(true);
                    Log.i(TAG, "onNext:上传失败: " + returnMsg.getMsg());
                    Toast.makeText(PublishVideoActivity.this, "上传失败：" + returnMsg.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                DialogUtil.showToastErrDialog("上传失败");
                v.setEnabled(true);
                Log.i(TAG, "onError:上传失败: " + e.getMessage());
                Toast.makeText(PublishVideoActivity.this, "上传失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void setResult(ReturnMsg returnMsg) {
        PostBean bean = GsonTools.changeGsonToBean(returnMsg.getData(), PostBean.class);
        Intent intent = new Intent();
        intent.putExtra("PostBean", bean);
        intent.setAction(CircleActivity.VIDEO_BROAD_ACTION);
        sendBroadcast(intent);
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        /**
         * 上传之后或后退，删除视频
         */
        File file = new File(video_uri);
        file.delete();
        File file1 = new File(video_screenshot);
        file1.delete();
        super.onBackPressed();
        finish();
    }
}

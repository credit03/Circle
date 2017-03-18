package com.guoyi.circle.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.guoyi.circle.R;
import com.guoyi.circle.been.PostBean;
import com.guoyi.circle.been.ReturnMsg;
import com.guoyi.circle.dao.UserDao;
import com.guoyi.circle.mvp.model.CircleModel;
import com.guoyi.circle.request.GsonTools;
import com.guoyi.circle.request.RequestApi;
import com.guoyi.circle.ui.MultiImageView;
import com.guoyi.circle.utils.CommonUtils;
import com.guoyi.circle.utils.DialogUtil;
import com.guoyi.circle.utils.ImageUtils;
import com.guoyi.circle.utils.NetUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;

public class PublishActivity extends AppCompatActivity {

    private static final String TAG = "PublishActivity";
    @InjectView(R.id.activity_publish)
    LinearLayout mActivityPublish;
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.btn_toobar)
    Button mBtnToobar;
    @InjectView(R.id.content)
    EditText mContent;
    @InjectView(R.id.multiImagView)
    MultiImageView mMultiImagView;
    @InjectView(R.id.select_image)
    Button mSelectImage;


    private Disposable disposable;
    CircleModel model;
    private int REQUEST_CODE = 100;
    private ArrayList<String> photos = new ArrayList<>();
    private String savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "cirlce";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        ButterKnife.inject(this);

        this.setSupportActionBar(mToolbar);
        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //单张图片时，显示小图
        mMultiImagView.setShowThumbnailsMode(true);
        initEvent();
        model = CircleModel.getInstance();
    }


    @OnClick(R.id.select_image)
    public void SelectIamge(View v) {
        // R.id.select_image
        PhotoPicker.builder()
                .setPhotoCount(9)
                .setGridColumnCount(4)
                .setSelected(photos)
                .start(this);
    }

    @OnClick(R.id.btn_toobar)
    public void publish(View v) {

        if (!NetUtils.isNetworkConnected(this)) {
            Toast.makeText(this, "网络不可用", Toast.LENGTH_SHORT).show();
            return;
        }
        String s = mContent.getText().toString();
        if (TextUtils.isEmpty(s)) {
            Toast.makeText(this, "填写15个字以上才是好同志！！！", Toast.LENGTH_SHORT).show();
            return;
        }


        CommonUtils.hideSoftInput(this, v);
        v.setEnabled(false);
        DialogUtil.showToastClickLoadingDialog(this, "正在发表中..", false, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtil.dismissToatLoadingDialog();
            }
        });
        Observable.create((ObservableEmitter<List<String>> e) -> {
            /**
             * 压缩图片，再上传。 在wifi：max size 256kb,在手机网络下:max 128kb
             */

            List<String> list;
            if (NetUtils.isWifi(PublishActivity.this)) {
                list = ImageUtils.compressImages(photos, savePath, 256l);
            } else {
                list = ImageUtils.compressImages(photos, savePath, 128l);
            }
            e.onNext(list);
            e.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(r -> {
                    Log.e(TAG, "publish: 处理后的图片 :" + r.toString());
                    model.addPost(UserDao.getInstance().getUserId(), s, 1, r, new Observer<ReturnMsg>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            disposable = d;
                        }

                        @Override
                        public void onNext(ReturnMsg returnMsg) {
                            Log.e(TAG, "onNext: " + returnMsg.toString());
                            DialogUtil.dismissToatLoadingDialog();
                            v.setEnabled(true);
                            if (returnMsg.getIs() == RequestApi.SUCCESS) {
                                setResult(returnMsg);
                            } else {
                                Toast.makeText(PublishActivity.this, returnMsg.getMsg(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            v.setEnabled(true);
                            DialogUtil.showToastErrDialog("发送失败" + e.getMessage());
                            Log.e(TAG, "上传失败 onError: " + e.getMessage());
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
                });

    }


    public void setResult(ReturnMsg msg) {
        PostBean bean = GsonTools.changeGsonToBean(msg.getData(), PostBean.class);
        Intent intent = new Intent();
        intent.putExtra("PostBean", bean);
        this.setResult(RESULT_OK, intent);
        this.finish();

    }

    public void initEvent() {
        mMultiImagView.setOnItemClickListener((view, position) -> {
            //imagesize是作为loading时的图片size
            ImagePagerActivity.ImageSize imageSize = new ImagePagerActivity.ImageSize(view.getMeasuredWidth(), view.getMeasuredHeight());
            ImagePagerActivity.startImagePagerActivityForResult(this, REQUEST_CODE, photos, position, imageSize, true);
        });
        mToolbar.setNavigationOnClickListener((view -> {
            finish();
        }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        File file = new File(savePath);
        if (file.exists()) {
            file.delete();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PhotoPicker.REQUEST_CODE || requestCode == PhotoPreview.REQUEST_CODE || requestCode == REQUEST_CODE) {
                if (data != null) {
                    if (requestCode == REQUEST_CODE) {
                        photos = data.getStringArrayListExtra(ImagePagerActivity.INTENT_IMGURLS);
                    } else {
                        photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);

                    }
                    getResult();
                }
            }


        }

    }

    private void getResult() {
        if (photos != null) {
            mMultiImagView.setStringList(photos);
            if (photos.size() > 8) {
                mSelectImage.setVisibility(View.GONE);
            } else {
                mSelectImage.setVisibility(View.VISIBLE);
            }
        }
    }
}

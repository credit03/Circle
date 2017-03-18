package com.guoyi.circle.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.guoyi.circle.R;
import com.guoyi.circle.adapter.CircleAdapter;
import com.guoyi.circle.been.CommentBean;
import com.guoyi.circle.been.CommentConfig;
import com.guoyi.circle.been.FavortsBean;
import com.guoyi.circle.been.PostBean;
import com.guoyi.circle.been.ReturnMsg;
import com.guoyi.circle.dao.OfflineACache;
import com.guoyi.circle.dao.UserDao;
import com.guoyi.circle.mvp.presenter.CirclePresenter;
import com.guoyi.circle.mvp.presenter.UserPresenter;
import com.guoyi.circle.mvp.view.CircleView;
import com.guoyi.circle.request.GsonTools;
import com.guoyi.circle.ui.CommentListView;
import com.guoyi.circle.ui.DividerItemDecoration;
import com.guoyi.circle.ui.videolist.visibility.calculator.SingleListViewItemActiveCalculator;
import com.guoyi.circle.ui.videolist.visibility.scroll.RecyclerViewItemPositionGetter;
import com.guoyi.circle.utils.CommonUtils;
import com.guoyi.circle.utils.DialogUtil;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import mabeijianxi.camera.MediaRecorderActivity;
import mabeijianxi.camera.model.MediaRecorderConfig;

import static com.guoyi.circle.R.id.swipeToLoadLayout;
import static com.guoyi.circle.R.id.tv_toobar;

public class CircleActivity extends AppCompatActivity implements View.OnClickListener, OnRefreshListener, OnLoadMoreListener, CircleView.IxCircleView {

    private static final String TAG = "CircleActivity";
    @InjectView(swipeToLoadLayout)
    SwipeToLoadLayout mSwipeToLoadLayout;
    @InjectView(R.id.swipe_target)
    RecyclerView mRecyclerview;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(tv_toobar)
    TextView mTvToobar;


    @InjectView(R.id.ll_load_pro)
    LinearLayout mLlLoad;
    @InjectView(R.id.load_pro)
    ProgressBar mLoadPro;
    @InjectView(R.id.tv_load_text)
    TextView mTvload;
    @InjectView(R.id.reload_ll)
    LinearLayout mReload;


    @InjectView(R.id.editTextBodyLl)
    LinearLayout mEditTextBody;
    @InjectView(R.id.circleEt)
    EditText mCircleEdit;
    @InjectView(R.id.sendIv)
    ImageView mSendIv;
    @InjectView(R.id.progressBar)
    ProgressBar mProgressBar;


    private int screenHeight;
    private int editTextBodyHeight;
    private int currentKeyboardH;
    private int selectCircleItemH;
    private int selectCommentItemOffset;


    private CircleAdapter circleAdapter;

    private CirclePresenter presenter;
    private CommentConfig commentConfig;
    private LinearLayoutManager layoutManager;
    private int currentPosistionScrollY = 0;
    private int currentPosistion = 0;

    private OfflineACache aCache;//缓存框架

    private static final int PUBILISH_REQUEST_CODE = 123;

    /**
     * 视频列表滑动自动播放/停止
     */
    private SingleListViewItemActiveCalculator mCalculator;

    private VideoBroadCast videoBroadCast;
    public static final String VIDEO_BROAD_ACTION = "video_pubilsh_success";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle);
        ButterKnife.inject(this);
        initValue();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (comLastLoadTime(3)) {
            if (circleAdapter.getItemCount() > 0) {
                mProgressBar.setVisibility(View.VISIBLE);
            }
            presenter.loadData(presenter.LOAD_REFRESH);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(videoBroadCast);
    }

    public void initValue() {
        aCache = OfflineACache.get(this);
        this.setSupportActionBar(mToolbar);
        ActionBar supportActionBar = this.getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayShowCustomEnabled(true);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        setViewTreeObserver();

        layoutManager = new LinearLayoutManager(this);
        presenter = new CirclePresenter(this, this);
        circleAdapter = new CircleAdapter(this, presenter, mRecyclerview);

        mCalculator = new SingleListViewItemActiveCalculator(circleAdapter,
                new RecyclerViewItemPositionGetter(layoutManager, mRecyclerview));

        mRecyclerview.setLayoutManager(layoutManager);
        mRecyclerview.addItemDecoration(new DividerItemDecoration(this, true, DividerItemDecoration.VERTICAL_LIST));


        mRecyclerview.setAdapter(circleAdapter);


        /**
         * 注册视频发表成功广播
         */
        videoBroadCast = new VideoBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(VIDEO_BROAD_ACTION);

        registerReceiver(videoBroadCast, filter);
        /**
         * 获取缓存json
         */
        String JSON = aCache.getAsString(CirclePresenter.OFFLINE_JSON);
        if (!TextUtils.isEmpty(JSON)) {
            List<PostBean> list = GsonTools.changeGsonToSafeList(JSON, PostBean.class);
            if (list != null && list.size() > 0) {
                closeLoadView();
                mSwipeToLoadLayout.setVisibility(View.VISIBLE);
                circleAdapter.changeData(list);

            }
        }

    }

    private int mScrollState;

    public void initEvent() {
        mTvToobar.setOnClickListener(this);
        mReload.setOnClickListener(this);
        mSwipeToLoadLayout.setOnRefreshListener(this);
        mSwipeToLoadLayout.setOnLoadMoreListener(this);
        mToolbar.setNavigationOnClickListener(view -> {
            onBackPressed();
        });

        mRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                mScrollState = newState;
                if (newState == RecyclerView.SCROLL_STATE_IDLE && circleAdapter.getItemCount() > 0) {
                    mCalculator.onScrollStateIdle();
                }

                if (newState == RecyclerView.SCROLL_STATE_IDLE && (!ViewCompat.canScrollVertically(recyclerView, 1))) {
                    mSwipeToLoadLayout.setLoadingMore(true);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                mCalculator.onScrolled(mScrollState);
            }
        });

        mRecyclerview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mEditTextBody.getVisibility() == View.VISIBLE) {
                    updateEditTextBodyVisible(View.GONE, null);
                    return true;
                }
                return false;
            }
        });


        mSendIv.setOnClickListener(this);

    }

    //第一次加载时间
    protected long lastTime = 0;


    /**
     * 当前时间与上一次加载时间比较，是否大于min分钟
     *
     * @return
     */
    public boolean comLastLoadTime(int min) {
        if (System.currentTimeMillis() - lastTime > 1000 * 60 * min) {
            lastTime = System.currentTimeMillis();
            return true;
        }
        return false;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reload_ll:
                lastTime = 0;
                presenter.loadData(presenter.LOAD_REFRESH);
                break;
            case R.id.sendIv: //发布评论
                if (presenter != null) {
                    String content = mCircleEdit.getText().toString().trim();
                    if (TextUtils.isEmpty(content)) {
                        Toast.makeText(CircleActivity.this, "评论内容不能为空...", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    presenter.addComment(content, commentConfig);
                }
                updateEditTextBodyVisible(View.GONE, null);
                break;
            case R.id.tv_toobar:

                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setItems(new String[]{"发表图文", "发表视频"}, (d, i) -> {
                    d.dismiss();
                    switch (i) {
                        case 0://发表图文
                            startActivityForResult(new Intent(this, PublishActivity.class), PUBILISH_REQUEST_CODE);
                            break;
                        case 1: //发表视频

                            /**
                             * 发表成功，之后使用VideoBroadCast广播，获取获取发表的内容
                             */


                            MediaRecorderConfig config = new MediaRecorderConfig.Buidler()
                                    .doH264Compress(true)
                                    .smallVideoWidth(480) //宽度
                                    .smallVideoHeight(360)  //高度
                                    .recordTimeMax(6 * 1000) //时长
                                    .maxFrameRate(20) //帧数
                                    .minFrameRate(8)  //最小帧数
                                    .captureThumbnailsTime(1) //提取第一帧当缩略图
                                    .recordTimeMin((int) (1.5 * 1000)) //最小时间
                                    .build();
                            //打开视频录制界面
                            MediaRecorderActivity.goSmallVideoRecorder(this, PublishVideoActivity.class.getName(), config);


                            break;
                    }
                });

                dialog.show();

                break;
        }
    }

    /**
     * 发表视频成功广播监听
     */
    private class VideoBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: 广播接收");
            if (intent != null) {
                PostBean msg = (PostBean) intent.getSerializableExtra("PostBean");
                if (msg != null) {
                    circleAdapter.addDataFirst(msg);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("是否退出");
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                //退出
                new UserPresenter().logout(new Observer<ReturnMsg>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ReturnMsg returnMsg) {
                        UserDao.getInstance().clearUser();
                        aCache.put(CirclePresenter.OFFLINE_JSON, "");
                        startActivity(new Intent(CircleActivity.this, LoginActivity.class));
                        CircleActivity.this.finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(CircleActivity.this, "网络出错，退出失败:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });


            }
        });
        builder.setNeutralButton("取消", null);
        builder.show();

    }

    /**
     * 显示加载页面
     *
     * @param msg
     */

    public void showLoadView(String msg) {
        if (mLlLoad != null) {
            mLlLoad.setVisibility(View.VISIBLE);
            mLoadPro.setVisibility(View.VISIBLE);
            mTvload.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(msg)) {
                mTvload.setText(msg);
            }
            mReload.setVisibility(View.GONE);
        }
    }

    /**
     * 显示加载出错页面
     */
    public void showLoadErrView() {
        mProgressBar.setVisibility(View.GONE);
        if (mLlLoad != null) {
            mLlLoad.setVisibility(View.VISIBLE);
            mLoadPro.setVisibility(View.GONE);
            mTvload.setVisibility(View.GONE);
            mReload.setVisibility(View.VISIBLE);
        }

    }

    /**
     * 关闭加载页面
     */
    public void closeLoadView() {
        if (mLlLoad != null) {
            mLlLoad.setVisibility(View.GONE);
            mLoadPro.setVisibility(View.GONE);
            mTvload.setVisibility(View.GONE);
            mReload.setVisibility(View.GONE);
        }
    }


    /**
     * 显示加载没有数据页面
     *
     * @param msg
     */
    public void showloadNoDataView(String msg) {
        mProgressBar.setVisibility(View.GONE);
        if (mLlLoad != null) {
            mLlLoad.setVisibility(View.VISIBLE);
            mTvload.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(msg)) {
                mTvload.setText(msg);
            } else {
                mTvload.setText("暂没有记录");
            }
            mLoadPro.setVisibility(View.GONE);
            mReload.setVisibility(View.GONE);
        }

    }

    @Override
    public void onRefresh() {
        presenter.loadData(presenter.LOAD_REFRESH);
    }

    @Override
    public void onLoadMore() {
        presenter.loadData(presenter.LOAD_MORE);
    }


    @Override
    public void closeSwipeView(String msg) {
        mProgressBar.setVisibility(View.GONE);
        if (mSwipeToLoadLayout.isLoadingMore()) {
            mSwipeToLoadLayout.setLoadingMore(false);
        }
        if (mSwipeToLoadLayout.isRefreshing()) {
            mSwipeToLoadLayout.setRefreshing(false);
        }
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateDeleteCircle(int circlePosition) {
        List<PostBean> list = circleAdapter.getmData();
        if (list != null && list.size() > circlePosition) {
            list.remove(circlePosition);
            circleAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void updateAddFavorite(int circlePosition, FavortsBean addItem) {
        Log.e(TAG, "updateAddFavorite: 添加点赞成功" + addItem.toString());
        List<PostBean> list = circleAdapter.getmData();
        if (list != null && list.size() > circlePosition) {
            list.get(circlePosition).getFavorts().add(addItem);
            circleAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void updateDeleteFavort(int circlePosition, int userId) {
        Log.e(TAG, "updateDeleteFavort: 删除点赞成功" + userId);
        List<PostBean> list = circleAdapter.getmData();
        if (list != null && list.size() > circlePosition) {
            List<FavortsBean> favorts = list.get(circlePosition).getFavorts();
            int pos = -1;
            for (int i = 0; i < favorts.size(); i++) {
                if (favorts.get(i).getUser().getId() == userId) {
                    pos = i;
                    break;
                }

            }
            if (pos != -1) {
                favorts.remove(pos);
                circleAdapter.notifyDataSetChanged();
            }
           /* *//**
             * 使用rxjava
             * java.util.ConcurrentModificationException
             *//*
            Observable.fromIterable(favorts).flatMap(favortsBean -> {
                if (favortsBean.getUser().getId() == userId) {
                    favorts.remove(favortsBean);
                    return Observable.just(true);
                }
                return Observable.just(false);
            }).filter(r -> {
                return r;
            }).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(r -> {
                        circleAdapter.notifyDataSetChanged();
                    });
*/

        }
    }

    @Override
    public void updateAddComment(int circlePosition, CommentBean addItem) {
        List<PostBean> list = circleAdapter.getmData();
        if (list != null && list.size() > circlePosition) {
            list.get(circlePosition).getComments().add(addItem);
            circleAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void updateDeleteComment(int circlePosition, int commentId) {
        Log.e(TAG, "updateAddComment: 删除评价" + commentId);
        List<PostBean> list = circleAdapter.getmData();
        if (list != null && list.size() > circlePosition) {
            List<CommentBean> comments = list.get(circlePosition).getComments();
            int pos = -1;
            for (int i = 0; i < comments.size(); i++) {
                if (comments.get(i).getId() == commentId) {
                    pos = i;
                    break;
                }

            }
            if (pos != -1) {
                comments.remove(pos);
                circleAdapter.notifyDataSetChanged();
            }

        }
    }

    @Override
    public void updateEditTextBodyVisible(int visibility, CommentConfig c) {
        mCircleEdit.setText("");
        this.commentConfig = c;
        mEditTextBody.setVisibility(visibility);

        measureCircleItemHighAndCommentItemOffset(c);

        if (View.VISIBLE == visibility) {
            mCircleEdit.requestFocus();
            //弹出键盘
            CommonUtils.showSoftInput(mCircleEdit.getContext(), mCircleEdit);

        } else if (View.GONE == visibility) {
            //隐藏键盘
            CommonUtils.hideSoftInput(mCircleEdit.getContext(), mCircleEdit);
            if (currentPosistion != 0 && currentPosistionScrollY != 0) {
                layoutManager.scrollToPositionWithOffset(currentPosistion, 0);
            }
        }
    }

    @Override
    public void updateloadData(int loadType, List<PostBean> datas) {
        mProgressBar.setVisibility(View.GONE);
        closeLoadView();
        mSwipeToLoadLayout.setVisibility(View.VISIBLE);
        mSwipeToLoadLayout.setRefreshing(false);
        mSwipeToLoadLayout.setLoadingMore(false);


        if (loadType == presenter.LOAD_REFRESH) {
            if (datas.size() > 0) {
                circleAdapter.changeData(datas);
            } else {
                Toast.makeText(this, "服务器空空的~~~", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (datas.size() > 0) {
                circleAdapter.addData(datas);
            } else {
                Toast.makeText(this, "没有更多的数据啦", Toast.LENGTH_SHORT).show();
            }

        }
    }


    /**
     * 设置View的观察者
     */
    private void setViewTreeObserver() {
        /**
         * 获取View的观察者
         */
        ViewTreeObserver viewTreeObserver = mSwipeToLoadLayout.getViewTreeObserver();
        /**
         *   interface          ViewTreeObserver.OnGlobalLayoutListener
         当在一个视图树中全局布局发生改变或者视图树中的某个视图的可视状态发生改变时，所要调用的回调函数的接口类

         */
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();

                /**
                 * 状态栏高度
                 View的getWindowVisibleDisplayFrame(Rect outRect)附值outRect后，outRect.top()即是状态栏高度

                 标题高度
                 View的getWindowVisibleDisplayFrame(Rect outRect1)附值outRect后，outRect.height()-view.getheight()即是标题高度。
                 */

                mSwipeToLoadLayout.getWindowVisibleDisplayFrame(r); //获取当前view位置参数
                int statusBarH = getStatusBarHeight();//状态栏高度
                int screenH = mSwipeToLoadLayout.getRootView().getHeight(); //获取整个屏幕高度
                if (r.top != statusBarH) { //判断是否沉浸式状态栏
                    //在这个demo中r.top代表的是状态栏高度，在沉浸式状态栏时r.top＝0，通过getStatusBarHeight获取状态栏高度
                    r.top = statusBarH;
                }
                int keyboardH = screenH - (r.bottom - r.top); //获取弹出的键盘高度
                Log.d(TAG, "screenH＝ " + screenH + " &keyboardH = " + keyboardH + " &r.bottom=" + r.bottom + " &top=" + r.top + " &statusBarH=" + statusBarH);
                if (keyboardH == currentKeyboardH) {//有变化时才处理，否则会陷入死循环
                    return;
                }

                currentKeyboardH = keyboardH;
                screenHeight = screenH;//应用屏幕的高度

                editTextBodyHeight = mEditTextBody.getHeight();  //获取输入框所有高度

                if (keyboardH < 150) {//说明是隐藏键盘的情况
                    updateEditTextBodyVisible(View.GONE, null);
                    return;
                }
                //偏移listview
                if (layoutManager != null && commentConfig != null) {
                    currentPosistion = commentConfig.circlePosition + CircleAdapter.HEADVIEW_SIZE;

                    currentPosistionScrollY = getListviewOffset(commentConfig);
                    layoutManager.scrollToPositionWithOffset(currentPosistion, currentPosistionScrollY);
                }
            }
        });
    }

    /**
     * 测量偏移量
     *
     * @param commentConfig
     * @return
     */
    private int getListviewOffset(CommentConfig commentConfig) {
        if (commentConfig == null)
            return 0;
        //这里如果你的listview上面还有其它占高度的控件，则需要减去该控件高度，listview的headview除外。
        //int listviewOffset = mScreenHeight - mSelectCircleItemH - mCurrentKeyboardH - mEditTextBodyHeight;
        int listviewOffset = screenHeight - selectCircleItemH - currentKeyboardH - editTextBodyHeight - mToolbar.getHeight();
        if (commentConfig.commentType == CommentConfig.Type.REPLY) {
            //回复评论的情况
            listviewOffset = listviewOffset + selectCommentItemOffset;
        }
        return listviewOffset;
    }

    /**
     * 获取状态栏高度
     *
     * @return
     */
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (mEditTextBody != null && mEditTextBody.getVisibility() == View.VISIBLE) {
                updateEditTextBodyVisible(View.GONE, null);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void showLoadProgress(String msg) {
        if (circleAdapter.getItemCount() == 0) {
            mSwipeToLoadLayout.setVisibility(View.GONE);
            showLoadView(msg);
        }
    }

    @Override
    public void showErrorProgress(String msg) {
        if (circleAdapter.getItemCount() > 0) {
            this.mSwipeToLoadLayout.setRefreshing(false);
            this.mSwipeToLoadLayout.setLoadingMore(false);
            DialogUtil.showErrBtnDismissDialog(CircleActivity.this, "加载出错，请检查网络", false);
        } else {
            mSwipeToLoadLayout.setVisibility(View.GONE);
            showLoadErrView();
        }

    }

    @Override
    public void showNoDataProgress() {
        mSwipeToLoadLayout.setVisibility(View.GONE);
        showloadNoDataView("服务器出错 ");
    }


    private void measureCircleItemHighAndCommentItemOffset(CommentConfig commentConfig) {
        if (commentConfig == null)
            return;
        int firstPosition = layoutManager.findFirstVisibleItemPosition();
        //只能返回当前可见区域（列表可滚动）的子项
        View selectCircleItem = layoutManager.getChildAt(commentConfig.circlePosition + CircleAdapter.HEADVIEW_SIZE - firstPosition);

        if (selectCircleItem != null) {
            selectCircleItemH = selectCircleItem.getHeight();
        }

        if (commentConfig.commentType == CommentConfig.Type.REPLY && selectCircleItem != null) {
            //回复评论的情况
            CommentListView commentLv = (CommentListView) selectCircleItem.findViewById(R.id.commentList);
            if (commentLv != null) {
                //找到要回复的评论view,计算出该view距离所属动态底部的距离
                View selectCommentItem = commentLv.getChildAt(commentConfig.commentPosition);
                if (selectCommentItem != null) {
                    //选择的commentItem距选择的CircleItem底部的距离
                    selectCommentItemOffset = 0;
                    View parentView = selectCommentItem;
                    do {
                        int subItemBottom = parentView.getBottom();
                        parentView = (View) parentView.getParent();
                        if (parentView != null) {
                            selectCommentItemOffset += (parentView.getHeight() - subItemBottom);
                        }
                    } while (parentView != null && parentView != selectCircleItem);
                }
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == PUBILISH_REQUEST_CODE) {
                if (data != null) {
                    PostBean msg = (PostBean) data.getSerializableExtra("PostBean");
                    if (msg != null) {
                        circleAdapter.addDataFirst(msg);
                    }
                }
            }
        }
    }
}

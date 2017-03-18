package com.guoyi.circle.adapter.holder;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.guoyi.circle.MyApplication;
import com.guoyi.circle.R;
import com.guoyi.circle.adapter.OnRecyclerViewListener;
import com.guoyi.circle.been.ActionItem;
import com.guoyi.circle.been.CommentBean;
import com.guoyi.circle.been.CommentConfig;
import com.guoyi.circle.been.FavortsBean;
import com.guoyi.circle.been.PostBean;
import com.guoyi.circle.dao.UserDao;
import com.guoyi.circle.mvp.presenter.CirclePresenter;
import com.guoyi.circle.ui.CommentListView;
import com.guoyi.circle.ui.ExpandTextView;
import com.guoyi.circle.ui.PraiseListView;
import com.guoyi.circle.ui.SnsPopupWindow;
import com.guoyi.circle.ui.dialog.CommentDialog;
import com.guoyi.circle.ui.videolist.visibility.items.ListItem;
import com.guoyi.circle.utils.GlideCircleTransform;
import com.guoyi.circle.utils.StringUtils;
import com.guoyi.circle.utils.UrlUtils;

import java.util.List;


/**
 * Created by Credit on 2017/3/3.
 */

public abstract class BaseCircleHolder extends BaseViewHolder<PostBean> implements ListItem {


    protected CirclePresenter presenter;


    public BaseCircleHolder(View itemView, Context mContext, int viewType, OnRecyclerViewListener listener) {
        super(itemView, mContext, viewType, listener);
    }

    public BaseCircleHolder(Context context, ViewGroup root, int layoutRes, OnRecyclerViewListener l) {
        super(context, root, layoutRes, l);
    }

    public BaseCircleHolder(Context context, ViewGroup root, int layoutRes, int viewType, OnRecyclerViewListener l) {
        super(context, root, layoutRes, viewType, l);
    }

    public BaseCircleHolder(Context context, ViewGroup root, int layoutRes, int viewType, CirclePresenter circlePresenter, OnRecyclerViewListener l) {
        super(context, root, layoutRes, viewType, l);
        this.presenter = circlePresenter;
    }


    protected ImageView mHeadIv;
    protected TextView mNameTv;
    protected TextView mUrlTipTv;
    /**
     * 动态的内容
     */
    protected ExpandTextView mContentTv;
    protected ViewStub mViewStub;
    protected TextView mTimeTv;
    protected TextView mDeleteBtn;
    protected ImageView mSnsBtn;
    /**
     * /** 点赞列表
     */
    protected PraiseListView mPraiseListView;
    protected View mLinDig;
    protected LinearLayout mDigCommentBody;

    /**
     * 评论列表
     */
    protected CommentListView mCommentList;


    // ===========================
    public SnsPopupWindow snsPopupWindow;


    @Override
    public void initView(View rootView) {
        mHeadIv = (ImageView) rootView.findViewById(R.id.headIv);
        mNameTv = (TextView) rootView.findViewById(R.id.nameTv);
        mUrlTipTv = (TextView) rootView.findViewById(R.id.urlTipTv);
        mContentTv = (ExpandTextView) rootView.findViewById(R.id.contentTv);

        mViewStub = (ViewStub) rootView.findViewById(R.id.viewStub);

        mTimeTv = (TextView) rootView.findViewById(R.id.timeTv);
        mDeleteBtn = (TextView) rootView.findViewById(R.id.deleteBtn);
        mSnsBtn = (ImageView) rootView.findViewById(R.id.snsBtn);
        mDigCommentBody = (LinearLayout) rootView.findViewById(R.id.digCommentBody);
        mPraiseListView = (PraiseListView) rootView.findViewById(R.id.praiseListView);
        mLinDig = rootView.findViewById(R.id.lin_dig);
        mCommentList = (CommentListView) rootView.findViewById(R.id.commentList);

        snsPopupWindow = new SnsPopupWindow(mContext);

        initViewStub();
    }


    @Override
    public void bindData(PostBean circleItem, int postion) {
        super.bindData(circleItem, postion);
        if (mdata != null) {
            final int circleId = mdata.getId();
            String name = mdata.getAuthor().getName();
            String headImg = mdata.getAuthor().getPic();
            final String content = mdata.getContent();
            String createTime = mdata.getCreateTime();
            final List<FavortsBean> favortDatas = mdata.getFavorts();
            final List<CommentBean> commentsDatas = mdata.getComments();
            boolean hasFavort = favortDatas.size() > 0 ? true : false;
            boolean hasComment = commentsDatas.size() > 0 ? true : false;

            /**
             * 头像
             */
            Glide.with(mContext).load(StringUtils.getImageDefaultURL(headImg)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.color.bg_no_photo)
                    .transform(new GlideCircleTransform(mContext)).into(mHeadIv);

            //名字
            mNameTv.setText(name);
            //时间
            mTimeTv.setText(createTime);

            /**
             * 文本内容
             */
            if (!TextUtils.isEmpty(content)) {
                mContentTv.setExpand(circleItem.isExpand());
                mContentTv.setExpandStatusListener(isExpand -> {
                    mdata.setExpand(isExpand);
                });
                //把url设置高亮
                mContentTv.setText(UrlUtils.formatUrlString(content));
            }
            //设置是否显示内容
            mContentTv.setVisibility(TextUtils.isEmpty(content) ? View.GONE : View.VISIBLE);
            //是否是自己发表的文章
            if (UserDao.getInstance().getUserId().equals(circleItem.getAuthor().getId() + "")) {
                mDeleteBtn.setVisibility(View.VISIBLE);
            } else {
                mDeleteBtn.setVisibility(View.GONE);
            }

            //点赞列表与评论列表
            if (hasFavort || hasComment) {
                if (hasFavort) {//处理点赞列表
                    mPraiseListView.setDatas(favortDatas);
                    mPraiseListView.setVisibility(View.VISIBLE);
                } else {
                    mPraiseListView.setVisibility(View.GONE);
                }

                if (hasComment) {//处理评论列表
                    mCommentList.setDatas(commentsDatas);
                    mCommentList.setVisibility(View.VISIBLE);
                } else {
                    mCommentList.setVisibility(View.GONE);
                }
                mDigCommentBody.setVisibility(View.VISIBLE);
            } else {
                mDigCommentBody.setVisibility(View.GONE);
            }

            //点赞与评价的分割线
            mLinDig.setVisibility(hasFavort && hasComment ? View.VISIBLE : View.GONE);

            //判断是否已点赞
            if (circleItem.getCurUserFavortId(UserDao.getInstance().getUserId())) {
                snsPopupWindow.getmActionItems().get(0).mTitle = "取消";
            } else {
                snsPopupWindow.getmActionItems().get(0).mTitle = "赞";
            }
            snsPopupWindow.update();
            snsPopupWindow.setmItemClickListener(new PopupItemClickListener(position, mdata, UserDao.getInstance().getUserId()));
            mUrlTipTv.setVisibility(View.GONE);

        }
    }

    public abstract void initViewStub();

    @Override
    public void initEvent() {
        /**
         * 删除文章
         */
        mDeleteBtn.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage("是否删除该说说");
            builder.setNegativeButton("确定", (d, i) -> {
                d.dismiss();
                //删除
                if (presenter != null) {
                    presenter.deleteCircle(position, mdata.getId());
                }
            });
            builder.setNeutralButton("取消", null);
            builder.show();

        });

        /**
         * 点赞列表
         */
        mPraiseListView.setOnItemClickListener(new PraiseListView.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                List<FavortsBean> favorters = mdata.getFavorts();
                String userName = favorters.get(position).getUser().getName();
                int userId = favorters.get(position).getUser().getId();
                Toast.makeText(MyApplication.getContext(), userName + " &id = " + userId, Toast.LENGTH_SHORT).show();
            }
        });

        /**
         * 评论列表
         */
        mCommentList.setOnItemClickListener(new CommentListView.OnItemClickListener() {
            @Override
            public void onItemClick(int commentPosition) {
                List<CommentBean> comments = mdata.getComments();
                CommentBean commentItem = comments.get(commentPosition);
                //点击是自己发表的评价
                if (UserDao.getInstance().getUserId().equals(commentItem.getUser().getId() + "")) {//复制或者删除自己的评论
                    CommentDialog dialog = new CommentDialog(mContext, presenter, commentItem, position);
                    dialog.show();
                } else {//回复别人的评论
                    if (presenter != null) {
                        CommentConfig config = new CommentConfig();
                        config.postId = mdata.getId();
                        config.circlePosition = position;
                        config.commentPosition = commentPosition;
                        config.commentType = CommentConfig.Type.REPLY;
                        config.replyUser = commentItem.getUser();
                        presenter.showEditTextBody(config);
                    }
                }
            }
        });

        //长按评价
        mCommentList.setOnItemLongClickListener(new CommentListView.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(int commentPosition) {
                //长按进行复制或者删除
                List<CommentBean> comments = mdata.getComments();
                CommentBean commentItem = comments.get(commentPosition);
                CommentDialog dialog = new CommentDialog(mContext, presenter, commentItem, position);
                dialog.show();
            }
        });

        //泡泡窗口
        mSnsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //弹出popupwindow
                snsPopupWindow.showPopupWindow(view);
            }
        });
    }


    //泡泡窗口的事件回调
    private class PopupItemClickListener implements SnsPopupWindow.OnItemClickListener {
        private String curUserId;
        //动态在列表中的位置
        private int mCirclePosition;
        private long mLasttime = 0;
        private PostBean mPostBean;

        public PopupItemClickListener(int circlePosition, PostBean circleItem, String favorId) {
            this.curUserId = favorId;
            this.mCirclePosition = circlePosition;
            this.mPostBean = circleItem;
            Log.e(TAG, "PopupItemClickListener: UserID:" + favorId);
        }

        @Override
        public void onItemClick(ActionItem actionitem, int position) {
            switch (position) {
                case 0://点赞、取消点赞
                    if (System.currentTimeMillis() - mLasttime < 700)//防止快速点击操作
                        return;
                    mLasttime = System.currentTimeMillis();
                    if (presenter != null) {
                        if ("赞".equals(actionitem.mTitle.toString())) {
                            presenter.addFavort(mCirclePosition, mdata.getId());
                        } else {//取消点赞
                            presenter.deleteFavort(mCirclePosition, mPostBean.getId(), Integer.valueOf(curUserId));
                        }
                    }
                    break;
                case 1://发布评论
                    if (presenter != null) {
                        CommentConfig config = new CommentConfig();
                        config.postId = mdata.getId();
                        config.circlePosition = mCirclePosition;
                        config.commentType = CommentConfig.Type.PUBLIC;
                        presenter.showEditTextBody(config);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void setActive(View newActiveView, int newActiveViewPosition) {

    }

    @Override
    public void deactivate(View currentView, int position) {

    }
}

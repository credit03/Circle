package com.guoyi.circle.mvp.presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.guoyi.circle.been.CommentBean;
import com.guoyi.circle.been.CommentConfig;
import com.guoyi.circle.been.FavortsBean;
import com.guoyi.circle.been.PostBean;
import com.guoyi.circle.been.ReturnMsg;
import com.guoyi.circle.dao.OfflineACache;
import com.guoyi.circle.dao.UserDao;
import com.guoyi.circle.mvp.model.CircleModel;
import com.guoyi.circle.mvp.view.CircleView;
import com.guoyi.circle.request.GsonTools;
import com.guoyi.circle.request.RequestApi;
import com.guoyi.circle.utils.DialogUtil;
import com.guoyi.circle.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by Credit on 2017/3/3.
 */

public class CirclePresenter extends LoadFalgs implements CircleView.Presenter {
    private CircleView.IxCircleView view;
    private CircleModel circleModel;
    private Disposable disposable;
    private static final String TAG = "CirclePresenter";
    public static final String OFFLINE_JSON = "offlineJson";


    private int pageSize = 10;
    private int pageIndex = 0;

    private OfflineACache aCache;

    private Map<Integer, Map<Integer, String>> requestTags;
    private Context context;

    public CirclePresenter(@NonNull Context context, @NonNull CircleView.IxCircleView view) {
        this.view = view;
        pageIndex = 0;
        aCache = OfflineACache.get(context);
        this.context = context;
        circleModel = CircleModel.getInstance();
        requestTags = new HashMap<>();

        Map<Integer, String> Favort = new HashMap<>();
        Favort.put(OP_ADD, "点赞中");
        Favort.put(OP_DELETE, "删除点赞");

        Map<Integer, String> Comment = new HashMap<>();
        Comment.put(OP_ADD, "评价中");
        Comment.put(OP_DELETE, "删除评价");

        Map<Integer, String> Post = new HashMap<>();
        Post.put(OP_ADD, "发表说说");
        Post.put(OP_DELETE, "删除说说");

        requestTags.put(FAVORT_OP, Favort);
        requestTags.put(COMMENT_OP, Comment);
        requestTags.put(POST_OP, Post);


    }

    @Override
    public void loadData(int loadType) {
        if (LOAD_STATE == NOTE_LOAD) {
            view.showLoadProgress("正在加载中..");
        }

        if (loadType == LOAD_MORE) {
            pageIndex++;
        } else {
            pageIndex = 0;
        }

        circleModel.postList(pageIndex, pageSize, getObserver(LOAD_OP, loadType, 0));

    }

    @Override
    public void deleteCircle(final int circlePosition, int circleId) {
        String userId = UserDao.getInstance().getUserId();
        circleModel.deletePost(circleId, Integer.valueOf(userId), getObserver(POST_OP, OP_DELETE, circlePosition));
    }

    @Override
    public void addFavort(final int circlePosition, int circleId) {
        String userId = UserDao.getInstance().getUserId();
        circleModel.addFavort(circleId, Integer.valueOf(userId), getObserver(FAVORT_OP, OP_ADD, circlePosition));
    }

    @Override
    public void deleteFavort(final int circlePosition, int postId, final int userId) {
        Log.e(TAG, "deleteFavort: postId " + postId + "  userId:" + userId);
        circleModel.deleteFavort(postId, userId, getObserver(FAVORT_OP, OP_DELETE, circlePosition));
    }

    @Override
    public void addComment(String content, CommentConfig config) {
        int userId = Integer.valueOf(UserDao.getInstance().getUserId());
        if (config.commentType == CommentConfig.Type.PUBLIC) {
            circleModel.addComment(content, 0, userId, userId, config.postId, getObserver(COMMENT_OP, OP_ADD, config.circlePosition));
        } else {
            circleModel.addComment(content, 1, userId, config.replyUser.getId(), config.postId, getObserver(COMMENT_OP, OP_ADD, config.circlePosition));
        }
    }

    @Override
    public void deleteComment(int circlePosition, int commentId) {
        circleModel.deleteComment(commentId, getObserver(COMMENT_OP, OP_DELETE, circlePosition));
    }


    /**
     * @param commentConfig
     */
    public void showEditTextBody(CommentConfig commentConfig) {
        if (view != null) {
            view.updateEditTextBodyVisible(View.VISIBLE, commentConfig);
        }

    }


    /**
     * 获取观察者
     *
     * @param op    操作
     * @param mode  操作模式
     * @param value 回调传递值
     * @return
     */
    public Observer<ReturnMsg> getObserver(int op, int mode, int value) {
        if (op != LOAD_OP) {
            DialogUtil.showToastLoadingDialog(context, getTag(op, mode), false);
        }

        return new Observer<ReturnMsg>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
                Log.d(TAG, "onSubscribe: 关联了");
            }

            @Override
            public void onNext(ReturnMsg returnMsg) {
                DialogUtil.dismissToatLoadingDialog();
                if (returnMsg.getIs() == RequestApi.SUCCESS) {
                    LOAD_STATE = ALREADEY_LOAD;
                    String json = GsonTools.createGsonString(returnMsg.getData());
                    Log.e(TAG, "onNext: 返回JSON:" + returnMsg + "\n value:" + json);

                    switch (op) {
                        case LOAD_OP:
                            if (mode == LOAD_REFRESH || mode == LOAD_MORE) {
                                if (mode == LOAD_REFRESH) {
                                    /**
                                     * 缓存json
                                     */
                                    aCache.put(OFFLINE_JSON, json);
                                }
                                view.updateloadData(mode, GsonTools.changeGsonToSafeList(json, PostBean.class));
                            }
                            break;
                        case FAVORT_OP:
                            if (mode == OP_ADD) {
                                view.updateAddFavorite(value, GsonTools.changeGsonToBean(json, FavortsBean.class));
                            } else {
                                double d = (double) returnMsg.getData();
                                int id = StringUtils.stringToInt(d + "");
                                view.updateDeleteFavort(value, id);
                            }
                            break;
                        case POST_OP:
                            if (mode == OP_ADD) {

                            } else {
                                view.updateDeleteCircle(value);
                            }
                            break;
                        case COMMENT_OP:
                            if (mode == OP_ADD) {
                                view.updateAddComment(value, GsonTools.changeGsonToBean(json, CommentBean.class));
                            } else {
                                double d = (double) returnMsg.getData();
                                int id = StringUtils.stringToInt(d + "");
                                view.updateDeleteComment(value, id);
                            }
                            break;
                    }
                } else {
                    Log.e(TAG, "onNext: 失败 code:" + returnMsg.getIs() + " msg:" + returnMsg.getMsg());
                    view.closeSwipeView(returnMsg.getMsg());
                }
                if (disposable != null && !disposable.isDisposed()) {
                    disposable.dispose();
                }
            }

            @Override
            public void onError(Throwable e) {
                DialogUtil.dismissToatLoadingDialog();
                Log.e(TAG, "onError: 加载出错:" + e.getMessage());
                if (LOAD_STATE == NOTE_LOAD) {
                    view.showErrorProgress("加载出错 ");
                }

                switch (op) {
                    case LOAD_OP:
                        //关闭加载界面
                        view.closeSwipeView(e.getMessage());
                        if (mode == LOAD_REFRESH) {
                            Toast.makeText(context, "刷新失败，请检查网络", Toast.LENGTH_LONG).show();
                        } else {
                            pageIndex--;
                            Toast.makeText(context, "加载失败，请检查网络", Toast.LENGTH_LONG).show();
                        }
                        break;
                    case FAVORT_OP:
                        if (mode == OP_ADD) {
                            Toast.makeText(context, "点赞失败，请检查网络", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, "取消点赞失败，请检查网络", Toast.LENGTH_LONG).show();
                        }
                        break;
                    case POST_OP:
                        if (mode == OP_ADD) {
                            Toast.makeText(context, "发表说说失败，请检查网络", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, "删除说说失败，请检查网络", Toast.LENGTH_LONG).show();
                        }
                        break;
                    case COMMENT_OP:
                        if (mode == OP_ADD) {
                            Toast.makeText(context, "发表评价失败，请检查网络", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, "删除评价失败，请检查网络", Toast.LENGTH_LONG).show();
                        }
                        break;
                }


            }

            @Override
            public void onComplete() {

            }
        };
    }


    public String getTag(int op, int mode) {
        return requestTags.get(op).get(mode);
    }

}

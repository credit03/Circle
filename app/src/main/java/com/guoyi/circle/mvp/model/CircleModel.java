package com.guoyi.circle.mvp.model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.guoyi.circle.been.ReturnMsg;
import com.guoyi.circle.been.UserBean;
import com.guoyi.circle.dao.UserDao;
import com.guoyi.circle.request.GsonTools;
import com.guoyi.circle.request.RequestApi;
import com.guoyi.circle.request.RetrofitProvider;
import com.guoyi.circle.utils.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by Credit on 2017/3/6.
 */

public class CircleModel {
    private static final String TAG = "UserModel";

    private static CircleModel instance;

    private CircleModel() {
    }

    public synchronized static CircleModel getInstance() {
        if (instance == null) {
            instance = new CircleModel();
        }
        return instance;
    }

    @NonNull
    public void postList(int pageIndex, int pageSize, Observer<ReturnMsg> observer) {
        RequestApi api = RetrofitProvider.get().create(RequestApi.class);
        /**
         * @Field("pageIndex") int pageIndex,
         @Field("pageSize") int pageSize
         */
        Map<String, RequestBody> request = new HashMap<>();
        request.put("pageIndex", RequestBody.create(null, pageIndex + ""));
        request.put("pageSize", RequestBody.create(null, pageSize + ""));
        execute(api.get("postlist", request), observer);
    }


    @NonNull
    public void addUrlPost(String userId, String icon, String title, String content, String shareDesc, String url, int type, Observer<ReturnMsg> observer) {
        RequestApi api = RetrofitProvider.getUpload(60).create(RequestApi.class);
        Map<String, RequestBody> photos = new HashMap<>();
        photos.put("content", RequestBody.create(null, content));
        photos.put("userId", RequestBody.create(null, userId));
        photos.put("type", RequestBody.create(null, type + ""));
        photos.put("shareIcon", RequestBody.create(null, icon));
        photos.put("shareTitle", RequestBody.create(null, title));
        photos.put("shareDesc", RequestBody.create(null, shareDesc));
        photos.put("shareUrl", RequestBody.create(null, url));
        execute(api.create("post", photos), observer);
    }


    @NonNull
    public void addPost(String userId, String content, int type, List<String> imgs, Observer<ReturnMsg> observer) {
        RequestApi api = RetrofitProvider.getUpload(60).create(RequestApi.class);
        Map<String, RequestBody> photos = new HashMap<>();
        photos.put("content", RequestBody.create(null, content));
        photos.put("userId", RequestBody.create(null, userId));
        photos.put("type", RequestBody.create(null, type + ""));
        if (imgs != null && imgs.size() > 0) {
            photos.put("haveimg", RequestBody.create(null, "have"));
            for (String url : imgs) {
                String urlFileName = StringUtils.getURLFileName(url);
                RequestBody photo = RequestBody.create(MediaType.parse("image/png"), new File(url));
                photos.put("photos\"; filename=\"" + urlFileName, photo);
            }
        } else {
            photos.put("haveimg", RequestBody.create(null, "done"));
        }
        execute(api.create("post", photos), observer);
    }

    @NonNull
    public void addVideoPost(String userId, String content, int type, String videoPath, String videoImg, Observer<ReturnMsg> observer) {
        RequestApi api = RetrofitProvider.getUpload(60).create(RequestApi.class);
        Map<String, RequestBody> request = new HashMap<>();
        request.put("content", RequestBody.create(null, content));
        request.put("userId", RequestBody.create(null, userId));
        request.put("type", RequestBody.create(null, type + ""));

        String VideoFileName = StringUtils.getURLFileName(videoPath);
        RequestBody video = RequestBody.create(MediaType.parse("'video/mpeg'"), new File(videoPath));
        request.put("video\"; filename=\"" + VideoFileName, video);

        String videoImgName = StringUtils.getURLFileName(videoImg);
        RequestBody photo = RequestBody.create(MediaType.parse("image/png"), new File(videoImg));
        request.put("videoImg\"; filename=\"" + videoImgName, photo);
        execute(api.create("post", request), observer);
    }


    @NonNull
    public void deletePost(int postId, int userId, Observer<ReturnMsg> observer) {
        RequestApi api = RetrofitProvider.get().create(RequestApi.class);
        Map<String, RequestBody> request = new HashMap<>();
        request.put("postId", RequestBody.create(null, postId + ""));
        request.put("userId", RequestBody.create(null, userId + ""));
        execute(api.delete("post", request), observer);
    }

    @NonNull
    public void deleteComment(int commentId, Observer<ReturnMsg> observer) {
        RequestApi api = RetrofitProvider.get().create(RequestApi.class);
        Map<String, RequestBody> request = new HashMap<>();
        request.put("commentId", RequestBody.create(null, commentId + ""));
        execute(api.delete("comment", request), observer);
    }

    @NonNull
    public void addComment(String content, int cType, int userId, int touserId, int postId, Observer<ReturnMsg> observer) {
        RequestApi api = RetrofitProvider.get().create(RequestApi.class);
        /**
         * @Field("content") String content,
         @Field("cType") int cType,
         @Field("userId") int userId,
         @Field("touserId") int touserId,
         @Field("postId") int postId
         */
        Map<String, RequestBody> request = new HashMap<>();
        request.put("content", RequestBody.create(null, content));
        request.put("cType", RequestBody.create(null, cType + ""));
        request.put("userId", RequestBody.create(null, userId + ""));
        request.put("touserId", RequestBody.create(null, touserId + ""));
        request.put("postId", RequestBody.create(null, postId + ""));
        execute(api.create("comment", request), observer);
    }

    @NonNull
    public void addFavort(int postId, int userId, Observer<ReturnMsg> observer) {
        RequestApi api = RetrofitProvider.get().create(RequestApi.class);
        Map<String, RequestBody> request = new HashMap<>();
        request.put("userId", RequestBody.create(null, userId + ""));
        request.put("postId", RequestBody.create(null, postId + ""));
        execute(api.create("favort", request), observer);
    }

    @NonNull
    public void deleteFavort(int postId, int userId, Observer<ReturnMsg> observer) {
        RequestApi api = RetrofitProvider.get().create(RequestApi.class);
        Map<String, RequestBody> request = new HashMap<>();
        request.put("userId", RequestBody.create(null, userId + ""));
        request.put("postId", RequestBody.create(null, postId + ""));
        execute(api.delete("favort", request), observer);
    }

    @NonNull
    private void execute(Observable<ReturnMsg> observeable, Observer<ReturnMsg> observer) {

        /**
         * RxJava将这个操作符实现为repeat方法。它不是创建一个Observable，
         * 而是重复发射原始Observable的数据序列，这个序列或者是无限的，或者通过repeat(n)指定重复次数
         */
        observeable.repeat(1);

        observeable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ReturnMsg>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ReturnMsg returnMsg) {
                        Log.e(TAG, "onNext: 返回的数据+" + returnMsg.toString());
                        /**
                         * 判断是否要登录操作
                         */
                        if (returnMsg.getIs() != RequestApi.TOLOGIN) {
                            Observable.just(returnMsg).subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(observer);

                        } else {
                            Log.e(TAG, "accept: 用户还没有登录:>>>" + returnMsg.getIs());
                            /**
                             * 用户还没有登录 ？
                             */
                            RequestApi requestApi = RetrofitProvider.get().create(RequestApi.class);
                            //重新登录
                            requestApi.login(UserDao.getInstance().getMobile(), UserDao.getInstance().getPwd())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(r -> {
                                        Log.e(TAG, "accept: 重新登录:>>>" + r.getIs());
                                        //登录成功
                                        if (r.getIs() == RequestApi.SUCCESS) {
                                            //保存用户信息
                                            UserDao.getInstance().saveUser(GsonTools.changeGsonToBean(r.getData(), UserBean.class));
                                            /**
                                             * 再次发起上一次操作请求
                                             */
                                            observeable
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(observer);
                                        } else {
                                            Observable.just(r).observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(observer);
                                        }
                                    }, throwable -> {
                                        observer.onError(throwable);
                                        /*Observable.create(new ObservableOnSubscribe<ReturnMsg>() {
                                            @Override
                                            public void subscribe(ObservableEmitter<ReturnMsg> e) throws Exception {
                                                e.onError(throwable);
                                            }
                                        }).subscribe(observer);*/
                                    });
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: 出错啦" + e.getMessage());
                        observer.onError(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


}

package com.guoyi.circle.request;

import com.guoyi.circle.been.ReturnMsg;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.PartMap;
import retrofit2.http.Path;


/**
 * Created by Credit on 2017/2/28.
 * <p>
 * 访问服务器接口
 */

public interface RequestApi {

    static int SUCCESS = 100;
    static int FAIL = 200;
    static int ERROR = 300;
    static int TOLOGIN = 400;


    @POST("/login")
    @FormUrlEncoded
    Observable<ReturnMsg> login(@Field("mobile") String mobile,
                                @Field("pwd") String pwd
    );

    @POST("/register")
    @FormUrlEncoded
    Observable<ReturnMsg> register(@Field("mobile") String mobile,
                                   @Field("pwd") String pwd
    );

    @POST("/logout")
    Observable<ReturnMsg> logout();

    @POST("/api/post/create/{path}")
    @Multipart
    Observable<ReturnMsg> create(@Path("path") String path, @PartMap Map<String, RequestBody> params);

    @POST("/api/post/delete/{path}")
    @Multipart
    Observable<ReturnMsg> delete(@Path("path") String path, @PartMap Map<String, RequestBody> params);

    @PUT("/api/post/update/{path}")
    @Multipart
    Observable<ReturnMsg> update(@Path("path") String path, @PartMap Map<String, RequestBody> params);

    @POST("/api/post/read/{path}")
    @Multipart
    Observable<ReturnMsg> get(@Path("path") String path, @PartMap Map<String, RequestBody> params);


    /********************************************
     * 下面的接口已屏蔽，为了使用RESTful 规则，建议使用上面的接口
     ******************************************/

   /* @POST("/postList")
    @FormUrlEncoded
    Observable<ReturnMsg> postList(@Field("pageIndex") int pageIndex,
                                   @Field("pageSize") int pageSize
    );

    @POST("/favortOp")
    @FormUrlEncoded
    Observable<ReturnMsg> favortOp(@Field("postId") int postId,
                                   @Field("userId") int userId,
                                   @Field("op") String op
    );

    @POST("/deletePost")
    @FormUrlEncoded
    Observable<ReturnMsg> deletePost(@Field("postId") int postId,
                                     @Field("userId") int userId
    );

    @POST("/deleteComment")
    @FormUrlEncoded
    Observable<ReturnMsg> deleteComment(@Field("commentId") int commentId);

    //content string, cType int, userId int, touserId int, postId int
    @POST("/addComment")
    @FormUrlEncoded
    Observable<ReturnMsg> addComment(@Field("content") String content,
                                     @Field("cType") int cType,
                                     @Field("userId") int userId,
                                     @Field("touserId") int touserId,
                                     @Field("postId") int postId


    );

    @POST("/addUrlPost")
    @Multipart
    Observable<ReturnMsg> addUrlPost(@PartMap Map<String, RequestBody> params);

    @POST("/addVideoPost")
    @Multipart
    Observable<ReturnMsg> addVideoPost(@PartMap Map<String, RequestBody> params);


    @POST("/addPost")
    @Multipart
    Observable<ReturnMsg> addPost(@PartMap Map<String, RequestBody> params);*/

}

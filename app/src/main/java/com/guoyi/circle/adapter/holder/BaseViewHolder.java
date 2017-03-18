package com.guoyi.circle.adapter.holder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.guoyi.circle.adapter.OnRecyclerViewListener;

import java.util.List;



public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder implements View.OnClickListener {

    protected static final String TAG = "BaseViewHolder";
    protected Context mContext; //上下文
    protected View rootView = null; //根view
    protected int viewType;      //view类型
    protected int position;      //view item位置

    /**
     * 点击事件
     */
    protected OnRecyclerViewListener listener;

    /**
     * 绑定数据
     */
    protected T mdata;
    /**
     * 绑定数据集合
     */
    protected List<T> listdata;


    /**
     * 构造器
     *
     * @param itemView view文件
     */
    public BaseViewHolder(View itemView) {
        super(itemView);
        rootView = itemView;
        initView(rootView);
        initEvent();
    }

    /**
     * @param itemView view文件
     * @param viewType view类型
     */
    public BaseViewHolder(View itemView, int viewType) {
        super(itemView);
        rootView = itemView;
        this.viewType = viewType;
        initView(rootView);
        initEvent();

    }

    public BaseViewHolder(View itemView, Context mContext, int viewType, OnRecyclerViewListener listener) {
        super(itemView);
        this.mContext = mContext;
        this.viewType = viewType;
        this.listener = listener;
        rootView = itemView;
        initView(rootView);
        initEvent();
    }

    /**
     * @param context   上下文
     * @param root      父view
     * @param layoutRes layout R文件
     * @param l
     */
    public BaseViewHolder(Context context, ViewGroup root, int layoutRes, OnRecyclerViewListener l) {
        super(getInflateView(context, root, layoutRes, false));
        rootView = itemView;
        this.mContext = context;
        this.listener = l;
        initView(rootView);
        initEvent();
    }


    public BaseViewHolder(Context context, ViewGroup root, int layoutRes, int viewType, OnRecyclerViewListener l) {
        super(getInflateView(context, root, layoutRes, false));
        rootView = itemView;
        this.mContext = context;
        this.viewType = viewType;
        this.listener = l;
        initView(rootView);
        initEvent();
    }

    /**
     * 获取上下文
     *
     * @return
     */
    public Context getmContext() {
        return this.mContext;
    }

    /**
     * 绑定数据
     *
     * @param t
     */
    public void bindData(T t) {
        this.mdata = t;
    }

    /**
     * 绑定数据
     *
     * @param t
     * @param postion
     */
    public void bindData(T t, int postion) {
        this.mdata = t;
        this.position = postion;
    }

    /**
     * 绑定集合数据
     *
     * @param data
     * @param p
     */
    public void bindListData(List<T> data, int p) {
        this.listdata = data;
        this.position = p;
    }


    public int getViewType() {
        return viewType;
    }

    public void resetView() {

    }

    /**
     * 初始化View
     *
     * @param rootView
     */
    public abstract void initView(View rootView);

    /**
     * 初始事件
     */
    public abstract void initEvent();


    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onItemClick(v, position, this);
        }
    }

    /**
     * 启动指定Activity
     *
     * @param target
     * @param bundle
     */
    public void startActivity(Class<? extends Activity> target, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(getmContext(), target);
        if (bundle != null)
            intent.putExtra(getmContext().getPackageName(), bundle);
        getmContext().startActivity(intent);
    }

    public T getMdata() {
        return mdata;
    }

    public List<T> getListdata() {
        return listdata;
    }

    public static View getInflateView(Context context, ViewGroup root, int layoutRes, boolean praent) {
        View inflate = LayoutInflater.from(context).inflate(layoutRes, root, praent);
        return inflate;
    }
}
package com.guoyi.circle.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Credit on 2017/1/11.
 */

public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    /**
     * 集合数据
     */
    protected List<T> mData;
    /**
     * 上下文
     */
    protected Context mContext;
    /**
     * 回调事件
     */
    protected OnRecyclerViewListener listener;

    public BaseRecyclerAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public BaseRecyclerAdapter(Context mContext, List<T> mData) {
        this.mData = mData;
        this.mContext = mContext;
    }

    public BaseRecyclerAdapter(Context mContext, OnRecyclerViewListener listener) {
        this.mContext = mContext;
        this.listener = listener;
    }

    public BaseRecyclerAdapter(List<T> mData, Context mContext, OnRecyclerViewListener listener) {
        this.mData = mData;
        this.mContext = mContext;
        this.listener = listener;
    }

    /**
     * 追加集合数据
     *
     * @param d
     */
    public void addData(List<T> d) {
        if (mData == null) {
            mData = new ArrayList<>();
        }
        this.mData.addAll(d);
        this.notifyDataSetChanged();
    }

    /**
     * 追加一条数据
     *
     * @param d
     */
    public void addData(T d) {
        if (mData == null) {
            mData = new ArrayList<>();
        }
        this.mData.add(d);
        this.notifyDataSetChanged();
    }

    /**
     * 追加一条数据
     *
     * @param d
     */
    public void addDataFirst(T d) {
        if (mData == null) {
            mData = new ArrayList<>();
        }
        this.mData.add(0, d);
        this.notifyDataSetChanged();
    }

    /**
     * 设置一条数据
     *
     * @param data
     */
    public void setOneData(T data) {
        ArrayList<T> l = new ArrayList<>();
        l.add(data);
        changeData(l);
    }

    /**
     * 改变数据
     *
     * @param d
     */
    public void changeData(List<T> d) {
        this.mData = d;
        if (this.mData == null) {
            this.mData = new ArrayList<>();
        }
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    /**
     * 清空数据
     */
    public void clearData() {

        if (this.mData != null) {
            this.mData.clear();
            this.notifyDataSetChanged();
        }
    }

    /**
     * 删除指定位置数据
     *
     * @param position
     */
    public void deleteData(int position) {
        if (this.mData != null && this.mData.size() > position) {
            this.mData.remove(position);
            this.notifyDataSetChanged();
        }
    }

    /**
     * 获取指定位置数据
     *
     * @param position
     * @return
     */
    public T getPositionData(int position) {
        if (this.mData != null && this.mData.size() > position) {
            return this.mData.get(position);
        }
        return null;
    }

    /**
     * 设置回调事件
     *
     * @param listener
     */
    public void setListener(OnRecyclerViewListener listener) {
        this.listener = listener;
    }

    /**
     * 回收资源
     */
    public void destroyData() {
        if (this.mData != null) {
            this.mData.clear();
        }
        this.mData = null;
        this.listener = null;
        this.mContext = null;
    }

    public List<T> getmData() {
        return mData;
    }
}

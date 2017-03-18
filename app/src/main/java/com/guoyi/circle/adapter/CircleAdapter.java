package com.guoyi.circle.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.guoyi.circle.R;
import com.guoyi.circle.adapter.holder.BaseViewHolder;
import com.guoyi.circle.adapter.holder.HeadVIewHolder;
import com.guoyi.circle.adapter.holder.ImageViewHolder;
import com.guoyi.circle.adapter.holder.UrlViewHolder;
import com.guoyi.circle.adapter.holder.VideoViewHolder;
import com.guoyi.circle.been.PostBean;
import com.guoyi.circle.mvp.presenter.CirclePresenter;
import com.guoyi.circle.ui.videolist.visibility.items.ListItem;
import com.guoyi.circle.ui.videolist.visibility.scroll.ItemsProvider;

/**
 * Created by Credit on 2017/3/2.
 */

public class CircleAdapter extends BaseRecyclerAdapter<PostBean> implements ItemsProvider {

    public final static int HEAD_TYPE = 100;

    public final static int TYPE_IMG = 1;
    public final static int TYPE_URL = 2;
    public final static int TYPE_VIDEO = 3;


    public static final int HEADVIEW_SIZE = 1;

    private CirclePresenter presenter;
    private RecyclerView mRecylerView;

    public CircleAdapter(Context mContext, CirclePresenter presenter, RecyclerView mRecylerView) {
        super(mContext);
        this.presenter = presenter;
        this.mRecylerView = mRecylerView;
    }

    /*   public CircleAdapter(Context mContext, CirclePresenter presenter) {
           super(mContext);
           this.presenter = presenter;
       }
   */
    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEAD_TYPE;
        }
        return mData.get(position - HEADVIEW_SIZE).getType();
    }

    @Override
    public int getItemCount() {
        return mData == null ? 1 : mData.size() + HEADVIEW_SIZE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEAD_TYPE) {
            return new HeadVIewHolder(mContext, parent, R.layout.head_circle, listener);
        } else if (viewType == TYPE_URL) {
            return new UrlViewHolder(mContext, parent, R.layout.adapter_circle_item, viewType, presenter, listener);
        } else if (viewType == TYPE_VIDEO) {
            return new VideoViewHolder(mContext, parent, R.layout.adapter_circle_item, viewType, presenter, listener);
        } else {
            return new ImageViewHolder(mContext, parent, R.layout.adapter_circle_item, viewType, presenter, listener);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BaseViewHolder baseViewHolder = (BaseViewHolder) holder;

        if (position >= HEADVIEW_SIZE) {
            baseViewHolder.bindData(mData.get(position - HEADVIEW_SIZE), position - HEADVIEW_SIZE);
        } else {
            baseViewHolder.bindData(null, position);
        }

    }

    @Override
    public ListItem getListItem(int position) {
        RecyclerView.ViewHolder holder = mRecylerView.findViewHolderForAdapterPosition(position);
        if (holder instanceof ListItem) {
            return (ListItem) holder;
        }
        return null;
    }

    @Override
    public int listItemSize() {
        return getItemCount();
    }
}

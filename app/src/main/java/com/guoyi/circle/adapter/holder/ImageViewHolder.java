package com.guoyi.circle.adapter.holder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.guoyi.circle.R;
import com.guoyi.circle.activity.ImagePagerActivity;
import com.guoyi.circle.adapter.OnRecyclerViewListener;
import com.guoyi.circle.been.ImageBean;
import com.guoyi.circle.been.PostBean;
import com.guoyi.circle.mvp.presenter.CirclePresenter;
import com.guoyi.circle.ui.MultiImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Credit on 2017/3/3.
 */

public class ImageViewHolder extends BaseCircleHolder {
    private MultiImageView multiImageView;

    public ImageViewHolder(Context context, ViewGroup root, int layoutRes, int viewType, CirclePresenter circlePresenter, OnRecyclerViewListener l) {
        super(context, root, layoutRes, viewType, circlePresenter, l);
    }

    @Override
    public void initViewStub() {
        if (mViewStub == null) {
            throw new IllegalArgumentException("viewStub is null...");
        }
        mViewStub.setLayoutResource(R.layout.viewstub_imgbody);
        View subView = mViewStub.inflate();
        MultiImageView multiImageView = (MultiImageView) subView.findViewById(R.id.multiImagView);
        if (multiImageView != null) {
            this.multiImageView = multiImageView;
        }

    }

    @Override
    public void bindData(PostBean circleItem, int postion) {
        super.bindData(circleItem, postion);
        if (mdata != null) {
            final List<ImageBean> photos = circleItem.getImages();
            if (photos != null && photos.size() > 0) {
                multiImageView.setVisibility(View.VISIBLE);
                multiImageView.setList(photos);
                multiImageView.setOnItemClickListener(new MultiImageView.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        //imagesize是作为loading时的图片size
                        ImagePagerActivity.ImageSize imageSize = new ImagePagerActivity.ImageSize(view.getMeasuredWidth(), view.getMeasuredHeight());

                        List<String> photoUrls = new ArrayList<String>();
                        for (ImageBean photoInfo : photos) {
                            photoUrls.add(photoInfo.getUrl());
                        }
                        ImagePagerActivity.startImagePagerActivity(mContext, photoUrls, position, imageSize);


                    }
                });
            } else {
                multiImageView.setVisibility(View.GONE);
            }
        }
    }
}

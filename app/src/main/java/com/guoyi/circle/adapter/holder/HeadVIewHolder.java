package com.guoyi.circle.adapter.holder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.guoyi.circle.R;
import com.guoyi.circle.adapter.OnRecyclerViewListener;
import com.guoyi.circle.been.UserBean;
import com.guoyi.circle.dao.UserDao;
import com.guoyi.circle.utils.StringUtils;

/**
 * Created by Credit on 2017/3/2.
 */

public class HeadVIewHolder extends BaseViewHolder {

    private ImageView mBgIv;
    private ImageView mHeadIv;
    private TextView mNameTv;

    public HeadVIewHolder(Context context, ViewGroup root, int layoutRes, OnRecyclerViewListener l) {
        super(context, root, layoutRes, l);
    }

    @Override
    public void initView(View rootView) {
        mBgIv = (ImageView) rootView.findViewById(R.id.bgIv);
        mHeadIv = (ImageView) rootView.findViewById(R.id.headIv);
        mNameTv = (TextView) rootView.findViewById(R.id.nameTv);
    }

    @Override
    public void bindData(Object o, int postion) {
        super.bindData(o, postion);
        UserBean user = UserDao.getInstance().getUser();
        if (user != null) {
            Glide.with(mContext).load(StringUtils.getImageDefaultURL(user.getPic())).into(mHeadIv);
        }
        mNameTv.setText(user.getName());

    }

    @Override
    public void initEvent() {

    }
}

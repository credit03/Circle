package com.guoyi.circle.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.guoyi.circle.R;
import com.guoyi.circle.utils.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class ImagePagerActivity extends AppCompatActivity {
    public static final String INTENT_IMGURLS = "imgurls";
    public static final String INTENT_POSITION = "position";
    public static final String INTENT_IMAGESIZE = "imagesize";
    public static final String INTENT_DELETE = "delete";
    private static String TAG = "ImagePagerActivity";

    private List<View> guideViewList = new ArrayList<View>();
    private LinearLayout guideGroup;
    public ImageSize imageSize;
    private int startPos;
    private ArrayList<String> imgUrls;

    private TextView tv_count;
    private ImageView close;

    private Toolbar toolbar;

    private ImageAdapter mAdapter;

    private boolean showDelete = false;

    public static void startImagePagerActivity(Context context, List<String> imgUrls, int position, ImageSize imageSize) {
        Intent intent = new Intent(context, ImagePagerActivity.class);
        intent.putStringArrayListExtra(INTENT_IMGURLS, new ArrayList<String>(imgUrls));
        intent.putExtra(INTENT_POSITION, position);
        intent.putExtra(INTENT_IMAGESIZE, imageSize);
        context.startActivity(intent);

    }

    public static void startImagePagerActivityForResult(Activity activity, int requestCode, List<String> imgUrls, int position, ImageSize imageSize, boolean showdeletebtn) {
        Intent intent = new Intent(activity, ImagePagerActivity.class);
        intent.putStringArrayListExtra(INTENT_IMGURLS, new ArrayList<String>(imgUrls));
        intent.putExtra(INTENT_POSITION, position);
        intent.putExtra(INTENT_IMAGESIZE, imageSize);
        intent.putExtra(INTENT_DELETE, showdeletebtn);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagepager);
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        guideGroup = (LinearLayout) findViewById(R.id.guideGroup);
        getIntentData();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        close = (ImageView) findViewById(R.id.close);
        tv_count = (TextView) findViewById(R.id.tv_count);

        if (showDelete) {
            close.setImageResource(R.mipmap.delete_btn);
            this.setSupportActionBar(toolbar);

            ActionBar bar = this.getSupportActionBar();
            if (bar != null) {
                bar.setDisplayHomeAsUpEnabled(true);
            }
            toolbar.setNavigationOnClickListener(view -> {
                onBackPressed();
            });

        }

        for (String s : imgUrls) {
            Log.e(TAG, " url:" + s);
        }

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (showDelete) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ImagePagerActivity.this);
                    builder.setTitle("是否删除该照片");
                    builder.setNegativeButton("确定", (d, p) -> {
                        d.dismiss();
                        int currentItem = viewPager.getCurrentItem();
                        Log.e(TAG, "onClick: 删除位置>>:" + currentItem + " url:" + imgUrls.get(currentItem));
                        imgUrls.remove(currentItem);

                        if (imgUrls.size() == 0) {
                            onBackPressed();
                            return;
                        }

                        if (currentItem >= imgUrls.size()) {
                            currentItem--;
                        }
                       /* mAdapter.setDatas(imgUrls);
                        mAdapter.notifyDataSetChanged();
                        viewPager.refreshDrawableState();
                        viewPager.requestLayout();*/

                        viewPager.removeAllViews();
                        mAdapter = new ImageAdapter(ImagePagerActivity.this, showDelete);
                        mAdapter.setDatas(imgUrls);
                        mAdapter.setImageSize(imageSize);
                        viewPager.setAdapter(mAdapter);

                        viewPager.setCurrentItem(currentItem);
                        tv_count.setText(String.format("%1$d/%2$d张", currentItem + 1, imgUrls.size()));
                        addGuideView(guideGroup, currentItem, imgUrls);
                    });
                    builder.setNeutralButton("取消", null);
                    builder.show();


                } else {
                    ImagePagerActivity.this.finish();
                }
            }
        });

        tv_count.setText(String.format("%1$d/%2$d张", startPos + 1, imgUrls.size()));


        mAdapter = new ImageAdapter(this, showDelete);
        mAdapter.setDatas(imgUrls);
        mAdapter.setImageSize(imageSize);
        viewPager.setAdapter(mAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //%2$s
                tv_count.setText(String.format("%1$d/%2$d张", position + 1, imgUrls.size()));
                for (int i = 0; i < guideViewList.size(); i++) {
                    guideViewList.get(i).setSelected(i == position ? true : false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(startPos);

        addGuideView(guideGroup, startPos, imgUrls);

    }


    private void getIntentData() {
        startPos = getIntent().getIntExtra(INTENT_POSITION, 0);
        imgUrls = getIntent().getStringArrayListExtra(INTENT_IMGURLS);
        imageSize = (ImageSize) getIntent().getSerializableExtra(INTENT_IMAGESIZE);
        showDelete = getIntent().getBooleanExtra(INTENT_DELETE, false);
    }

    private void addGuideView(LinearLayout guideGroup, int startPos, ArrayList<String> imgUrls) {
        guideGroup.removeAllViews();
        if (imgUrls != null && imgUrls.size() > 0) {
            guideViewList.clear();
            for (int i = 0; i < imgUrls.size(); i++) {
                View view = new View(this);
                view.setBackgroundResource(R.drawable.selector_guide_bg);
                view.setSelected(i == startPos ? true : false);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.gudieview_width),
                        getResources().getDimensionPixelSize(R.dimen.gudieview_heigh));
                layoutParams.setMargins(10, 0, 0, 0);
                guideGroup.addView(view, layoutParams);
                guideViewList.add(view);
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            return super.dispatchTouchEvent(ev);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static class ImageAdapter extends PagerAdapter {

        private List<String> datas = null;
        private LayoutInflater inflater;
        private Context context;
        private ImageSize imageSize;
        private ImageView smallImageView = null;
        private boolean isLocal = false;

        public void setDatas(List<String> datas) {

            this.datas = new ArrayList<>();
            if (datas != null)
                this.datas.addAll(datas);
        }

        public void setImageSize(ImageSize imageSize) {
            this.imageSize = imageSize;
        }

        public ImageAdapter(Context context, boolean isLocal) {
            this.context = context;
            this.isLocal = isLocal;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            if (datas == null) return 0;
            return datas.size();
        }


        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = inflater.inflate(R.layout.item_pager_image, container, false);
            if (view != null) {
                final ImageView imageView = (ImageView) view.findViewById(R.id.image);

                if (imageSize != null) {
                    //预览imageView
                    smallImageView = new ImageView(context);
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(imageSize.getWidth(), imageSize.getHeight());
                    layoutParams.gravity = Gravity.CENTER;
                    smallImageView.setLayoutParams(layoutParams);
                    smallImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    ((FrameLayout) view).addView(smallImageView);
                }

                //alreadyLoading
                final ProgressBar loading = new ProgressBar(context);
                FrameLayout.LayoutParams loadingLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                loadingLayoutParams.gravity = Gravity.CENTER;
                loading.setLayoutParams(loadingLayoutParams);
                ((FrameLayout) view).addView(loading);

                String imgurl = datas.get(position);
                loading.setVisibility(View.VISIBLE);
                DrawableTypeRequest<String> load;
                if (isLocal) {
                    load = Glide.with(context)
                            .load(imgurl);
                } else {
                    load = Glide.with(context)
                            .load(StringUtils.getImageDefaultURL(imgurl));
                }
                load.diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(new GlideDrawableImageViewTarget(imageView) {
                            @Override
                            public void onLoadStarted(Drawable placeholder) {
                                super.onLoadStarted(placeholder);
                               /* if(smallImageView!=null){
                                    smallImageView.setVisibility(View.VISIBLE);
                                    Glide.with(context).load(imgurl).into(smallImageView);
                                }*/
                                loading.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                super.onLoadFailed(e, errorDrawable);
                                /*if(smallImageView!=null){
                                    smallImageView.setVisibility(View.GONE);
                                }*/
                                loading.setVisibility(View.GONE);
                            }

                            @Override
                            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                                super.onResourceReady(resource, animation);
                                loading.setVisibility(View.GONE);
                                /*if(smallImageView!=null){
                                    smallImageView.setVisibility(View.GONE);
                                }*/
                            }
                        });

                container.addView(view, 0);
            }
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }


    }

    @Override
    public void onBackPressed() {

        if (showDelete) {
            Intent intent = new Intent();
            intent.putStringArrayListExtra(INTENT_IMGURLS, imgUrls);
            this.setResult(RESULT_OK, intent);
        }
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onDestroy() {
        guideViewList.clear();
        super.onDestroy();
    }

    public static class ImageSize implements Serializable {

        private int width;
        private int height;

        public ImageSize(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }
    }
}

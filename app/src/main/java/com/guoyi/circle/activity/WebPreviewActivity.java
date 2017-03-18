package com.guoyi.circle.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.guoyi.circle.R;
import com.guoyi.circle.been.PostBean;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class WebPreviewActivity extends AppCompatActivity {

    @InjectView(R.id.activity_web_preview)
    RelativeLayout mActivityWebPreview;
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.progressBar)
    ProgressBar mProgressBar;
    @InjectView(R.id.webview)
    WebView webView;
    private PostBean bean;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_preview);
        ButterKnife.inject(this);
        bean = (PostBean) getIntent().getSerializableExtra("PostBean");

        this.setSupportActionBar(mToolbar);
        ActionBar bar = this.getSupportActionBar();
        if (bar != null) {
            bar.setDisplayShowTitleEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
        }

        mProgressBar.setMax(100);
        mProgressBar.setProgress(0);

        if (bean != null) {
            bar.setTitle(bean.getLinkTitle());
            setWebSettingStr();
        }
        mToolbar.setNavigationOnClickListener(view -> {
            onBackPressed();
        });
    }


    @SuppressLint("AddJavascriptInterface")
    public void setWebSettingStr() {

        /**
         * WebSettings常用方法：
         setAllowFileAccess 启用或禁止WebView访问文件数据
         setBlockNetworkImage 是否显示网络图像
         setBuiltInZoomControls 设置是否支持缩放
         setCacheMode 设置缓冲的模式
         setDefaultFontSize 设置默认的字体大小
         setDefaultTextEncodingName 设置在解码时使用的默认编码
         setFixedFontFamily 设置固定使用的字体
         setJavaSciptEnabled 设置是否支持Javascript
         setLayoutAlgorithm 设置布局方式
         setLightTouchEnabled 设置用鼠标激活被选项
         setSupportZoom 设置是否支持变焦



         */
        WebSettings settings = webView.getSettings();
        //setPluginsEnabled(true);  //支持插件
        //webview webSettings.setBuiltInZoomControls(true); //设置支持缩放
        settings.setAllowFileAccess(true);//设置可以访问文件

        settings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        settings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); //支持内容重新布局
        settings.setSupportZoom(true); //支持缩放

        settings.setJavaScriptEnabled(true);  //支持js
        settings.setJavaScriptCanOpenWindowsAutomatically(true); ////支持通过JS打开新窗口
        synCookies(this);

        //设置进度条
        webView.setWebChromeClient(new MyWebChromeClient());

        //webView 点击连接如何不让跳转到系统的 浏览器
        webView.setWebViewClient(new MyWebViewClient());
        webView.loadUrl(bean.getLinkUrl());
    }

    public static void synCookies(Context paramContext) {
        CookieSyncManager.createInstance(paramContext);
        CookieManager.getInstance();
        CookieSyncManager.getInstance().sync();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private class MyWebViewClient extends WebViewClient {

        /**
         * WebViewClient常用方法：
         * doUpdate VisitedHistory 更新历史记录
         * onFormResubmission 应用程序重新请求网页数据
         * onLoadResource 加载指定地址提供的资源
         * onPageFinished 网页加载完毕
         * onPageStarted 网页开始加载
         * onReceivedError 报告错误信息
         * onScaleChanged WebView发生改变
         * shouldOverrideUrlLoading 控制新的连接在当前WebView中打开
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return false;
        }
    }

    private class MyWebChromeClient extends WebChromeClient {
        /**
         * WebChromeClient常用方法：
         * onCloseWindow 关闭WebView
         * onCreateWindow 创建WebView
         * onJsAlert 处理Javascript中的Alert对话框
         * onJsConfirm处理Javascript中的Confirm对话框
         * onJsPrompt处理Javascript中的Prompt对话框
         * onProgressChanged 加载进度条改变
         * onReceivedlcon 网页图标更改
         * onReceivedTitle 网页Title更改
         * onRequestFocus WebView显示焦点
         *
         * @param view
         * @param newProgress
         */
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                mProgressBar.setVisibility(View.GONE);
                return;
            }
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBar.setProgress(newProgress);
            // super.onProgressChanged(view, newProgress);
        }


    }
}

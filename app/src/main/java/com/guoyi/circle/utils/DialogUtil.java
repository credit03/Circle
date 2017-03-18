package com.guoyi.circle.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.guoyi.circle.R;


public class DialogUtil {


    private static Dialog dialog;
    private static ImageView ivsuccesss;
    private static ProgressBar progressBar;
    private static TextView mText;
    private static Button restart;


    private static MyDelayHander hander;
    private static boolean CancelableDismiss = true;

    /**
     * 显示加载toast
     *
     * @param paramContext 上下文
     * @param paramString  显示内容
     * @param OnTouchOut   触摸是否关闭Dialog
     */
    public static synchronized void showToastLoadingDialog(Context paramContext, String paramString, boolean OnTouchOut) {

        if (dialog != null) {
            dialog.dismiss();
        }
        CancelableDismiss = true;
        View localView = LayoutInflater.from(paramContext).inflate(R.layout.layout_toast_load, null);
        progressBar = (ProgressBar) localView.findViewById(R.id.progressBar);
        ivsuccesss = (ImageView) localView.findViewById(R.id.ivSuccess);
        restart = (Button) localView.findViewById(R.id.btn_next);
        mText = (TextView) localView.findViewById(R.id.text);
        mText.setText(paramString);
        progressBar.setVisibility(View.VISIBLE);
        ivsuccesss.setVisibility(View.GONE);
        dialog = new Dialog(paramContext, R.style.loading_dialog);
        dialog.setCanceledOnTouchOutside(OnTouchOut);
        dialog.setCancelable(true);
        dialog.setContentView(localView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        dialog.show();
    }


    public static void showToastSureBtnErrDialog(String msg, String btnmsg) {
        if (ivsuccesss != null && progressBar != null && mText != null) {
            ivsuccesss.setImageResource(R.mipmap.ic_error_red_48dp);
            ivsuccesss.setVisibility(View.VISIBLE);
            restart.setVisibility(View.VISIBLE);
            restart.setText(btnmsg);
            progressBar.setVisibility(View.GONE);
            dialog.setCanceledOnTouchOutside(false);
            mText.setText(msg);
        }
    }

    public static void showToastErrDialog(String msg) {
        if (ivsuccesss != null && progressBar != null && mText != null) {
            ivsuccesss.setImageResource(R.mipmap.ic_error_red_48dp);
            ivsuccesss.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            dialog.setCanceledOnTouchOutside(true);
            mText.setText(msg);
        }
    }

    public static synchronized void showToastClickLoadingDialog(Context paramContext, String paramString, boolean OnTouchOut, final View.OnClickListener listener) {

        if (dialog != null) {
            dialog.dismiss();
        }
        CancelableDismiss = true;
        View localView = LayoutInflater.from(paramContext).inflate(R.layout.layout_toast_load, null);
        progressBar = (ProgressBar) localView.findViewById(R.id.progressBar);
        ivsuccesss = (ImageView) localView.findViewById(R.id.ivSuccess);
        restart = (Button) localView.findViewById(R.id.btn_next);


        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissToatLoadingDialog();
                if (listener != null) {
                    listener.onClick(v);
                }
            }
        });

        mText = (TextView) localView.findViewById(R.id.text);
        mText.setText(paramString);
        progressBar.setVisibility(View.VISIBLE);
        ivsuccesss.setVisibility(View.GONE);
        dialog = new Dialog(paramContext, R.style.loading_dialog);
        dialog.setCanceledOnTouchOutside(OnTouchOut);
        dialog.setCancelable(true);
        dialog.setContentView(localView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        dialog.show();
    }


    /**
     * 显示出错定时自动关闭窗口
     *
     * @param paramContext
     * @param paramString
     * @param OnTouchOut
     */
    public static synchronized void showErrDelayDismissDialog(Context paramContext, int delay, String paramString, boolean OnTouchOut) {
        if (dialog != null) {
            dialog.dismiss();
        }
        CancelableDismiss = true;
        View localView = LayoutInflater.from(paramContext).inflate(R.layout.layout_toast_load, null);
        progressBar = (ProgressBar) localView.findViewById(R.id.progressBar);
        ivsuccesss = (ImageView) localView.findViewById(R.id.ivSuccess);
        restart = (Button) localView.findViewById(R.id.btn_next);
        mText = (TextView) localView.findViewById(R.id.text);
        mText.setText(paramString);
        progressBar.setVisibility(View.GONE);
        ivsuccesss.setVisibility(View.VISIBLE);
        ivsuccesss.setImageResource(R.mipmap.ic_error_red_48dp);
        dialog = new Dialog(paramContext, R.style.loading_dialog);
        dialog.setCanceledOnTouchOutside(OnTouchOut);
        dialog.setCancelable(true);
        dialog.setContentView(localView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        dialog.show();
        getDelayHander().sendEmptyMessageDelayed(200, delay);

    }

    /**
     * 显示出错确定关闭窗口
     *
     * @param paramContext
     * @param paramString
     * @param OnTouchOut
     */
    public static synchronized void showErrBtnDismissDialog(Context paramContext, String paramString, boolean OnTouchOut) {
        if (dialog != null) {
            dialog.dismiss();
        }
        CancelableDismiss = true;
        View localView = LayoutInflater.from(paramContext).inflate(R.layout.layout_toast_load, null);
        progressBar = (ProgressBar) localView.findViewById(R.id.progressBar);
        ivsuccesss = (ImageView) localView.findViewById(R.id.ivSuccess);
        restart = (Button) localView.findViewById(R.id.btn_next);
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissToatLoadingDialog();
            }
        });
        restart.setText("关闭");
        restart.setVisibility(View.VISIBLE);
        mText = (TextView) localView.findViewById(R.id.text);
        mText.setText(paramString);
        progressBar.setVisibility(View.GONE);
        ivsuccesss.setVisibility(View.VISIBLE);
        ivsuccesss.setImageResource(R.mipmap.ic_error_red_48dp);
        dialog = new Dialog(paramContext, R.style.loading_dialog);
        dialog.setCanceledOnTouchOutside(OnTouchOut);
        dialog.setCancelable(true);
        dialog.setContentView(localView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        dialog.show();

    }


    public static MyDelayHander getDelayHander() {
        if (hander == null) {
            hander = new MyDelayHander();
        }
        return hander;
    }


    private static class MyDelayHander extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 100) { //显示出错窗口
                if (ivsuccesss != null && progressBar != null && mText != null) {
                    if (msg.arg2 == 1) {
                        ivsuccesss.setImageResource(R.mipmap.bg_toast_load_success);
                    } else if (msg.arg2 == 2) {
                        ivsuccesss.setImageResource(R.mipmap.ic_error_red_48dp);
                    }
                    ivsuccesss.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    mText.setText((String) msg.obj);
                    CancelableDismiss = false;
                    this.sendEmptyMessageDelayed(200, msg.arg1);
                }
            } else if (msg.what == 200) { //关闭窗口
                CancelableDismiss = true;
                dismissToatLoadingDialog();
            }
        }
    }

    public static synchronized void dismissToastDelayLoadingDialog(String msg, boolean err) {
        dismissToastDelayLoadingDialog(1500, err, msg);
    }


    /**
     * 延时关闭窗口
     *
     * @param delaydismiss 延时时间
     * @param err          显示出错
     * @param str          提示语
     */
    public static synchronized void dismissToastDelayLoadingDialog(int delaydismiss, boolean err, String str) {
        Message msg = Message.obtain();
        msg.what = 100;
        if (delaydismiss == 0) {
            delaydismiss = 1500;
        }
        msg.arg1 = delaydismiss;
        if (err) {
            msg.arg2 = 2; //加载失败图标
        } else {
            msg.arg2 = 1; //加载成功图标
        }
        msg.obj = str;   //提示语
        getDelayHander().sendMessage(msg);
    }

    public static synchronized void dismissToatLoadingDialog() {
        if (CancelableDismiss) {
            if (dialog != null) {
                dialog.dismiss();
            }
            dialog = null;
            progressBar = null;
            ivsuccesss = null;
            mText = null;
        }

    }


}

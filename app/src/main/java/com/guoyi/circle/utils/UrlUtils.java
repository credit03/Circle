package com.guoyi.circle.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;

import com.guoyi.circle.activity.WebPreviewActivity;
import com.guoyi.circle.been.PostBean;
import com.guoyi.circle.spannable.SpannableClickable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by suneee on 2016/6/2.
 */
public class UrlUtils {

    /**
     * String格式化Url TEL,
     *
     * @param contentStr
     * @return
     */
    public static SpannableStringBuilder formatUrlString(String contentStr) {

        SpannableStringBuilder sp;
        if (!TextUtils.isEmpty(contentStr)) {

            sp = new SpannableStringBuilder(contentStr);
            try {
                //处理url匹配
                Pattern urlPattern = Pattern.compile("(http|https|ftp|svn)://([a-zA-Z0-9]+[/?.?])" +
                        "+[a-zA-Z0-9]*\\??([a-zA-Z0-9]*=[a-zA-Z0-9]*&?)*");
                Matcher urlMatcher = urlPattern.matcher(contentStr);

                while (urlMatcher.find()) {
                    final String url = urlMatcher.group();
                    if (!TextUtils.isEmpty(url)) {


                        sp.setSpan(new SpannableClickable() {
                            @Override
                            public void onClick(View widget) {
                                Context context = widget.getContext();
                                PostBean bean = new PostBean();
                                bean.setLinkUrl(url);
                                Intent intent = new Intent(context, WebPreviewActivity.class);
                                intent.putExtra("PostBean", bean);
                                context.startActivity(intent);

                               /*
                                    Uri uri = Uri.parse(url);
                               Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
                                context.startActivity(intent);*/
                            }
                            /**
                             *   点击url的区间有效
                             */
                        }, urlMatcher.start(), urlMatcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }

                //处理电话匹配
                Pattern phonePattern = Pattern.compile("[1][34578][0-9]{9}");
                Matcher phoneMatcher = phonePattern.matcher(contentStr);
                while (phoneMatcher.find()) {
                    final String phone = phoneMatcher.group();
                    if (!TextUtils.isEmpty(phone)) {
                        sp.setSpan(new SpannableClickable() {
                            @Override
                            public void onClick(View widget) {
                                Context context = widget.getContext();
                                //用intent启动拨打电话
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }
                            //   //点击电话的区间有效
                        }, phoneMatcher.start(), phoneMatcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            sp = new SpannableStringBuilder();
        }
        return sp;
    }
}

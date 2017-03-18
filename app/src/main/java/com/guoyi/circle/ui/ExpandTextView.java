package com.guoyi.circle.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.guoyi.circle.R;
import com.guoyi.circle.spannable.CircleMovementMethod;


/**
 * Created by yiwei on 16/7/10.
 */
public class ExpandTextView extends LinearLayout {
    public static final int DEFAULT_MAX_LINES = 3;
    private TextView contentText;
    private TextView textPlus;

    private int showLines;

    private ExpandStatusListener expandStatusListener;
    private boolean isExpand;

    public ExpandTextView(Context context) {
        super(context);
        initView();
    }

    public ExpandTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        initView();
    }

    public ExpandTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        initView();
    }

    private void initView() {
        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.layout_magic_text, this);
        contentText = (TextView) findViewById(R.id.contentText);
        if (showLines > 0) {
            contentText.setMaxLines(showLines);
        }

        textPlus = (TextView) findViewById(R.id.textPlus);
        textPlus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String textStr = textPlus.getText().toString().trim();
                if ("全文".equals(textStr)) {
                    contentText.setMaxLines(Integer.MAX_VALUE);
                    textPlus.setText("收起");
                    setExpand(true);
                } else {

                    contentText.setMaxLines(showLines);
                    textPlus.setText("全文");
                    setExpand(false);
                }
                //通知外部状态已变更
                if (expandStatusListener != null) {
                    expandStatusListener.statusChange(isExpand());
                }
            }
        });
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.ExpandTextView, 0, 0);
        try {
            showLines = typedArray.getInt(R.styleable.ExpandTextView_showLines, DEFAULT_MAX_LINES);
        } finally {
            typedArray.recycle();
        }
    }

    public void setText(final CharSequence content) {

        /**
         * 包含下面6个事件：

         interface  ViewTreeObserver.OnDrawListener
         挡在一个视图树绘制时，所要调用的回调函数的接口类（level 16)

         interface          ViewTreeObserver.OnGlobalFocusChangeListener
         当在一个视图树中的焦点状态发生改变时，所要调用的回调函数的接口类

         interface          ViewTreeObserver.OnGlobalLayoutListener
         当在一个视图树中全局布局发生改变或者视图树中的某个视图的可视状态发生改变时，所要调用的回调函数的接口类

         interface          ViewTreeObserver.OnPreDrawListener
         当一个视图树将要绘制时，所要调用的回调函数的接口类

         interface          ViewTreeObserver.OnScrollChangedListener
         当一个视图树中的一些组件发生滚动时，所要调用的回调函数的接口类

         interface          ViewTreeObserver.OnTouchModeChangeListener
         当一个视图树的触摸模式发生改变时，所要调用的回调函数的接口类


         */
        contentText.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {
                // 避免重复监听
                contentText.getViewTreeObserver().removeOnPreDrawListener(this);
                int linCount = contentText.getLineCount();
                if (linCount > showLines) {
                    if (isExpand) {
                        contentText.setMaxLines(Integer.MAX_VALUE);
                        textPlus.setText("收起");
                    } else {
                        contentText.setMaxLines(showLines);
                        textPlus.setText("全文");
                    }
                    textPlus.setVisibility(View.VISIBLE);
                } else {
                    textPlus.setVisibility(View.GONE);
                }
                return true;
            }


        });
        contentText.setText(content);
        /**
         * 为TextView设置链接：

         当文字中出现URL、E-mail、电话号码等的时候，我们为TextView设置链接。总结起来，一共有4种方法来为TextView实现链接。我们一一举例介绍；

         1. 在xml里添加android:autoLink属性。
         android:autoLink ：的可选值：none/web/email/phone/map/all，分别代表将当前文本设置为：
         普通文本/URL/email/电话号码/map/自动识别，文本显示为可点击的链接。其中：设置为all时，系统会自动根据你的文本格式识别文本类型，如：http为web，tel为电话等；当然，以上内容也可以在Java代码中完成，用法为tv.setAutoLinkMask(Linkify.ALL)。

         2. 将显示内容写到资源文件，一般为String.xml中，并且用<a>标签来声明链接，然后激活这个链接，激活链接需要在Java代码中使用setMovementMethod()方法设置TextView为可点击。

         3. 用Html类的fromHtml()方法格式化要放到TextView里的文字。然后激活这个链接，激活链接需要在Java代码中使用setMovementMethod()方法设置TextView为可点击。

         4. 用Spannable或实现它的类，如SpannableString。与其他方法不同的是，Spannable对象可以为个别字符设置链接（当然也可以为个别字符设置颜色、字体等，实现某些字符高亮显示的效果等）。这个方法同样需要在Java代码中使用setMovementMethod()方法设置TextView为可点击。
         */
        contentText.setMovementMethod(new CircleMovementMethod(getResources().getColor(R.color.name_selector_color)));
    }

    public void setExpand(boolean isExpand) {
        this.isExpand = isExpand;
    }

    public boolean isExpand() {
        return this.isExpand;
    }

    public void setExpandStatusListener(ExpandStatusListener listener) {
        this.expandStatusListener = listener;
    }

    public static interface ExpandStatusListener {

        void statusChange(boolean isExpand);
    }

}

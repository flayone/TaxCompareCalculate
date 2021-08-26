package com.flayone.taxcc.taxcomparecalculate.widget;

import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class ReadView extends AppCompatTextView {

//    constructor(context: Context) : this(context, null)
//    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
//    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
//        init(context)
//    }
//
    public ReadView(Context context) {
        this(context, null);

    }
    public ReadView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public ReadView(Context context, AttributeSet attrs,int defStyle) {
        super(context,attrs,defStyle);
    }

    public int reNum = 0;
    // 构造函数略...

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        resize();
    }

    /**
     * 去除当前页无法显示的字
     *
     * @return 去掉的字数
     */
    public int resize() {
        CharSequence oldContent = getText();
        reNum= getCharNum();
        CharSequence newContent = oldContent.subSequence(0, reNum);
        setText(newContent);
        return oldContent.length() - newContent.length();
    }

    /**
     * 获取当前页总字数
     */
    public int getCharNum() {
        return getLayout().getLineEnd(getLineNum());
    }

    /**
     * 获取当前页总行数
     */
    public int getLineNum() {
        Layout layout = getLayout();
        int topOfLastLine = getHeight() - getPaddingTop() - getPaddingBottom() - getLineHeight();
        return layout.getLineForVertical(topOfLastLine);
    }
}

package com.flayone.taxcc.taxcomparecalculate.widget

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.support.design.widget.TextInputEditText
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.flayone.taxcc.taxcomparecalculate.R
import com.flayone.taxcc.taxcomparecalculate.utils.LogUtil

class ClearInputEditText : TextInputEditText, TextWatcher, View.OnFocusChangeListener {
    private var clBg: Int = 0
    private var ta: TypedArray? = null
    /**
     * 删除按钮的引用
     */
    private var mClearDrawable: Drawable? = null
    /**
     * 控件是否有焦点
     */
    private var hasFocus: Boolean = false

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, android.R.attr.editTextStyle)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        if (isInEditMode) {
            return
        }
        try {
            ta = context.obtainStyledAttributes(attrs, R.styleable.CEditText)
            clBg = ta!!.getResourceId(R.styleable.CEditText_clear_background, R.mipmap.close)
            mClearDrawable = resources.getDrawable(clBg)
        } catch (e: Exception) {
            Log.e("ClearEditText_err", "获取资源失败")
        } finally {
            ta!!.recycle()
        }
        //使图标大小根据字体大小来适应
        mClearDrawable!!.setBounds(0, 0, textSize.toInt(), textSize.toInt())

        LogUtil.d("drawable ==="+mClearDrawable!!.intrinsicWidth+ mClearDrawable!!.intrinsicHeight+"textSize==="+textSize)
        //默认设置隐藏图标
        setClearIconVisible(false)
        //设置焦点改变的监听
        onFocusChangeListener = this
        //设置输入框里面内容发生改变的监听
        addTextChangedListener(this)
    }

    private fun setClearIconVisible(visible: Boolean) {
        val right: Drawable? = if (visible) mClearDrawable else null
        setCompoundDrawables(compoundDrawables[0], compoundDrawables[1], right, compoundDrawables[3])
    }


    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        this.hasFocus = hasFocus
        setClearIconVisible(text.isNotEmpty() && hasFocus)
    }

    /**
     * 当输入框里面内容发生变化的时候回调的方法
     */
    override fun onTextChanged(s: CharSequence, start: Int, count: Int,
                               after: Int) {
        if (hasFocus) {
            setClearIconVisible(text.isNotEmpty())
        }
    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    /**
     * 因为我们不能直接给EditText设置点击事件，所以我们用记住我们按下的位置来模拟点击事件
     * 当我们按下的位置 在  EditText的宽度 - 图标到控件右边的间距 - 图标的宽度  和
     * EditText的宽度 - 图标到控件右边的间距之间我们就算点击了图标，竖直方向就没有考虑
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            if (compoundDrawables[2] != null) {
                val touchable = event.x > width - totalPaddingRight && event.x < width - paddingRight
                if (touchable) {
                    this.setText("")
                }
            }
        }
        return super.onTouchEvent(event)
    }

}
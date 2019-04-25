package com.flayone.taxcc.taxcomparecalculate.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator

/**
 * Created by liyayu on 2018/12/24.
 * 监听触摸事件，实现拖拽效果
 */
open class OnDragListener(private val isAutoPullToBorder: Boolean, private val mListener: OnDraggableClickListener? = null) : View.OnTouchListener {

    private var mScreenWidth: Int = 0
    private var mScreenHeight: Int = 0//屏幕宽高
    private var mOriginalX: Float = 0.toFloat()
    private var mOriginalY: Float = 0.toFloat()//手指按下时的初始位置
    private var mDistanceX: Float = 0.toFloat()
    private var mDistanceY: Float = 0.toFloat()//记录手指与view的左上角的距离
    var left: Int = 0
    var top: Int = 0
    var right: Int = 0
    var bottom: Int = 0
    private val sensitiveMove = 10f //移动敏感度设置

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        mScreenWidth = getScreenW()
        mScreenHeight = getScreenHWithOutBar()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mOriginalX = event.rawX
                mOriginalY = event.rawY
                mDistanceX = event.rawX - v.left
                mDistanceY = event.rawY - v.top
            }
            MotionEvent.ACTION_MOVE -> {
                left = (event.rawX - mDistanceX).toInt()
                top = (event.rawY - mDistanceY).toInt()
                right = left + v.width
                bottom = top + v.height
                if (left < 0) {
                    left = 0
                    right = left + v.width
                }
                if (top < 0) {
                    top = 0
                    bottom = top + v.height
                }
                if (right > mScreenWidth) {
                    right = mScreenWidth
                    left = right - v.width
                }
                if (bottom > mScreenHeight) {
                    bottom = mScreenHeight
                    top = bottom - v.height
                }
                v.layout(left, top, right, bottom)
            }
            MotionEvent.ACTION_UP -> {
                //在拖动过按钮后，如果其他view刷新导致重绘，会让按钮重回原点，所以需要更改布局参数
                val lp = v.layoutParams as ViewGroup.MarginLayoutParams
                //如果移动距离过小，则判定为点击
                if (Math.abs(event.rawX - mOriginalX) < dp2px(sensitiveMove) && Math.abs(event.rawY - mOriginalY) < dp2px(sensitiveMove)) {
                    mListener?.onClick(v)
                }else {
                    startAutoPull(v, lp)
                }
                //消除警告
                v.performClick()
            }
        }
        return true

    }

    /**
     * 开启自动拖拽
     *
     * @param v  拉动控件
     * @param lp 控件布局参数
     */
    private fun startAutoPull(v: View, lp: ViewGroup.MarginLayoutParams) {
        if (!isAutoPullToBorder) {
            v.layout(left, top, right, bottom)
            lp.setMargins(left, top, 0, 0)
            v.layoutParams = lp
            mListener?.onDragged(v, left, top)
            return
        }
        //当用户拖拽完后，让控件根据远近距离回到最近的边缘
        var end = 0f
        if (left + v.width / 2 >= mScreenWidth / 2) {
            end = (mScreenWidth - v.width).toFloat()
        }
        val animator = ValueAnimator.ofFloat(left.toFloat(), end)
        animator.interpolator = DecelerateInterpolator()
        animator.addUpdateListener { animation ->
            val leftMargin = (animation.animatedValue as Float).toInt()
            v.layout(leftMargin, top, right, bottom)
            lp.setMargins(leftMargin, top, 0, 0)
            v.layoutParams = lp
        }
        val finalEnd = end
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                mListener?.onDragged(v, finalEnd.toInt(), top)
            }
        })
        animator.duration = 400
        animator.start()
    }

}
package com.example.sakura.stickerdemo

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.support.annotation.Px
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.LinearLayout

class PageControlView : View {
    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initPaint()
    }

    private var mPaint = Paint()

    private fun initPaint() {
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.FILL
    }

    private var mCurrentPosition = 0
    private var mCount = 3
    @Px
    private var mDotRadius = 6
    @Px
    private var mDotInterval = 24

    fun setCurrentPosition(currentPosition: Int) {
        // 判断是否合法
        if (currentPosition < 0 || currentPosition >= mCount) {
            throw RuntimeException("current position is illegal")
        }
        // 移动到该位置
        mCurrentPosition = currentPosition
        invalidate()
    }

    fun setCount(count: Int) {
        // 判断是否合法
        if (count <= 0) {
            throw RuntimeException("Count is illegal")
        }
        // 改变当前长度，如果相等则不改变
        mCount = count
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.translate(measuredWidth / 2f, measuredHeight / 2f)

        // 总长度
        val mLength = mDotRadius.shl(1) * mCount + (mCount - 1) * mDotInterval
        for (drawPos in 0 until mCount) {
            if (mCurrentPosition == drawPos) {
                mPaint.color = Color.WHITE
                canvas.drawCircle((-mLength / 2 + mDotRadius + (mDotInterval + mDotRadius * 2) * drawPos).toFloat(),
                        0f, mDotRadius.toFloat(), mPaint)
            } else {
                mPaint.color = Color.parseColor("#AAAAAA")
                canvas.drawCircle((-mLength / 2 + mDotRadius + (mDotInterval + mDotRadius * 2) * drawPos).toFloat(),
                        0f, mDotRadius.toFloat(), mPaint)
            }
        }
    }
}
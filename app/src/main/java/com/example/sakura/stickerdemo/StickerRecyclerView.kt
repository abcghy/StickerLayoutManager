package com.example.sakura.stickerdemo

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.animation.Interpolator

class StickerRecyclerView: RecyclerView {
    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {

    }

    private var _interpolator: Interpolator? = null

    private val interpolator: Interpolator
        get() {
            if (_interpolator == null) {
                _interpolator = Interpolator {
                    var result = it * 2
                    if (result >= 1) {
                        result = 1f
                    }
                    return@Interpolator result
                }
            }
            return _interpolator ?: throw AssertionError("Set to null by another thread")
        }

    override fun smoothScrollBy(dx: Int, dy: Int) {
        super.smoothScrollBy(dx, dy, interpolator)
    }

}
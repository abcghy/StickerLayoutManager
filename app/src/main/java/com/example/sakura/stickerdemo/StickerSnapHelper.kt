package com.example.sakura.stickerdemo

import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View

class StickerSnapHelper : LinearSnapHelper {

    val emojiColumns: Int
    val emojiRows: Int
    val emojiCount: Int
    val columns: Int
    val rows: Int

    val emojiPages: Int

    constructor(emojiColumns: Int, emojiRows: Int, emojiCount: Int, columns: Int, rows: Int) : super() {
        this.emojiColumns = emojiColumns
        this.emojiRows = emojiRows
        this.emojiCount = emojiCount
        this.columns = columns
        this.rows = rows

        emojiPages = emojiCount / (emojiColumns * emojiRows) + 1
    }

    var currentPage = 0
    var mStickerPageChangeListener: StickerPageChangeListener? = null

    override fun findTargetSnapPosition(layoutManager: RecyclerView.LayoutManager?, velocityX: Int, velocityY: Int): Int {
        var pos = super.findTargetSnapPosition(layoutManager, velocityX, velocityY)
        pos = getPos(pos)
        currentPage = getCurrentPage(pos)
        mStickerPageChangeListener?.onStickerPageChangeListener(currentPage)
        return pos
    }

    override fun findSnapView(layoutManager: RecyclerView.LayoutManager): View? {
        val view = super.findSnapView(layoutManager) ?: return null
        var pos = layoutManager.getPosition(view)
        pos = getPos(pos)
        currentPage = getCurrentPage(pos)
        mStickerPageChangeListener?.onStickerPageChangeListener(currentPage)
        return layoutManager.getChildAt(pos)
    }

    private fun getPos(pos: Int): Int {
        return if (pos < emojiCount) {
            // emoji
            pos / (emojiColumns * emojiRows) * (emojiColumns * emojiRows) + (emojiColumns / 2)
        } else {
            pos / (columns * rows) * (columns * rows) + (columns / 2)
        }
    }

    private fun getCurrentPage(pos: Int) : Int {
        return if (pos < emojiCount) {
            // emoji
            pos / (emojiColumns * emojiRows)
        } else {
            // sticker
            (pos - emojiCount) / (columns * rows) + emojiPages
        }
    }

}
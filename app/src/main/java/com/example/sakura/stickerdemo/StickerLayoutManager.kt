package com.example.sakura.stickerdemo

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.ViewGroup

class StickerLayoutManager : RecyclerView.LayoutManager {
    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private var emojiColumns: Int
    private var emojiRows: Int
    private var emojiCount: Int // this can't be change, cause emoji is system provided, not user provided
    private var columns: Int
    private var rows: Int

    private var pages = 0

    constructor(emojiColumns: Int, emojiRows: Int, stickEmojiCount: Int, columns: Int, rows: Int) : super() {
        // 表情可能有不同的宽高
        this.emojiColumns = emojiColumns
        this.emojiRows = emojiRows
        this.emojiCount = stickEmojiCount
        this.columns = columns
        this.rows = rows
    }

    override fun canScrollHorizontally(): Boolean {
        return true
    }

    private var mTotalDistance = 0
    private var mMaxDistance = 0

    // 向左滑 dx > 0, 向右滑 dx < 0
    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        var dx = dx
        if (mTotalDistance + dx < 0) {
            dx = -mTotalDistance
        }
        if (mTotalDistance + dx > mMaxDistance) {
            dx = mMaxDistance - mTotalDistance
        }
        mTotalDistance += dx
        offsetChildrenHorizontal(-dx)
        return dx
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
//        super.onLayoutChildren(recycler, state)
        detachAndScrapAttachedViews(recycler)
        val allCount = state.itemCount.apply {
            if (this == 0) {
                return
            }
        }

        // <editor-fold desc="找到 emoji view 的长宽，因为 sticker 是第一个，所以直接取第一个">
        val emojiView = recycler.getViewForPosition(0)
        measureChildWithMargins(emojiView, 0, 0)
        val emojiWidth = emojiView.measuredWidth
        val emojiHeight = emojiView.measuredHeight
        // </editor-fold>

        // <editor-fold desc="确定 emoji 页数">
        val emojiPages = emojiCount / (emojiRows * emojiColumns) + 1
        // </editor-fold>

        // <editor-fold desc="找到 sticker 的长宽">
        val stickerView = recycler.getViewForPosition(emojiCount)
        measureChildWithMargins(stickerView, 0, 0)
        val stickerWidth = stickerView.measuredWidth
        val stickerHeight = stickerView.measuredHeight
        // </editor-fold>

        // <editor-fold desc="确定 sticker 页数">
        val stickerPages = (state.itemCount - emojiCount) / (rows * columns)
        // </editor-fold>

        // <editor-fold desc="总页数"
        pages = emojiPages + stickerPages
        // </editor-fold>

        // <editor-fold desc="获取 RecyclerView 的长宽"
        val recyclerViewWidth = width
        val recyclerViewHeight = height
        // </editor-fold>

        // <editor-fold desc="能滑动的最大距离">
        mMaxDistance = (pages - 1) * recyclerViewWidth
        // </editor-fold>

        // <editor-fold desc="emoji 排版"
        for (pos in 0 until emojiCount) {
            // 找到对应的点
            val currPage = pos / (emojiRows * emojiColumns)
            val column = pos % emojiColumns
            val row = pos % (emojiRows * emojiColumns) / emojiColumns

            val verticalEmptySpace = recyclerViewHeight - emojiHeight * emojiRows
            val singleVerticalEmptySpace = verticalEmptySpace / (emojiRows + 1)
            val currTop = singleVerticalEmptySpace * (row + 1) + emojiHeight * row

            val allSpace = recyclerViewWidth - emojiColumns * emojiWidth
            val singleSpace = allSpace / (emojiColumns + 1)
            val left = singleSpace * (column + 1) + emojiWidth * column + recyclerViewWidth * currPage

            val posLeft = left
            val posTop = currTop

            val indexView = recycler.getViewForPosition(pos)
            addView(indexView)
            measureChildWithMargins(indexView, 0, 0)
            layoutDecoratedWithMargins(indexView, posLeft, posTop, posLeft + emojiWidth, posTop + emojiHeight)
        }
        // </editor-fold>

        // <editor-fold desc="sticker 排版"
        val emojiPagesCount = emojiPages * emojiColumns * emojiRows
        for (pos in emojiPagesCount until allCount) {
            // 找到对应的点
            val currPage = (pos - emojiPagesCount) / (rows * columns) + emojiPages //
            val column = (pos - emojiPagesCount) % columns
            val row = (pos - emojiPagesCount) % (rows * columns) / columns

            val verticalEmptySpace = recyclerViewHeight - stickerHeight * rows
            val singleVerticalEmptySpace = verticalEmptySpace / (rows + 1)
            val currTop = singleVerticalEmptySpace * (row + 1) + stickerHeight * row

            val allSpace = recyclerViewWidth - columns * stickerWidth
            val singleSpace = allSpace / (columns + 1)
            val left = singleSpace * (column + 1) + stickerWidth * column + recyclerViewWidth * currPage

            val posLeft = left
            val posTop = currTop

            val indexView = recycler.getViewForPosition(pos)
            addView(indexView)
            measureChildWithMargins(indexView, 0, 0)
            layoutDecoratedWithMargins(indexView, posLeft, posTop, posLeft + stickerWidth, posTop + stickerHeight)
        }
        // </editor-fold>
    }

    fun scrollToPage(page: Int) {
        val diff = mTotalDistance - width * page
        mTotalDistance = width * page
        offsetChildrenHorizontal(diff)
    }
}
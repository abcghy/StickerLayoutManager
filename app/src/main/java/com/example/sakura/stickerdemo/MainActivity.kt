package com.example.sakura.stickerdemo

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.PagerSnapHelper
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.markzhai.recyclerview.MultiTypeAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.tab_sticker.view.*

class MainActivity : AppCompatActivity() {

    private var titles: Array<String> = arrayOf("emoji", "招呼", "再见", "开心", "难过", "生气", "卖萌", "害羞", "操蛋", "傻逼", "日狗", "册那")
    private var sizes: IntArray = intArrayOf(100, 25, 26, 27, 30, 35, 38, 89, 58, 22, 12, 4)

    private var mAdapter: StickerAdapter? = null
    private var mLayoutManager: RecyclerView.LayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        vp_category.adapter = CategoryAdapter(supportFragmentManager)

        stl_category.setCustomTabView { container, position, adapter ->
            if (position == 0) {
                LayoutInflater.from(container.context).inflate(R.layout.tab_sticker_emoji, container, false)
            } else {
                LayoutInflater.from(container.context).inflate(R.layout.tab_sticker, container, false).apply {
                    tv_tab_sticker.text = titles[position]
                }
            }
        }
        stl_category.setViewPager(vp_category)

        recycler_view.setHasFixedSize(true)
        mAdapter = StickerAdapter(this)
        recycler_view.adapter = mAdapter
        recycler_view.addItemDecoration(GridPaddingItemDecoration(2, 18, 0, GridLayoutManager.HORIZONTAL))

//        mLayoutManager = GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false)
//        mLayoutManager = GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false)
        mLayoutManager = StickerLayoutManager(5, 2)
        recycler_view.layoutManager = mLayoutManager

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recycler_view)

        loadData()
    }

    private fun loadData() {
        val allCount = 34
//                sizes.reduce { a, b ->
//            a + b
//        }
        val stickerList: List<String> = List(allCount) { "$it" }
        mAdapter?.set(stickerList, StickerAdapter.STICKER)
    }

    inner class CategoryAdapter : FragmentStatePagerAdapter {

        private var titles: Array<String> = arrayOf("emoji", "招呼", "再见", "开心", "难过", "生气", "卖萌", "害羞", "操蛋", "傻逼", "日狗", "册那")

        override fun getItem(p0: Int): Fragment? {
            return StickerCategoryFragment.newInstance(p0)
        }

        override fun getCount(): Int = titles.size

        override fun getPageTitle(position: Int): CharSequence? = titles[position]

        constructor(fm: FragmentManager?) : super(fm)
    }
}

class StickerAdapter : MultiTypeAdapter {

    companion object {
        val EMOJI = 0
        val STICKER = 1
    }

    constructor(context: Context?) : super(context) {
        addViewTypeToLayoutMap(EMOJI, R.layout.item_emoji)
        addViewTypeToLayoutMap(STICKER, R.layout.item_sticker)
    }
}


class StickerCategoryFragment : Fragment() {

    companion object {
        fun newInstance(position: Int): StickerCategoryFragment {
            return StickerCategoryFragment().apply {
                arguments = Bundle().apply {
                    putInt("position", position)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

}

class StickerLayoutManager: RecyclerView.LayoutManager {
    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private var columns: Int
    private var rows: Int

    private var pages = 2

    constructor(columns: Int, rows: Int) : super() {
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
        Log.d("test", "dx $dx")
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

        // <editor-fold desc="通过第一个 view 获得长和宽">
        // 假设所有的长宽都一样
        val firstView = recycler.getViewForPosition(0)
        measureChildWithMargins(firstView, 0, 0)
        val viewWidth = firstView.measuredWidth
        val viewHeight = firstView.measuredHeight
        // </editor-fold>

        // <editor-fold desc="获取 RecyclerView 的长宽"
        val recyclerViewWidth = width
        val recyclerViewHeight = height
        // </editor-fold>

        pages = allCount / (rows * columns) + 1
        mMaxDistance = (pages - 1) * recyclerViewWidth

        for (pos in 0 until allCount) {
            // 找到对应的点
            val currPage = pos / (rows * columns)
            val column = pos % columns
            val row = pos % (rows * columns) / columns

            val verticalEmptySpace = recyclerViewHeight - viewHeight * rows
            val singleVerticalEmptySpace = verticalEmptySpace / (rows + 1)
            val currTop = singleVerticalEmptySpace * (row + 1) + viewHeight * row

            val allSpace = recyclerViewWidth - columns * viewWidth
            val singleSpace = allSpace / (columns + 1)
            val left = singleSpace * (column + 1) + viewWidth * column + recyclerViewWidth * currPage

            val posLeft = left
            val posTop = currTop

            val indexView = recycler.getViewForPosition(pos)
            addView(indexView)
            measureChildWithMargins(indexView, 0, 0)
            layoutDecoratedWithMargins(indexView, posLeft, posTop, posLeft + viewWidth, posTop + viewHeight)
        }
    }
}
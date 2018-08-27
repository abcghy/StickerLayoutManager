package com.example.sakura.stickerdemo

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.widget.*
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
    private var listList: MutableList<List<String>> = ArrayList()
    private lateinit var pages: IntArray

    private var mAdapter: StickerAdapter? = null
    private var mLayoutManager: RecyclerView.LayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        vp_category.adapter = CategoryAdapter(supportFragmentManager)

        stl_category.setCustomTabView { container, position, _ ->
            if (position == 0) {
                LayoutInflater.from(container.context).inflate(R.layout.tab_sticker_emoji, container, false)
            } else {
                LayoutInflater.from(container.context).inflate(R.layout.tab_sticker, container, false).apply {
                    tv_tab_sticker.text = titles[position]
                }
            }
        }
        stl_category.setViewPager(vp_category)

        stl_category.setOnTabClickListener {
            var page = 0
            if (it > 0) {
                page += pages.asList().subList(0, it).reduce { a, b ->
                    a + b
                }
            }

            (mLayoutManager as StickerLayoutManager).scrollToPage(page)
            pcv.setCount(getCurrentCountBy(page))
            pcv.setCurrentPosition(getCurrentPosBy(page))
        }

        recycler_view.setHasFixedSize(true)
        mAdapter = StickerAdapter(this)
        recycler_view.adapter = mAdapter
        recycler_view.addItemDecoration(GridPaddingItemDecoration(2, 18, 0, GridLayoutManager.HORIZONTAL))

        mLayoutManager = StickerLayoutManager(5, 2)
        recycler_view.layoutManager = mLayoutManager

//        val snapHelper = LinearSnapHelper()
        val snapHelper = StickerSnapHelper()
        snapHelper.attachToRecyclerView(recycler_view)

        pages = IntArray(sizes.size) {
            val i = sizes[it]
            if (i % 10 != 0) {
                i / 10 + 1
            } else {
                i / 10
            }
        }

        loadData()

        snapHelper.mStickerPageChangeListener = object : StickerPageChangeListener {
            override fun onStickerPageChangeListener(page: Int) {
                Log.d("test", "onStickerPageChangeListener: $page")

                pcv.setCount(getCurrentCountBy(page))
                pcv.setCurrentPosition(getCurrentPosBy(page))

                // 改变 tab
                vp_category.currentItem = getTabBy(page)
            }
        }

        pcv.setCount(getCurrentCountBy(0))
        pcv.setCurrentPosition(getCurrentPosBy(0))
    }

    private fun getCurrentCountBy(page: Int): Int {
        var leftPage = page
        for (i in 0 until pages.size) {
            if (leftPage - pages[i] < 0) {
                return pages[i]
            }
            leftPage -= pages[i]
        }
        return pages[0]
    }

    private fun getCurrentPosBy(page: Int): Int {
        var leftPage = page
        for (i in 0 until pages.size) {
            if (leftPage - pages[i] < 0) {
                break
            }
            leftPage -= pages[i]
        }
        return leftPage
    }

    private fun getTabBy(page: Int): Int {
        var leftPage = page
        for (i in 0 until pages.size) {
            if (leftPage - pages[i] < 0) {
                return i
            }
            leftPage -= pages[i]
        }
        return 0
    }

    private fun loadData() {
        sizes.forEach {
            val list = ArrayList<String>()
            for (i in 0 until it) {
                list.add("")
            }
            listList.add(list)
        }
        mAdapter?.addAllSticker(listList)
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

    private var listList: MutableList<List<String>>? = null

    companion object {
        val EMOJI = 0
        val STICKER = 1
        val PLACE_HOLDER = 2
    }

    constructor(context: Context?) : super(context) {
        addViewTypeToLayoutMap(EMOJI, R.layout.item_emoji)
        addViewTypeToLayoutMap(STICKER, R.layout.item_sticker)
        addViewTypeToLayoutMap(PLACE_HOLDER, R.layout.item_placeholder)
    }

    fun addAllSticker(theListList: MutableList<List<String>>) {
        listList = theListList

        listList?.forEach {
            val stickerSize = it.size
            addAll(it, STICKER)
            val stickerPlaceholderSize = if (stickerSize % 10 == 0) {
                0
            } else {
                10 - stickerSize % 10
            }
            val stickerPlaceHolderList = List(stickerPlaceholderSize) {}
            addAll(stickerPlaceHolderList, PLACE_HOLDER)
        }
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

class StickerLayoutManager : RecyclerView.LayoutManager {
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

        pages = allCount / (rows * columns)
        mMaxDistance = (pages - 1) * recyclerViewWidth

        // 扩充当前数量到倍数
//        val multiCount = pages * columns * rows
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

    fun scrollToPage(page: Int) {
        val diff = mTotalDistance - width * page
        Log.d("test", "diff: $diff, mTotalDistance: $mTotalDistance, maxDistance: $width, page: $page")
        mTotalDistance = width * page
        offsetChildrenHorizontal(diff)
    }

//    override fun scrollToPosition(position: Int) {
////        super.scrollToPosition(position)
//        // 计算 mTotalDistance，然后调用 offsetChildrenHorizontal
//        mTotalDistance =
//    }

//    override fun smoothScrollToPosition(recyclerView: RecyclerView?, state: RecyclerView.State?,
//                                        position: Int) {
//        val linearSmoothScroller = LinearSmoothScroller(recyclerView!!.context)
//        linearSmoothScroller.targetPosition = position
//        startSmoothScroll(linearSmoothScroller)
//    }
}

class StickerSnapHelper : LinearSnapHelper {
    constructor() : super()

    var currentPage = 0
    var mStickerPageChangeListener: StickerPageChangeListener? = null

    override fun findTargetSnapPosition(layoutManager: RecyclerView.LayoutManager?, velocityX: Int, velocityY: Int): Int {
        var pos = super.findTargetSnapPosition(layoutManager, velocityX, velocityY)
        pos = pos / 10 * 10 + 2
        currentPage = pos / 10
        mStickerPageChangeListener?.onStickerPageChangeListener(currentPage)
        return pos
    }

    override fun findSnapView(layoutManager: RecyclerView.LayoutManager): View? {
        val view = super.findSnapView(layoutManager) ?: return null
        var pos = layoutManager.getPosition(view)
        pos = pos / 10 * 10 + 2
        currentPage = pos / 10
        mStickerPageChangeListener?.onStickerPageChangeListener(currentPage)
        return layoutManager.getChildAt(pos)
    }

}

interface StickerPageChangeListener {
    fun onStickerPageChangeListener(page: Int)
}
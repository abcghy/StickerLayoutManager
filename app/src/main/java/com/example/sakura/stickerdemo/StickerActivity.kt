package com.example.sakura.stickerdemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.github.markzhai.recyclerview.BaseViewAdapter
import kotlinx.android.synthetic.main.activity_new.*
import kotlinx.android.synthetic.main.tab_sticker.view.*
import kotlin.collections.ArrayList

class StickerActivity : AppCompatActivity() {

    private var titles: Array<String> = arrayOf("emoji", "你好呀", "再见咯", "卖个萌", "吐个槽", "不开心", "尴尬了")
    private var dirNames: Array<String> = arrayOf("emoji", "hello", "bye", "cute", "complaints", "not_happy", "awkward")

    private var mAdapter: StickerAdapter? = null
    private var mLayoutManager: RecyclerView.LayoutManager? = null

    private lateinit var pages: IntArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new)

        // 设置 vp 的 adapter
        vp_category.adapter = CategoryAdapter(supportFragmentManager, titles.size)

        // 自定义 STL
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

        // 设置 STL 点击事件
        stl_category.setOnTabClickListener {
            // 找到需要跳转的页面
            var page = 0
            if (it > 0) {
                page += pages.asList().subList(0, it).reduce { a, b ->
                    a + b
                }
            }

            // 跳转
            (mLayoutManager as StickerLayoutManager).scrollToPage(page)
            // 设置 pcv
            pcv.setCount(getCurrentCountBy(page))
            pcv.setCurrentPosition(getCurrentPosBy(page))
        }

        // <editor-fold desc="设置 Sticker RecyclerView">
        recycler_view.setHasFixedSize(true)
        mAdapter = StickerAdapter(this)
        mAdapter?.setPresenter(Presenter())
        recycler_view.adapter = mAdapter

        mLayoutManager = StickerLayoutManager(7, 3, 80, 5, 2)
        recycler_view.layoutManager = mLayoutManager

        val snapHelper = StickerSnapHelper(7, 3, 80, 5, 2)
        snapHelper.attachToRecyclerView(recycler_view)
        // </editor-fold>

        loadData()

        // 滑动后，更新 pcv，更新 tab 选中
        snapHelper.mStickerPageChangeListener = object : StickerPageChangeListener {
            override fun onStickerPageChangeListener(page: Int) {
                pcv.setCount(getCurrentCountBy(page))
                pcv.setCurrentPosition(getCurrentPosBy(page))

                // 改变 tab
                vp_category.currentItem = getTabBy(page)
            }
        }

        // 初始化 pcv
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

    private val stickerRootDirName = "sticker"

    var stickerListList = ArrayList<ArrayList<String>>()

    lateinit var sizes: IntArray

    /**
     * 1. get all the sticker file names. (be an adapter, may be they will change method to get file)
     * 2. analyse every file into different list.
     * 3. load every data into list.
     */
    private fun loadData() {
        // 1.
        // emoji file
        var emojiArray = resources.getStringArray(R.array.emoji_array)
        val emojiList = ArrayList<String>()
        emojiArray.forEach {
            emojiList.add(it)
        }
        stickerListList.add(emojiList)

        dirNames.forEachIndexed { index, dirName ->
            if (index != 0) {
                val stickerList = ArrayList<String>()
                for (stickerIndex in 0 until assets.list("$stickerRootDirName/$dirName").size) {
                    stickerList.add("file:///android_asset/$stickerRootDirName/$dirName/${assets.list("$stickerRootDirName/$dirName")[stickerIndex]}")
                }
                stickerListList.add(stickerList)
            }
        }

        // 2.
        // 设置每个 sticker 的 page 是多少, 目前 5*2=10 个一页，不足一页算一页, 0 页目前算一页
        sizes = IntArray(stickerListList.size) {
            return@IntArray stickerListList[it].size
        }

        pages = IntArray(sizes.size) {
            if (it == 0) {
                val i = sizes[it]
                if (i % 21 != 0) {
                    i / 21 + 1
                } else {
                    i / 21
                }
            } else {
                val i = sizes[it]
                if (i % 10 != 0) {
                    i / 10 + 1
                } else {
                    i / 10
                }
            }
        }

        // 3.
        mAdapter?.addAllSticker(stickerListList)
    }

    inner class Presenter : BaseViewAdapter.Presenter {
        fun clickEmoji(emoji: String) {
            Log.d("test", "emoji: $emoji")
        }

        fun clickSticker(stickerLocalPath: String) {
            Log.d("test", "stickerLocalPath: $stickerLocalPath")
        }
    }
}

class StickerScrollListener : RecyclerView.OnScrollListener {
    constructor() : super()

    private var isScroll = false
//    private var recyclerView: RecyclerView? = null

    private var offsetX: Int = 0

    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
//        super.onScrolled(recyclerView, dx, dy)
        offsetX += dx
        if (dx != 0 || dy != 0) {
            isScroll = true
        }
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//        super.onScrollStateChanged(recyclerView, newState)
        if (newState == RecyclerView.SCROLL_STATE_IDLE && isScroll) {
            isScroll = !isScroll
            scrollToPosition(recyclerView)
        }
    }

    private var recyclerViewWidth = 1080

    private fun scrollToPosition(recyclerView: RecyclerView) {
//        Log.d("test", "recyclerView?.left: $offsetX")
//        recyclerView?.left
        var currentPos = offsetX
        recyclerViewWidth = recyclerView.measuredWidth
        val currPage = currentPos / recyclerViewWidth
        var scrollPage = if (currentPos % recyclerViewWidth <= (recyclerViewWidth / 2)) {
            // scroll to last page
            currPage
        } else {
            // scroll to next page
            currPage + 1
        }
        recyclerView.layoutManager?.offsetChildrenHorizontal(currentPos - scrollPage * recyclerViewWidth)
    }

//    private fun scrollToPage(page: Int) {
//        recyclerView?.layoutManager?.scrollHorizontallyBy()
//    }
}
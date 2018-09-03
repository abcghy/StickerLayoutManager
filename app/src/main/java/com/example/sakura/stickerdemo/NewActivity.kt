package com.example.sakura.stickerdemo

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_new.*
import kotlinx.android.synthetic.main.tab_sticker.view.*

class NewActivity: AppCompatActivity() {

    private var titles: Array<String> = arrayOf("你好呀", "再见咯", "卖个萌", "吐个槽", "不开心", "尴尬了")
    private var dirNames : Array<String> = arrayOf("hello", "bye", "cute", "complaints", "unhappy", "awkward")

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
        recycler_view.adapter = mAdapter
        recycler_view.addItemDecoration(GridPaddingItemDecoration(2, 18, 0, GridLayoutManager.HORIZONTAL))

        mLayoutManager = StickerLayoutManager(5, 2)
        recycler_view.layoutManager = mLayoutManager

        val snapHelper = StickerSnapHelper()
        snapHelper.attachToRecyclerView(recycler_view)
        // </editor-fold>

        loadData()

        // 滑动后，更新 pcv，更新 tab 选中
        snapHelper.mStickerPageChangeListener = object : StickerPageChangeListener {
            override fun onStickerPageChangeListener(page: Int) {
                Log.d("test", "onStickerPageChangeListener: $page")

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

    val stickerRootDirName = "sticker"

    var stickerListList = ArrayList<ArrayList<String>>()

    lateinit var sizes: IntArray

    /**
     * 1. get all the sticker file names. (be an adapter, may be they will change method to get file)
     * 2. analyse every file into different list.
     * 3. load every data into list.
     */
    private fun loadData() {
        // 1.
        for (dirName in dirNames) {
            val stickerList = ArrayList<String>()
            for (stickerIndex in 0 until assets.list("sticker/$dirName").size) {
//                Log.d("test", "dirName: $dirName, stickerName: ${assets.list("sticker/$dirName")[stickerIndex]}")
                stickerList.add("file:///android_asset/sticker/$dirName/${assets.list("sticker/$dirName")[stickerIndex]}")
            }
            stickerListList.add(stickerList)
        }

        // 2.
        // 设置每个 sticker 的 page 是多少, 目前 5*2=10 个一页，不足一页算一页, 0 页目前算一页
        sizes = IntArray(stickerListList.size) {
            return@IntArray stickerListList[it].size
        }

        pages = IntArray(sizes.size) {
            val i = sizes[it]
            if (i % 10 != 0) {
                i / 10 + 1
            } else {
                i / 10
            }
        }

//        sizes.forEach {
//            val list = ArrayList<String>()
//            for (i in 0 until it) {
//                list.add("")
//            }
//            listList.add(list)
//        }
        // 3.
        mAdapter?.addAllSticker(stickerListList)
    }
}
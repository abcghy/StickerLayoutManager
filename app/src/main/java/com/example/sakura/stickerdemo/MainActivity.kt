package com.example.sakura.stickerdemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
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

        vp_category.adapter = CategoryAdapter(supportFragmentManager, titles.size)

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

        mLayoutManager = StickerLayoutManager(7, 3, 80,5, 2)
        recycler_view.layoutManager = mLayoutManager

//        val snapHelper = LinearSnapHelper()
        val snapHelper = StickerSnapHelper(7, 3, 80,5, 2)
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
}
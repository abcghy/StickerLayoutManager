package com.example.sakura.stickerdemo

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

class CategoryAdapter : FragmentStatePagerAdapter {

    var mCount: Int

    override fun getItem(p0: Int): Fragment? {
        return StickerCategoryFragment.newInstance(p0)
    }

    override fun getCount(): Int = mCount

    constructor(fm: FragmentManager?, count: Int) : super(fm) {
        mCount = count
    }
}
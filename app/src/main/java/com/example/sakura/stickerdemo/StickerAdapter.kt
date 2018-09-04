package com.example.sakura.stickerdemo

import android.content.Context
import android.util.Log
import com.github.markzhai.recyclerview.MultiTypeAdapter

class StickerAdapter : MultiTypeAdapter {

    private var listList: List<List<String>>? = null

    companion object {
        val EMOJI = 0
        val EMOJI_PLACE_HOLDER = 3
        val STICKER = 1
        val PLACE_HOLDER = 2
    }

    constructor(context: Context?) : super(context) {
        addViewTypeToLayoutMap(EMOJI, R.layout.item_emoji)
        addViewTypeToLayoutMap(EMOJI_PLACE_HOLDER, R.layout.item_emoji_placeholder)
        addViewTypeToLayoutMap(STICKER, R.layout.item_sticker)
        addViewTypeToLayoutMap(PLACE_HOLDER, R.layout.item_placeholder)
    }

    fun addAllSticker(theListList: List<List<String>>) {
        listList = theListList

        listList?.forEachIndexed { index, list ->
            if (index == 0) {
                // emoji
                val emojiSize = list.size
                addAll(list, EMOJI)
                val stickerPlaceholderSize = if (emojiSize % 21 == 0) {
                    0
                } else {
                    21 - emojiSize % 21
                }
                val stickerPlaceHolderList = List(stickerPlaceholderSize){}
                addAll(stickerPlaceHolderList, EMOJI_PLACE_HOLDER)
            } else {
                // sticker
                val stickerSize = list.size
                addAll(list, STICKER)
                val stickerPlaceholderSize = if (stickerSize % 10 == 0) {
                    0
                } else {
                    10 - stickerSize % 10
                }
                val stickerPlaceHolderList = List(stickerPlaceholderSize){}
                addAll(stickerPlaceHolderList, PLACE_HOLDER)
            }
        }
    }

}
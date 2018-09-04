package com.example.sakura.stickerdemo

import android.app.Application
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.ios.IosEmojiProvider

class App : Application {
    constructor() : super()

    override fun onCreate() {
        super.onCreate()

        EmojiManager.install(IosEmojiProvider())
    }
}
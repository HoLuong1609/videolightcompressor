package com.example.videolightcompressor.ui

import android.app.Application
import com.example.videolightcompressor.extensions.AppSettings

class VideoCompressorApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        AppSettings.init(this)
        HOLDER.INSTANCE = this
    }

    private object HOLDER {
        lateinit var INSTANCE: VideoCompressorApplication
    }

    companion object {
        val instance: VideoCompressorApplication by lazy { HOLDER.INSTANCE }
    }
}
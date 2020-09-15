package com.example.videolightcompressor.ui

import android.app.Application

class VideoCompressorApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        HOLDER.INSTANCE = this
    }

    private object HOLDER {
        lateinit var INSTANCE: VideoCompressorApplication
    }

    companion object {
        val instance: VideoCompressorApplication by lazy { HOLDER.INSTANCE }
    }
}
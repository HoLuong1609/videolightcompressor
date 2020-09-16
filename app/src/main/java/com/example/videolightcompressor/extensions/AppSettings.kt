package com.example.videolightcompressor.extensions

import android.content.Context
import android.content.SharedPreferences
import com.abedelazizshe.lightcompressorlibrary.VideoQuality

object AppSettings {

    private lateinit var sharedPreferences: SharedPreferences
    private val LOCK = Any()

    fun init(context: Context) {
        if (!AppSettings::sharedPreferences.isInitialized) {
            synchronized(LOCK) {
                sharedPreferences =
                    context.getSharedPreferences("simple_saf_demo", Context.MODE_PRIVATE)
            }
        }
    }

    var keepOriginalResolution: Boolean
        set(value) {
            sharedPreferences.edit().apply {
                putBoolean(KEEP_ORIGINAL_RESOLUTION, value)
                apply()
            }
        }
        get() = sharedPreferences.getBoolean(KEEP_ORIGINAL_RESOLUTION, false)

    var videoQuality: Int
        set(value) {
            sharedPreferences.edit().apply {
                putInt(VIDEO_QUALITY, value)
                apply()
            }
        }
        get() = sharedPreferences.getInt(VIDEO_QUALITY, VideoQuality.HIGH.ordinal)

    private const val KEEP_ORIGINAL_RESOLUTION = "keep_original_resolution"
    private const val VIDEO_QUALITY = "video_quality"
}
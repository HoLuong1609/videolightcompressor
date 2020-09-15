package com.example.videolightcompressor.ui.compressor

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.videolightcompressor.R

class ConfigCompressorActivity : AppCompatActivity() {

    private lateinit var viewModel: ConfigCompressorViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_compressor)

        viewModel = ViewModelProvider(this).get(ConfigCompressorViewModel::class.java)
    }

    companion object {
        private const val KEY_VIDEO_URI = "key_video_uri"
        fun start(context: Context, uri: Uri) {
            Intent(context, ConfigCompressorActivity::class.java).apply {
                putExtra(KEY_VIDEO_URI, uri)
            }
        }
    }
}
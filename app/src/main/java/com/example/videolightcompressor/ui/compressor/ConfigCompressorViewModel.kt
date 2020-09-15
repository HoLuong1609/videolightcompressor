package com.example.videolightcompressor.ui.compressor

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.abedelazizshe.lightcompressorlibrary.CompressionListener
import com.example.videolightcompressor.extensions.compressVideo

class ConfigCompressorViewModel : ViewModel(), CompressionListener {

    fun compressor(uri: Uri) {
        uri.compressVideo(this)
    }

    override fun onStart() {
        TODO("Not yet implemented")
    }

    override fun onSuccess() {
        TODO("Not yet implemented")
    }

    override fun onFailure(failureMessage: String) {
        TODO("Not yet implemented")
    }

    override fun onProgress(percent: Float) {
        TODO("Not yet implemented")
    }

    override fun onCancelled() {
        TODO("Not yet implemented")
    }
}
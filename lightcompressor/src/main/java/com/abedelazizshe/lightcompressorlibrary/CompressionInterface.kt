package com.abedelazizshe.lightcompressorlibrary

import java.io.File

/**
 * Created by AbedElaziz Shehadeh on 27 Jan, 2020
 * elaziz.shehadeh@gmail.com
 */

interface CompressionListener {
    fun onStart()
    fun onSuccess(file: File)
    fun onFailure(failureMessage: String)
    fun onProgress(percent: Float)
    fun onCancelled()
}

interface CompressionProgressListener {
    fun onProgressChanged(percent: Float)
    fun onProgressCancelled()
}
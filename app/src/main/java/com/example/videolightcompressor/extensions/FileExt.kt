package com.example.videolightcompressor.extensions

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.abedelazizshe.lightcompressorlibrary.CompressionListener
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor
import com.abedelazizshe.lightcompressorlibrary.VideoQuality
import java.io.File
import java.io.IOException

fun Uri.getFileFromUri(context: Context): File {
    val filePath = if ("content" == scheme) getDataColumn(context, null, null)
    else path
    return File(filePath!!)
}

fun Uri.getDataColumn(
    context: Context, selection: String?,
    selectionArgs: Array<String?>?
): String? {
    var cursor: Cursor? = null
    val column = MediaStore.Images.Media.DATA
    val projection = arrayOf(
        column
    )
    try {
        cursor = context.contentResolver.query(
            this, projection, selection, selectionArgs,
            null
        )
        if (cursor != null && cursor.moveToFirst()) {
            val columnIndex: Int = cursor.getColumnIndexOrThrow(column)
            return cursor.getString(columnIndex)
        }
    } finally {
        cursor?.close()
    }
    return null
}

fun deleteTempFiles() {
    getRootFolder().listFiles()?.let {
        for (file in it) {
            file.delete()
        }
    }
}

fun Uri.compressVideo(listener: CompressionListener) {
    val file = getFileFromUri(appContext)
    val fileFormat = "." + MimeTypeMap.getFileExtensionFromUrl(file.path)
    val tempVideoCompressorFile =
        createFileTemp(TEMP_VIDEO_COMPRESSOR_FILE_NAME + System.currentTimeMillis() + fileFormat)
    VideoCompressor.start(
        file.path, tempVideoCompressorFile.path,
        listener, VideoQuality.HIGH, isMinBitRateEnabled = false, keepOriginalResolution = false
    )
}

@Throws(IOException::class)
fun createFileTemp(fileName: String): File {
    return if (Environment.getExternalStorageState() ==
        Environment.MEDIA_MOUNTED
    ) {
        val file =
            File(getRootFolder(), fileName)
        if (file.exists()) {
            file.delete()
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        file
    } else {
        val file =
            File(getRootFolder(), fileName)
        if (file.exists()) {
            file.delete()
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        file
    }
}

fun getRootFolder(): File {
    return if (Environment.getExternalStorageState() ==
        Environment.MEDIA_MOUNTED
    ) {
        val file = File(
            Environment.getExternalStorageDirectory(),
            ROOT_FOLDER
        )
        if (!file.exists()) {
            file.mkdir()
        }
        file
    } else {
        val file =
            File(ROOT_FOLDER)
        if (!file.exists()) {
            file.mkdir()
        }
        file
    }
}

const val ROOT_FOLDER = "VideoCompressor"
const val TEMP_VIDEO_COMPRESSOR_FILE_NAME = "temp_video_compressor_"
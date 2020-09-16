package com.example.videolightcompressor.extensions

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.abedelazizshe.lightcompressorlibrary.CompressionListener
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor
import com.abedelazizshe.lightcompressorlibrary.VideoQuality
import com.example.videolightcompressor.ui.compressor.VideoInfo
import wseemann.media.FFmpegMediaMetadataRetriever
import java.io.File
import java.io.IOException
import java.util.*

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

fun Uri.compressVideo(
    quality: VideoQuality,
    keepOriginalResolution: Boolean,
    listener: CompressionListener
) {
    val file = getFileFromUri(appContext)
    val fileFormat = "." + MimeTypeMap.getFileExtensionFromUrl(file.path)
    val tempVideoCompressorFile =
        createFileTemp(TEMP_VIDEO_COMPRESSOR_FILE_NAME + System.currentTimeMillis() + fileFormat)
    VideoCompressor.start(
        file.path,
        tempVideoCompressorFile.path,
        listener,
        quality,
        isMinBitRateEnabled = false,
        keepOriginalResolution = keepOriginalResolution
    )
}

fun Uri.extractVideoInfo(): VideoInfo {
    val ffmpegMediaMetadataRetriever = FFmpegMediaMetadataRetriever()
    val filePath = if ("content" == scheme) getDataColumn(appContext, null, null) else path
    ffmpegMediaMetadataRetriever.setDataSource(filePath)
    val videoDuration: String = ffmpegMediaMetadataRetriever.extractMetadata(
        FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION
    )
    val size: String = ffmpegMediaMetadataRetriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_FILESIZE)
    val width: String = ffmpegMediaMetadataRetriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
    val height: String = ffmpegMediaMetadataRetriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
    val timeInMilliseconds = videoDuration.toLong()
    return VideoInfo(
        filePath,
        "${height}x${width}",
        getMimeType(),
        getFileSize(size.toLong()),
        convertSecondsToTime(timeInMilliseconds / 1000)
    )
}

fun Uri.getMimeType(): String? {
    val extension: String?

    //Check uri format to avoid null

    //Check uri format to avoid null
    extension = if (scheme.equals(ContentResolver.SCHEME_CONTENT)) {
        //If scheme is a content
        val mime = MimeTypeMap.getSingleton()
        mime.getExtensionFromMimeType(appContext.contentResolver.getType(this))
    } else {
        //If scheme is a File
        //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
        MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(path)).toString())
    }

    return extension
}

private fun convertSecondsToTime(seconds: Long): String {
    val timeStr: String?
    val hour: Int
    var minute: Int
    val second: Int
    if (seconds <= 0) {
        return "00:00"
    } else {
        minute = seconds.toInt() / 60
        if (minute < 60) {
            second = seconds.toInt() % 60
            timeStr =
                "00:" + unitFormat(minute) + ":" + unitFormat(
                    second
                )
        } else {
            hour = minute / 60
            if (hour > 99) return "99:59:59"
            minute %= 60
            second = (seconds - hour * 3600 - minute * 60).toInt()
            timeStr =
                unitFormat(hour) + ":" + unitFormat(
                    minute
                ) + ":" + unitFormat(second)
        }
    }
    return timeStr
}

fun getFileSize(length: Long): String {
    val MB = 1024 * 1024
    if (length < MB) {
        val resultKB = length * 1.0 / 1024
        return String.format(Locale.getDefault(), "%.1f", resultKB) + " Kb"
    }
    val resultMB = length * 1.0 / MB
    return String.format(Locale.getDefault(), "%.1f", resultMB) + " Mb"
}

private fun unitFormat(i: Int): String {
    return if (i in 0..9) {
        "0$i"
    } else {
        "" + i
    }
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
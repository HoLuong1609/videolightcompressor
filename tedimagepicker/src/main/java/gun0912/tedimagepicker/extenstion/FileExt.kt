package gun0912.tedimagepicker.extenstion

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import gun0912.tedimagepicker.model.VideoInfo
import wseemann.media.FFmpegMediaMetadataRetriever
import java.io.File
import java.util.*

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

fun Uri.extractVideoInfo(context: Context): VideoInfo {
    val ffmpegMediaMetadataRetriever = FFmpegMediaMetadataRetriever()
    val filePath = if ("content" == scheme) getDataColumn(context, null, null) else path
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
        getMimeType(context),
        getFileSize(size.toLong()),
        convertSecondsToTime(timeInMilliseconds / 1000)
    )
}

fun Uri.getMimeType(context: Context): String? {
    val extension: String?

    //Check uri format to avoid null

    //Check uri format to avoid null
    extension = if (scheme.equals(ContentResolver.SCHEME_CONTENT)) {
        //If scheme is a content
        val mime = MimeTypeMap.getSingleton()
        mime.getExtensionFromMimeType(context.contentResolver.getType(this))
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
package com.example.videolightcompressor.extensions

import android.content.ContentResolver
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.abedelazizshe.lightcompressorlibrary.CompressionListener
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor
import com.abedelazizshe.lightcompressorlibrary.VideoQuality
import com.example.videolightcompressor.ui.VideoCompressorApplication
import gun0912.tedimagepicker.builder.type.MediaType
import gun0912.tedimagepicker.extenstion.getFileFromUri
import gun0912.tedimagepicker.model.Media
import gun0912.tedimagepicker.util.GalleryUtil
import io.reactivex.Single
import java.io.File
import java.io.IOException
import java.util.*

val appContext = VideoCompressorApplication.instance

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
    val type: String? = getMimeType()
    val fileFormat = ".$type"
    val videoCompressorFile =
        createFile(VIDEO_COMPRESSOR_FILE_NAME + System.currentTimeMillis() + fileFormat)
    VideoCompressor.start(
        file.path,
        videoCompressorFile,
        listener,
        quality,
        isMinBitRateEnabled = false,
        keepOriginalResolution = keepOriginalResolution
    )
}

fun Uri.getMimeType(): String? {
    return if (ContentResolver.SCHEME_CONTENT == scheme)
        appContext.contentResolver.getType(this)
    else
        MimeTypeMap.getFileExtensionFromUrl(toString()).toLowerCase(Locale.ROOT)
}

@Throws(IOException::class)
fun createFile(fileName: String): File {
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
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),
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

 fun getResultVideos(): Single<List<Media>> {
     var albumName = ""
     return Single.create { emitter ->
         try {
             val uri: Uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
             albumName = MediaStore.Video.Media.BUCKET_DISPLAY_NAME
             val sortOrder = "${MediaStore.MediaColumns.DATE_ADDED} DESC"
             val projection =
                 arrayOf(
                     MediaStore.MediaColumns._ID,
                     MediaStore.MediaColumns.DATA,
                     albumName,
                     MediaStore.MediaColumns.DATE_ADDED
                 )
             val selection = MediaStore.Images.Media.SIZE + " > 0"
             val cursor =
                 appContext.contentResolver.query(uri, projection, selection, null, sortOrder)
             val mediaList: List<Media> = cursor?.let {
                 generateSequence { if (cursor.moveToNext()) cursor else null }
                     .map { GalleryUtil.getImage(appContext, it, MediaType.VIDEO, albumName) }
                     .filter {
                         it?.uri?.getFileFromUri(appContext)?.parent == getRootFolder().path
                     }.filterNotNull()
                     .toList()
             } ?: emptyList()

             cursor?.close()
             emitter.onSuccess(mediaList)
         } catch (exception: Exception) {
             emitter.onError(exception)
         }
     }
}

const val ROOT_FOLDER = "VideoCompressor"
const val VIDEO_COMPRESSOR_FILE_NAME = "video_compressor_"
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
import gun0912.tedimagepicker.R
import gun0912.tedimagepicker.builder.type.MediaType
import gun0912.tedimagepicker.model.Album
import gun0912.tedimagepicker.model.Media
import gun0912.tedimagepicker.util.GalleryUtil
import io.reactivex.Single
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

fun Uri.compressVideo(
    quality: VideoQuality,
    keepOriginalResolution: Boolean,
    listener: CompressionListener
) {
    val file = getFileFromUri(appContext)
    val fileFormat = "." + MimeTypeMap.getFileExtensionFromUrl(file.path)
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

internal fun getMedia(context: Context, mediaType: MediaType): Single<List<Album>> {
    var albumName = ""
    return Single.create { emitter ->
        try {

            val uri: Uri

            when (mediaType) {
                MediaType.IMAGE -> {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    albumName = MediaStore.Images.Media.BUCKET_DISPLAY_NAME
                }
                MediaType.VIDEO -> {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    albumName = MediaStore.Video.Media.BUCKET_DISPLAY_NAME
                }
            }

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
                context.contentResolver.query(uri, projection, selection, null, sortOrder)
            val albumList: List<Album> = cursor?.let {

                val totalImageList =
                    generateSequence { if (cursor.moveToNext()) cursor else null }
                        .map { GalleryUtil.getImage(context, it, mediaType) }
                        .filterNotNull()
                        .toList()

                val albumList: List<Album> = totalImageList.asSequence()
                    .groupBy { media: Media -> media.albumName }
                    .toSortedMap(Comparator { albumName1: String, albumName2: String ->
                        if (albumName2 == "Camera") {
                            1
                        } else {
                            albumName1.compareTo(albumName2, true)
                        }
                    })
                    .map { entry -> GalleryUtil.getAlbum(entry) }
                    .toList()


                val totalAlbum = totalImageList.run {
                    val albumNameX = context.getString(R.string.ted_image_picker_album_all)
                    Album(
                        albumNameX,
                        getOrElse(0) { Media(context, albumNameX, Uri.EMPTY, 0) }.uri,
                        this
                    )
                }

                mutableListOf(totalAlbum).apply {
                    addAll(albumList)
                }
            } ?: emptyList()

            cursor?.close()
            emitter.onSuccess(albumList)
        } catch (exception: Exception) {
            emitter.onError(exception)
        }

    }
}

const val ROOT_FOLDER = "VideoCompressor"
const val VIDEO_COMPRESSOR_FILE_NAME = "video_compressor_"
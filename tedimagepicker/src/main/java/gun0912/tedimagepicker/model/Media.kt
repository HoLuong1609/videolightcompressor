package gun0912.tedimagepicker.model

import android.content.Context
import android.net.Uri
import gun0912.tedimagepicker.extenstion.extractVideoInfo

data class Media(
    val context: Context,
    val albumName: String,
    val uri: Uri,
    val dateAddedSecond: Long
) {
    val videoInfo = uri.extractVideoInfo(context)
}
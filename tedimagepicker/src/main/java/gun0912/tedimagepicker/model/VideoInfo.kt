package gun0912.tedimagepicker.model

import gun0912.tedimagepicker.extenstion.getFileSize

class VideoInfo(val path: String?, val resolution: String, val mimeType: String?, val duration: String, val size: Long) {

    fun getVideoSize() = getFileSize(size)
}
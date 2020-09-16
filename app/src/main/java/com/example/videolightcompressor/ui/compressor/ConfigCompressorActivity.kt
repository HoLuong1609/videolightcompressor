package com.example.videolightcompressor.ui.compressor

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.abedelazizshe.lightcompressorlibrary.CompressionListener
import com.abedelazizshe.lightcompressorlibrary.VideoQuality
import com.dd.processbutton.iml.ActionProcessButton
import com.example.videolightcompressor.R
import com.example.videolightcompressor.extensions.AppSettings
import com.example.videolightcompressor.extensions.compressVideo
import com.example.videolightcompressor.extensions.extractVideoInfo
import com.example.videolightcompressor.extensions.showSnackBar
import kotlinx.android.synthetic.main.activity_config_compressor.*

class ConfigCompressorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_compressor)

        val uri = intent.getParcelableExtra<Uri>(KEY_VIDEO_URI)
        uri?.let {
            videoView.setDataSource(it)
            val videoInfo = it.extractVideoInfo()
            tvPath.text = videoInfo.path
            tvResolution.text = videoInfo.resolution
            tvMimeType.text = videoInfo.mimeType
            tvFileSize.text = videoInfo.size
            tvDuration.text = videoInfo.duration

            keepResolutionSwitch.isChecked = AppSettings.keepOriginalResolution
            keepResolutionSwitch.setOnCheckedChangeListener { _, checked ->
                AppSettings.keepOriginalResolution = checked
            }
            radioGroup.check(getCheckedId())
            radioGroup.setOnCheckedChangeListener { radioGroup, _ ->
                when(radioGroup.checkedRadioButtonId) {
                    R.id.rdHigh -> AppSettings.videoQuality = VideoQuality.HIGH.ordinal
                    R.id.rdMedium -> AppSettings.videoQuality = VideoQuality.MEDIUM.ordinal
                    R.id.rdLow -> AppSettings.videoQuality = VideoQuality.LOW.ordinal
                }
            }
            btnStartComPress.setMode(ActionProcessButton.Mode.PROGRESS)
            btnStartComPress.setOnClickListener {
                uri.compressVideo(getVideoQuality(), AppSettings.keepOriginalResolution, object : CompressionListener {
                    override fun onStart() {
                        btnStartComPress.progress = 1
                    }

                    override fun onSuccess() {
                        btnStartComPress.progress = 100
                    }

                    override fun onFailure(failureMessage: String) {
                        showSnackBar(failureMessage)
                    }

                    override fun onProgress(percent: Float) {
                        btnStartComPress.progress = percent.toInt()
                    }

                    override fun onCancelled() {
                        btnStartComPress.error = getString(R.string.msg_failed_to_compress)
                    }
                })
            }
        }
    }

    override fun onResume() {
        super.onResume()
        videoView.onResume()
    }

    override fun onPause() {
        super.onPause()
        videoView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        videoView.releasePlayer()
    }

    private fun getCheckedId() = when(AppSettings.videoQuality) {
        VideoQuality.HIGH.ordinal -> R.id.rdHigh
        VideoQuality.MEDIUM.ordinal -> R.id.rdMedium
        else -> R.id.rdLow
    }

    private fun getVideoQuality() = when(AppSettings.videoQuality) {
        VideoQuality.HIGH.ordinal -> VideoQuality.HIGH
        VideoQuality.MEDIUM.ordinal -> VideoQuality.MEDIUM
        else -> VideoQuality.LOW
    }

    companion object {
        private const val KEY_VIDEO_URI = "key_video_uri"
        fun start(context: Context, uri: Uri) {
            Intent(context, ConfigCompressorActivity::class.java).apply {
                putExtra(KEY_VIDEO_URI, uri)
                context.startActivity(this)
            }
        }
    }
}
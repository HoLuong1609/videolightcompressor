package com.example.videolightcompressor.ui.compressionresult

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewTreeObserver.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.videolightcompressor.R
import com.example.videolightcompressor.extensions.*
import gun0912.tedimagepicker.extenstion.extractVideoInfo
import kotlinx.android.synthetic.main.activity_compression_result.*

class CompressionResultActivity : AppCompatActivity() {

    private var mUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compression_result)
        setTitle(R.string.compression_result)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        mUri = intent.getParcelableExtra(KEY_VIDEO_URI)
        mUri?.let {
            videoView.setDataSource(it)
            val videoInfo = it.extractVideoInfo(this)
            tvFileSizeAfter.text = videoInfo.size
            tvPath.text = videoInfo.path
            tvResolution.text = videoInfo.resolution

            val previousSizeString = intent.getStringExtra(KEY_PREVIOUS_FILE_SIZE)
            val previousFileSize = previousSizeString!!.split(" ")[0].toFloat()
            val newFileSize = videoInfo.size.split(" ")[0].toFloat()

            tvFileSizeBefore.text = previousSizeString
            tvFileSizeBeforeIndicator.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    tvFileSizeBeforeIndicator.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    val lp = tvFileSizeAfterIndicator.layoutParams as ConstraintLayout.LayoutParams
                    lp.width = (tvFileSizeBeforeIndicator.width * newFileSize / previousFileSize).toInt()
                    tvFileSizeAfterIndicator.layoutParams = lp
                }

            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.compression_result, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_rate -> {
                openStore()
                true
            }
            R.id.action_delete -> {
                showAlertDialog(title = getString(R.string.delete_video), msg = getString(R.string.delete_video_msg), onPositiveButtonClick = {
                    deleteVideo()
                }, onNegativeButtonClick = {})
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun deleteVideo() {
        mUri?.let {
            val file = it.getFileFromUri(this)
            if (file.delete()) {
                toast(R.string.video_deleted_msg)
                finish()
            } else {
                toast(R.string.video_deleted_msg_failed)
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

    companion object {
        private const val KEY_VIDEO_URI = "key_video_uri"
        private const val KEY_PREVIOUS_FILE_SIZE = "key_previous_file_size"
        fun start(context: Context, uri: Uri, previousFileSize: String) {
            Intent(context, CompressionResultActivity::class.java).apply {
                putExtra(KEY_VIDEO_URI, uri)
                putExtra(KEY_PREVIOUS_FILE_SIZE, previousFileSize)
                context.startActivity(this)
            }
        }
    }
}
package com.example.videolightcompressor.ui.gallery

import android.net.Uri
import android.os.Bundle
import android.view.View
import com.example.videolightcompressor.R
import com.example.videolightcompressor.base.BaseBottomSheetFragment
import com.example.videolightcompressor.event.DeleteVideoEvent
import com.example.videolightcompressor.extensions.getFileFromUri
import com.example.videolightcompressor.extensions.showAlertDialog
import com.example.videolightcompressor.extensions.toast
import gun0912.tedimagepicker.extenstion.extractVideoInfo
import kotlinx.android.synthetic.main.fragment_video_detail.*
import org.greenrobot.eventbus.EventBus

class VideoDetailFragment :
    BaseBottomSheetFragment() {

    override fun layoutResId(): Int = R.layout.fragment_video_detail

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val uri = arguments?.getParcelable<Uri>(KEY_VIDEO_URI)
        uri?.let {
            videoView.setDataSource(it)
            val videoInfo = it.extractVideoInfo(requireContext())
            tvPath.text = videoInfo.path
            tvResolution.text = videoInfo.resolution
            tvMimeType.text = videoInfo.mimeType
            tvFileSize.text = videoInfo.getVideoSize()
            tvDuration.text = videoInfo.duration
        }
        actionClose.setOnClickListener {
            dismiss()
        }
        actionDelete.setOnClickListener {
            context?.showAlertDialog(title = getString(R.string.delete_video), msg = getString(R.string.delete_video_msg), onPositiveButtonClick = {
                uri?.let { video -> deleteVideo(video) }
            }, onNegativeButtonClick = {})
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

    override fun onDestroyView() {
        super.onDestroyView()
        videoView.releasePlayer()
    }

    private fun deleteVideo(uri: Uri) {
        val file = uri.getFileFromUri(requireContext())
        if (file.delete()) {
            EventBus.getDefault().post(DeleteVideoEvent())
            dismiss()
        } else {
            context?.toast(R.string.video_deleted_msg_failed)
        }
    }

    companion object {
        const val KEY_VIDEO_URI = "key_video_uri"
        fun newInstance(uri: Uri): VideoDetailFragment =
            VideoDetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_VIDEO_URI, uri)
                }
            }
    }
}
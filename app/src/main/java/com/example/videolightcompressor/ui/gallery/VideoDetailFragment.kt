package com.example.videolightcompressor.ui.gallery

import android.net.Uri
import android.os.Bundle
import android.view.View
import com.example.videolightcompressor.R
import com.example.videolightcompressor.base.BaseBottomSheetFragment
import gun0912.tedimagepicker.extenstion.extractVideoInfo
import kotlinx.android.synthetic.main.fragment_video_detail.*

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
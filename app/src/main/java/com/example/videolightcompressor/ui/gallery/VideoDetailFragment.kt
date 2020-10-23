package com.example.videolightcompressor.ui.gallery

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.example.videolightcompressor.R
import com.tedpark.tedpermission.rx2.TedRx2Permission
import gun0912.tedimagepicker.base.BaseBottomSheetFragment
import gun0912.tedimagepicker.event.DeleteVideoEvent
import gun0912.tedimagepicker.extenstion.*
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_video_detail.*
import org.greenrobot.eventbus.EventBus

class VideoDetailFragment :
    BaseBottomSheetFragment() {

    private val disposable = CompositeDisposable()

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
        disposable.clear()
    }

    private fun deleteVideo(uri: Uri) {
        TedRx2Permission.with(context)
            .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .request()
            .subscribe({ permissionResult ->
                if (permissionResult.isGranted) {
                    val file = uri.getFileFromUri(requireContext())
                    if (file.delete()) {
                        EventBus.getDefault().post(DeleteVideoEvent())
                        dismiss()
                    } else {
                        context?.toast(R.string.video_deleted_msg_failed)
                    }
                } else {
                    context?.toast(getString(R.string.permission_denied))
                }
            }, { throwable -> context?.toast(throwable.localizedMessage ?: "") })
            .addTo(disposable)
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
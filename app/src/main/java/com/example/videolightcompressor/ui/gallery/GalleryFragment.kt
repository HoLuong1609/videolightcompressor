package com.example.videolightcompressor.ui.gallery

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.example.videolightcompressor.R
import com.example.videolightcompressor.event.DeleteVideoEvent
import com.example.videolightcompressor.extensions.addTo
import com.example.videolightcompressor.extensions.getFileFromUri
import com.example.videolightcompressor.extensions.showAlertDialog
import com.example.videolightcompressor.extensions.toast
import com.example.videolightcompressor.ui.adapter.GalleryAdapter
import com.tedpark.tedpermission.rx2.TedRx2Permission
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_gallery.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class GalleryFragment : Fragment() {

    private val disposable = CompositeDisposable()

    private val galleryViewModel by viewModels<GalleryViewModel> {
        ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
    }
    private var mAdapter: GalleryAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_gallery, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        galleryViewModel.apply {
            videoList.observe(viewLifecycleOwner, {
                mAdapter?.setData(it)
            })
            showVideoDetail.observe(viewLifecycleOwner, {uri ->
                Navigation.findNavController(view).navigate(R.id.bottomSheetDialog, Bundle().apply {
                    putParcelable(VideoDetailFragment.KEY_VIDEO_URI, uri)
                })
            })
            deleteVideo.observe(viewLifecycleOwner, {uri ->
                context?.showAlertDialog(title = getString(R.string.delete_video), msg = getString(R.string.delete_video_msg), onPositiveButtonClick = {
                    deleteVideo(uri)
                }, onNegativeButtonClick = {})
            })
        }
        TedRx2Permission.with(context)
            .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .request()
            .subscribe({ permissionResult ->
                if (permissionResult.isGranted) {
                    galleryViewModel.getVideoMedia()
                } else {
                    context?.toast(getString(R.string.permission_denied))
                }
            }, { throwable -> context?.toast(throwable.localizedMessage ?: "") })
            .addTo(disposable)
        EventBus.getDefault().register(this)
    }

    override fun onResume() {
        super.onResume()
        getVideo()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable.clear()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun deleteVideo(event: DeleteVideoEvent) {
        getVideo()
    }

    private fun setupRecyclerView() {
        rvVideos.run {
            mAdapter = GalleryAdapter(galleryViewModel, viewLifecycleOwner)
            GridLayoutManager(context, 3).apply {
                layoutManager = this
            }
            setHasFixedSize(true)
            adapter = mAdapter
        }
    }

    private fun getVideo() {
        if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            galleryViewModel.getVideoMedia()
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                //Permission don't granted
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        permission
                    )
                ) {
                    // Permission isn't granted
                    false
                } else {
                    // Permission don't granted and don't show dialog again.
                    false
                }
            } else true
        } else {
            true
        }
    }

    private fun deleteVideo(uri: Uri) {
        val file = uri.getFileFromUri(requireContext())
        if (file.delete()) {
            galleryViewModel.getVideoMedia()
        } else {
            context?.toast(R.string.video_deleted_msg_failed)
        }
    }
}
package com.example.videolightcompressor.ui.gallery

import android.Manifest
import android.content.pm.PackageManager
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
import androidx.recyclerview.widget.GridLayoutManager
import com.example.videolightcompressor.R
import com.example.videolightcompressor.extensions.addTo
import com.example.videolightcompressor.extensions.toast
import com.example.videolightcompressor.ui.adapter.GalleryAdapter
import com.tedpark.tedpermission.rx2.TedRx2Permission
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_gallery.*

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
        galleryViewModel.videoList.observe(viewLifecycleOwner, {
            mAdapter?.setData(it)
        })
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
    }

    override fun onResume() {
        super.onResume()
        if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            galleryViewModel.getVideoMedia()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
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
}
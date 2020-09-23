package com.example.videolightcompressor.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.videolightcompressor.R
import com.example.videolightcompressor.ui.adapter.GalleryAdapter
import kotlinx.android.synthetic.main.fragment_gallery.*

class GalleryFragment : Fragment() {

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
    }

    override fun onResume() {
        super.onResume()
        galleryViewModel.getVideoMedia()
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
}
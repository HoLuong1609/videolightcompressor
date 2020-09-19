package com.example.videolightcompressor.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.videolightcompressor.R
import com.example.videolightcompressor.ui.compressor.CompressorActivity
import gun0912.tedimagepicker.builder.TedImagePicker

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        root.findViewById<View>(R.id.btnComPressVideo).setOnClickListener {
            openVideoPicker()
        }
        return root
    }

    private fun openVideoPicker() {
        TedImagePicker.with(requireContext())
            .video()
            .title(R.string.ted_video_picker_title)
            .start { uri ->
                CompressorActivity.start(requireContext(), uri)
            }
    }
}
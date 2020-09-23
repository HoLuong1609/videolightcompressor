package com.example.videolightcompressor.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.videolightcompressor.R
import com.example.videolightcompressor.BR
import com.example.videolightcompressor.databinding.ItemGalleryBinding
import com.example.videolightcompressor.ui.gallery.GalleryViewModel
import gun0912.tedimagepicker.model.Media

class GalleryAdapter(
    var mViewModel: GalleryViewModel,
    var parentLifecycleOwner: LifecycleOwner
) :
    ListAdapter<Media, GalleryAdapter.ChatGalleryHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatGalleryHolder =
        ChatGalleryHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_gallery,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ChatGalleryHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setData(data: List<Media>) {
        submitList(data)
        notifyDataSetChanged()
    }

    class DiffCallback : DiffUtil.ItemCallback<Media>() {
        override fun areItemsTheSame(
            oldItem: Media,
            newItem: Media
        ): Boolean {
            return oldItem.dateAddedSecond == newItem.dateAddedSecond
        }

        override fun areContentsTheSame(
            oldItem: Media,
            newItem: Media
        ) = oldItem.dateAddedSecond == newItem.dateAddedSecond
    }

    inner class ChatGalleryHolder(private val viewDataBinding: ItemGalleryBinding) :
        RecyclerView.ViewHolder(viewDataBinding.root) {
        fun bind(item: Media) {
            viewDataBinding.apply {
                setVariable(BR.media, item)
                setVariable(BR.viewModel, mViewModel)
                lifecycleOwner = parentLifecycleOwner
                executePendingBindings()
            }
        }
    }
}
package com.example.videolightcompressor.ui.gallery

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import gun0912.tedimagepicker.extenstion.addTo
import com.example.videolightcompressor.extensions.getResultVideos
import gun0912.tedimagepicker.model.Media
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class GalleryViewModel : ViewModel() {

    private val disposable = CompositeDisposable()
    val videoList = MutableLiveData<List<Media>>()
    val showVideoDetail = MutableLiveData<Uri>()
    val deleteVideo = MutableLiveData<Uri>()

    fun getVideoMedia() {
        getResultVideos().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { mediaList ->
                videoList.value = mediaList
            }.addTo(disposable)
    }

    fun showVideoDetail(uri: Uri) {
        showVideoDetail.value = uri
    }

    fun delete(uri: Uri) {
        deleteVideo.value = uri
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}
package com.example.videolightcompressor.ui.gallery

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.videolightcompressor.extensions.addTo
import com.example.videolightcompressor.extensions.getResultVideos
import gun0912.tedimagepicker.model.Media
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class GalleryViewModel : ViewModel() {

    private val disposable = CompositeDisposable()
    val videoList = MutableLiveData<List<Media>>()

    fun getVideoMedia() {
        getResultVideos().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { mediaList ->
                videoList.value = mediaList
            }.addTo(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}
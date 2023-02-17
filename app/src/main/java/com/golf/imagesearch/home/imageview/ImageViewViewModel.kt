package com.golf.imagesearch.home.imagesearch

import com.golf.imagesearch.base.BaseViewModel
import com.golf.imagesearch.repository.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class ImageViewViewModel @Inject constructor(
    private val imageRepository: ImageRepository
) :
    BaseViewModel() {

     val viewedImageIndexLiveData
        get() = imageRepository.viewedImageIndexLiveData

    val removeImageIndexLiveData
        get() = imageRepository.removeImageIndexLiveData

    val imageSearchListLiveData
        get() = imageRepository.imageSearchListLiveData

    val pageLiveData
        get() = imageRepository.pageLiveData
}


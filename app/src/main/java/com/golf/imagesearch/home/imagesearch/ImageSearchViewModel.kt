package com.golf.imagesearch.home.imagesearch

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.golf.imagesearch.base.BaseFragment
import com.golf.imagesearch.base.BaseViewModel
import com.golf.imagesearch.base.SingleLiveEvent
import com.golf.imagesearch.network.launchScoped
import com.golf.imagesearch.repository.ImageRepository
import com.golf.imagesearch.utils.Constants.API_BASE_URL
import com.golf.imagesearch.utils.Constants.PAGE_SIZE
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class ImageSearchViewModel @Inject constructor(
    private val imageRepository: ImageRepository
) :
    BaseViewModel() {

    private val nextPageLiveData = SingleLiveEvent<String?>()
    val loadingLiveData = SingleLiveEvent<Boolean?>()
    private val loadingMoreLiveData = SingleLiveEvent<Boolean>()
    val hasMoreDataLiveData = SingleLiveEvent<Boolean>()
    val pendingFragment = MutableLiveData<BaseFragment<*, *>?>()
    val pendingTask = MutableLiveData<() -> Unit>()

    val viewedImageIndexLiveData
        get() = imageRepository.viewedImageIndexLiveData

    val removeImageIndexLiveData
        get() = imageRepository.removeImageIndexLiveData

    val imageSearchListLiveData
        get() = imageRepository.imageSearchListLiveData

    val pageLiveData
        get() = imageRepository.pageLiveData

    val imageSearchListRuleLiveData = MediatorLiveData<ImageSearchListRule>().apply {
        value = ImageSearchListRule()
        addSource(imageSearchListLiveData) {
            it ?: return@addSource
            value?.copy(imageList = it).takeIf { new -> new != value }?.let { new -> value = new }
        }
        addSource(nextPageLiveData) {
            it ?: return@addSource
            value?.copy(nextPage = it).takeIf { new -> new != value }?.let { new -> value = new }
        }
        addSource(loadingLiveData) {
            it ?: return@addSource
            value?.copy(isLoading = it).takeIf { new -> new != value }?.let { new -> value = new }
        }
        addSource(loadingMoreLiveData) {
            it ?: return@addSource
            value?.copy(isLoadingMoreData = it).takeIf { new -> new != value }
                ?.let { new -> value = new }
        }
        addSource(hasMoreDataLiveData) {
            it ?: return@addSource
            value?.copy(hasMoreData = it).takeIf { new -> new != value }?.let { new -> value = new }
        }
    }

    fun searchImage(searchQuery: String, perPage: Int = PAGE_SIZE, page: Int) {
        launchScoped(imageRepository.imageSearch(searchQuery, perPage, page),
            onSuccess = { response ->
                val data =
                    response?.photos?.toMutableList() ?: mutableListOf()
                val hasMoreData =
                    data.isNotEmpty() && data.size % PAGE_SIZE == 0
                hasMoreDataLiveData.postValue(hasMoreData)
                loadingLiveData.postValue(false)
                loadingMoreLiveData.postValue(false)
                val nextPagePath = response?.nextPage?.removePrefix(API_BASE_URL)
                nextPageLiveData.postValue(nextPagePath)

                val dataResult = imageSearchListLiveData.value?.toMutableList()
                if (response?.page == 1) {
                    dataResult?.clear()
                }
                dataResult?.addAll(data)
                imageSearchListLiveData.postValue(dataResult)
            }, onFailure = { error, data ->
//                onHideLoading()
                onDomainError(error)
            }, onLoading = {
//                if (page == 1) onShowLoading()
            }, onShowError = false
        )
    }
}


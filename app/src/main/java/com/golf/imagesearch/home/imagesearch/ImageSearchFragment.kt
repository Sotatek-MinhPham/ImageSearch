package com.golf.imagesearch.home.imagesearch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View.OnTouchListener
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.golf.imagesearch.base.BaseFragment
import com.golf.imagesearch.databinding.FragmentImageSearchBinding
import com.golf.imagesearch.utils.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ImageSearchFragment :
    BaseFragment<ImageSearchViewModel, FragmentImageSearchBinding>() {

    override fun getViewModelClass(): Class<ImageSearchViewModel> = ImageSearchViewModel::class.java

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentImageSearchBinding
        get() = FragmentImageSearchBinding::inflate

    var keywordSearch = ""
    private val imageSearchController by lazy {
        ImageSearchController(onLoadMore = {
            if (keywordSearch.length >= 2) {
                viewModel.pageLiveData.value?.let {
                    val newValue = it + 1
                    viewModel.pageLiveData.value = newValue
                    viewModel.searchImage(keywordSearch, page = newValue)
                }
            }
        }, onClickImage = { photo ->
            viewModel.imageSearchListLiveData.value?.let { list ->
                val indexClicked = list.indexOf(photo)
                viewModel.viewedImageIndexLiveData.postValue(indexClicked)
                baseNavigationListener.forImage.showImageViewFragment(
                    list
                )
            }
        })
    }

    override fun setupViews(savedInstanceState: Bundle?) {
        binding.layoutToolbar.navigationIcon = null

        binding.edtSearchQuery.doAfterTextChanged { s ->
            binding.ivClear.isVisible = s.toString().length > 0
            keywordSearch = s.toString().trim()
            if (keywordSearch.length >= 2) {
                binding.rvImageSearchList.smoothScrollToPosition(0)
                viewModel.pageLiveData.value = 1
                viewModel.pageLiveData.value?.let {
                    viewModel.searchImage(keywordSearch, page = it)
                }
            } else {
                viewModel.imageSearchListLiveData.postValue(mutableListOf())
            }
        }
        binding.rvImageSearchList.setController(imageSearchController)
        binding.rvImageSearchList.setOnTouchListener(OnTouchListener
        { v, event ->
            binding.edtSearchQuery.hideKeyboard()
            false
        })

        viewModel.imageSearchListRuleLiveData.observe(viewLifecycleOwner)
        {
            imageSearchController.hasMoreData = it.hasMoreData
            imageSearchController.isLoading = it.isLoadingMoreData
            binding.shimmerLayout.isVisible = it.isLoading
            binding.rvImageSearchList.isVisible = !it.isLoading
            val data = it.imageList ?: mutableListOf()
            binding.animSearch.isVisible =
                !it.isLoading && data.isEmpty() &&
                        (keywordSearch.length > 2 || keywordSearch.length == 0)

            imageSearchController.nextPage = it.nextPage
            imageSearchController.photoList = data
        }

        viewModel.viewedImageIndexLiveData.observe((viewLifecycleOwner))
        {
            it?.let { position ->
                binding.rvImageSearchList.scrollToPosition(position)
            }
        }

        viewModel.removeImageIndexLiveData.observe(viewLifecycleOwner)
        { index ->
            val data = viewModel.imageSearchListLiveData.value?.toMutableList()
            index?.let {
                if (index >= 0) {
                    data?.removeAt(index)
                    viewModel.imageSearchListLiveData.postValue(data)
                }
            }
        }
    }

    override fun enableBackPressed(): Boolean {
        requireActivity().finish()
        return false
    }
}
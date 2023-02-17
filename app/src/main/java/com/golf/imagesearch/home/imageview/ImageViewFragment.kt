package com.golf.imagesearch.home.imagesearch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.golf.imagesearch.R
import com.golf.imagesearch.base.BaseFragment
import com.golf.imagesearch.databinding.FragmentImageViewBinding
import com.golf.imagesearch.home.imageview.ImageObjectFragment
import com.golf.imagesearch.home.imageview.ImageViewAdapter
import com.golf.imagesearch.model.Photo
import com.golf.imagesearch.utils.setOnDebouncedClickListener
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ImageViewFragment :
    BaseFragment<ImageViewViewModel, FragmentImageViewBinding>() {

    override fun getViewModelClass(): Class<ImageViewViewModel> = ImageViewViewModel::class.java

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentImageViewBinding
        get() = FragmentImageViewBinding::inflate

    var imageList: MutableList<Photo?> = mutableListOf()
    private var pagerAdapter: ImageViewAdapter? = null

    override fun setupViews(savedInstanceState: Bundle?) {

        pagerAdapter = ImageViewAdapter(this)
        val tabFragments = mutableListOf<BaseFragment<*, *>>()
        for (image in imageList) {
            tabFragments.add(ImageObjectFragment.newInstance().apply {
                this.photo = image
            })
        }

        pagerAdapter?.tabFragments = tabFragments
        binding.vpImageViewer.offscreenPageLimit = 4
        binding.vpImageViewer.adapter = pagerAdapter
        binding.vpImageViewer.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewModel.viewedImageIndexLiveData.postValue(position)

            }
        })

        binding.fabRemove.drawable.mutate()
            .setTint(ContextCompat.getColor(requireContext(), R.color.white))
        binding.fabRemove.setOnDebouncedClickListener {
            viewModel.removeImageIndexLiveData.postValue(binding.vpImageViewer.getCurrentItem())
        }

        viewModel.viewedImageIndexLiveData.observe(viewLifecycleOwner) { index ->
            index?.let { indexNotNull ->
                binding.vpImageViewer.setCurrentItem(
                    indexNotNull,
                    false
                )
            }
        }

        viewModel.removeImageIndexLiveData.observe(viewLifecycleOwner) { index ->
            pagerAdapter?.let {
                index?.let { indexNotNull ->
                    if (index >= 0) {
                        it.removeView(indexNotNull)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.removeImageIndexLiveData.postValue(-1)
    }
}
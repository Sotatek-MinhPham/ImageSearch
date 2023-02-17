package com.golf.imagesearch.home.imageview

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.golf.imagesearch.base.BaseFragment
import com.golf.imagesearch.databinding.FragmentImageObjectBinding
import com.golf.imagesearch.model.Photo
import com.golf.imagesearch.utils.ImageViewTouchBase

class ImageObjectFragment : BaseFragment<ImageObjectViewModel, FragmentImageObjectBinding>() {

    override fun getViewModelClass(): Class<ImageObjectViewModel> =
        ImageObjectViewModel::class.java

    var photo : Photo? = null

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentImageObjectBinding
        get() = FragmentImageObjectBinding::inflate

    override fun onCreateViewModel() {
        viewModel = requireActivity().run { ViewModelProvider(this).get(getViewModelClass()) }
    }

    override fun setupViews(savedInstanceState: Bundle?) {
        binding.tvPhotographer.text = "@" + photo?.photographer
        binding.ivImage.displayType = ImageViewTouchBase.DisplayType.FIT_IF_BIGGER

        Glide.with(binding.ivImage.context)
            .asBitmap()
            .load(photo?.src?.large2x)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    binding.ivImage.setImageBitmap(resource, null, -1f, -1f);
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
    }

    companion object {
        fun newInstance() = ImageObjectFragment()
    }

}


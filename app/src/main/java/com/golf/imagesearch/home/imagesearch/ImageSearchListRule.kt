package com.golf.imagesearch.home.imagesearch

import com.golf.imagesearch.model.Photo


data class ImageSearchListRule(
    var imageList: MutableList<Photo?>? = null,

    var nextPage: String? = null,

    var isLoading: Boolean = false,

    var isLoadingMoreData: Boolean = false,

    var hasMoreData: Boolean = false,

    var deepLink: String? = "",

    val countNotPreviewed: Int = 0
)
package com.golf.imagesearch.model

import com.google.gson.annotations.SerializedName

data class ResponseSearchImageItem(
    @field:SerializedName("page")
    val page: Int = 0,

    @field:SerializedName("per_page")
    val perPage: Int = 0,

    @field:SerializedName("photos")
    val photos: List<Photo>? = null,

    @field:SerializedName("total_results")
    val totalResults: Int = 1412,

    @field:SerializedName("next_page")
    val nextPage: String? = null,
    )

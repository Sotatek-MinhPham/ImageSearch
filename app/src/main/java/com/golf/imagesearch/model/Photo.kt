package com.golf.imagesearch.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class Photo(
    @field:SerializedName("id")
    val id: BigDecimal = BigDecimal(-1),

    @field:SerializedName("width")
    val width: Int = -1,

    @field:SerializedName("height")
    val height: Int = -1,

    @field:SerializedName("url")
    val url: String? = null,

    @field:SerializedName("photographer")
    val photographer: String? = null,

    @field:SerializedName("photographer_url")
    val photographer_url: String? = null,

    @field:SerializedName("photographer_id")
    val photographer_id: BigDecimal = BigDecimal(-1),

    @field:SerializedName("avg_color")
    val avgColor: String? = null,

    @field:SerializedName("src")
    val src: ImageSource? = null,

    @field:SerializedName("liked")
    val liked: Boolean = false,

    @field:SerializedName("alt")
    val alt: String? = null,
)

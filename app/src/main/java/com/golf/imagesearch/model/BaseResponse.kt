package com.golf.imagesearch.model

import com.google.gson.annotations.SerializedName

data class BaseResponse<T>(
    @SerializedName("success")
    var success: Boolean? = false,
    @SerializedName("result")
    var result: T? = null
)
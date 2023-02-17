package com.golf.imagesearch.model

import com.google.gson.annotations.SerializedName

data class ErrorModel(
    @field:SerializedName("code")
    val code: String? = "",


    @field:SerializedName("message")
    val message: String? = "",


    @field:SerializedName("name")
    val name: String? = "",


)

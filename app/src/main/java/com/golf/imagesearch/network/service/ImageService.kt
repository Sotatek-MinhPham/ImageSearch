package com.golf.imagesearch.network.service

import com.golf.imagesearch.model.ResponseSearchImageItem
import retrofit2.http.GET
import retrofit2.http.Query

interface ImageService {

    @GET("v1/search")
    suspend fun search(
        @Query("query") searchQuery: String,
        @Query("per_page") perPage: Int,
        @Query("page") page: Int
    ): ResponseSearchImageItem?

}
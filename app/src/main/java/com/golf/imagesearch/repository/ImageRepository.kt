package com.golf.imagesearch.repository

//import com.golf.imagesearch.network.service.AuthService
import androidx.lifecycle.MutableLiveData
import com.golf.imagesearch.base.BaseRepository
import com.golf.imagesearch.model.Photo
import com.golf.imagesearch.network.Resource
import com.golf.imagesearch.network.eitherNetwork
import com.golf.imagesearch.network.service.ImageService
import com.golf.imagesearch.network.toResource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageRepository @Inject constructor(
    private val imageService: ImageService
) : BaseRepository() {
    val viewedImageIndexLiveData = MutableLiveData<Int?>()
    val removeImageIndexLiveData = MutableLiveData<Int?>()
    val imageSearchListLiveData = MutableLiveData<MutableList<Photo?>>(mutableListOf())
    val pageLiveData = MutableLiveData<Int?>()

    fun imageSearch(searchQuery: String, perPage : Int, page: Int) = flow {
        emit(Resource.Loading())
        emit(eitherNetwork { imageService.search(searchQuery, perPage, page) }.toResource())
    }

}
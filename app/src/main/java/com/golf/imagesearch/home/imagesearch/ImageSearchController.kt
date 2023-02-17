package com.golf.imagesearch.home.imagesearch

import android.widget.ImageView
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.google.android.material.card.MaterialCardView
import com.golf.imagesearch.R
import com.golf.imagesearch.base.BaseEpoxyHolder
import com.golf.imagesearch.model.Photo
import com.golf.imagesearch.utils.Constants.PAGE_SIZE
import com.golf.imagesearch.utils.loadImageSearch
import com.golf.imagesearch.utils.loading
import com.golf.imagesearch.utils.setOnDebouncedClickListener

class ImageSearchController constructor(
    val onLoadMore: (page: Int) -> Unit,
    private val onClickImage: ((photo: Photo?) -> Unit),
) : EpoxyController() {

    var isLoading = false
        set(value) {
            field = value
            requestModelBuild()
        }
    var hasMoreData = false
        set(value) {
            field = value
            requestModelBuild()
        }

    var nextPage: String? = null
        set(value) {
            field = value
            requestModelBuild()
        }

    var photoList: MutableList<Photo?> = mutableListOf()
        set(value) {
            field = value
            requestModelBuild()
        }

    override fun buildModels() {
        photoList.forEachIndexed { index, image ->
            searchImage {
                id("searchImage $index")
                photo(image)
                onClick {
                    onClickImage(image)
                }
            }
        }

        if (hasMoreData) {
            loading {
                id("loading")
                onBind { model, view, position ->
                    if (!isLoading) {
                        onLoadMore(photoList.size / PAGE_SIZE + 1)
                    }
                }
            }
        }
    }
}

@EpoxyModelClass(layout = R.layout.item_search_image)
abstract class SearchImageModel : EpoxyModelWithHolder<SearchImageModel.Holder>() {

    @EpoxyAttribute
    var photo: Photo? = null

    @EpoxyAttribute
    var onClick: ((Photo?) -> Unit)? = null

    override fun bind(holder: Holder) {
        super.bind(holder)
        photo?.let { image ->
            loadImageSearch(
                holder.cardViewSearchedImage,
                holder.ivThumbnail,
                image?.src?.large,
                placeHolder = R.drawable.image_not_found
            )
            holder.tvPhotographer.text = "@" + image?.photographer
            onClick?.let { onClick ->
                holder.view.setOnDebouncedClickListener {
                    onClick?.invoke(photo?.copy())
                }
            }
        }
    }

    class Holder : BaseEpoxyHolder() {
        val cardViewSearchedImage: MaterialCardView by bind(R.id.cardViewSearchedImage)
        val ivThumbnail: ImageView by bind(R.id.ivThumbnail)
        val tvPhotographer: TextView by bind(R.id.tvPhotographer)
    }
}
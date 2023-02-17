package com.golf.imagesearch.utils

import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.golf.imagesearch.R
import com.golf.imagesearch.base.BaseEpoxyHolder


@EpoxyModelClass(layout = R.layout.item_loading)
abstract class LoadingModel : EpoxyModelWithHolder<LoadingModel.Holder>() {
    class Holder : BaseEpoxyHolder()
}
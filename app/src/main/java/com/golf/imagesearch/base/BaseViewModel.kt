package com.golf.imagesearch.base

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.golf.imagesearch.R
import com.golf.imagesearch.network.DomainError
import com.golf.imagesearch.network.ERRORCODE_IMAGE_NOT_FOUND
import com.golf.imagesearch.network.HTTPCODE_ERROR_SYNTAX
import com.golf.imagesearch.utils.toResourceString
import timber.log.Timber

open class BaseViewModel : ViewModel() {
    val progressLoadingLiveData = SingleLiveEvent<Boolean>()
    val showErrorLiveData = SingleLiveEvent<String>()
    val showMessageLiveData = SingleLiveEvent<String>()
    val showMessageSuccessLiveData = SingleLiveEvent<String>()
    val tokenExpiredLiveData = SingleLiveEvent<String>()
    val navigationFragmentLiveData = SingleLiveEvent<NavigationFragment>()
    val popToFragmentLiveData = SingleLiveEvent<Fragment>()

    fun onShowLoading() {
        progressLoadingLiveData.postValue(true)
    }

    fun onHideLoading() {
        progressLoadingLiveData.postValue(false)
    }

    /**
     * uses placeholder "Something went wrong" in case of empty error message
     */
    fun onDomainError(domainError: DomainError) {
        Timber.tag("Minh")
        if (domainError is DomainError.ApiError<*> && domainError.httpCode == HTTPCODE_ERROR_SYNTAX
            && domainError.errorCode.equals(
                ERRORCODE_IMAGE_NOT_FOUND
            )
        ) {
            Timber.d("onDomainError: 400 " + R.string.error_message_cannot_detect_face_in_image.toResourceString())
            showErrorLiveData.postValue(R.string.error_message_cannot_detect_face_in_image.toResourceString())
        } else {
            Timber.d("onDomainError: other " + domainError.errorMessage)
            showErrorLiveData.postValue(domainError.errorMessage)
        }

    }

}
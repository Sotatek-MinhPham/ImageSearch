package com.golf.imagesearch.utils

import android.util.ArrayMap
import com.golf.imagesearch.R

object Constants {

    val API_BASE_URL: String = "https://api.pexels.com/"
    val API_KEY: String = "lWwMXgI4pH9uhwFyGyCmpERCgtmRIzofBUZxsZmmWiuhOLWu5n8Ua0Ff"
    val PAGE_SIZE = 15

    object ErrorApiCode {
        const val cannotDetectFace = "223"
    }

    fun getErrorApiContent(errorCode: String): String? {
        val errorMap = ArrayMap<String, String>()
        errorMap[ErrorApiCode.cannotDetectFace] = R.string.error_message_cannot_detect_face_in_image.toResourceString()
        return errorMap[errorCode]
    }
}

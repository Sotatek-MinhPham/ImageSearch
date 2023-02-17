package com.golf.imagesearch.network


import com.google.gson.annotations.SerializedName
import com.golf.imagesearch.utils.Constants.getErrorApiContent

/**
 * Domain representation for errors/exceptions/etc
 */

const val ERRORCODE_SYSTEM = "-100"
const val ERRORCODE_NETWORK_DNS = "-101"
const val ERRORCODE_NETWORK_CONNECTION_TIMEDOUT = "-102"
const val ERRORCODE_NETWORK_SOCKET_TIMEDOUT = "-103"
const val ERRORCODE_NETWORK_SOCKET = "-104"
const val ERRORCODE_NETWORK_SSL_HANDSHAKE = "-105"

const val ERRORCODE_IMAGE_NOT_FOUND = "223"

const val HTTPCODE_ERROR_SYNTAX = 400

interface DomainErrorInterface {
    val errorCode: String
    val errorMessage: String
    val errorMessageApi: String
}

sealed class DomainError : DomainErrorInterface {
    data class ApiError<T : BackendTypedError>(
        val httpCode: Int,
        val httpMessage: String,
        val error: T? = null
    ) : DomainError() {

        override val errorCode: String
            get() = when (error) {
                is BaseError -> error.code ?: ""
                else -> ""
            }

        override val errorMessage
            get() = when (error) {
                is BaseError -> {
                    val id = error.message ?: ""
                    var errorMessageWithId = getErrorApiContent(id)
                    if (errorMessageWithId.isNullOrEmpty()) {
                        errorMessageWithId = error.message ?: ""
                    }
                    errorMessageWithId
                }
                else -> ""
            }
        override val errorMessageApi: String
            get() = when (error) {
                is BaseError -> {
                    error.message ?: ""
                }
                else -> ""
            }
    }

    data class NetworkException(val throwable: Throwable) : DomainError() {

        val isOffline = throwable is java.net.UnknownHostException
        val isConnectionTimedOut = throwable is java.net.ConnectException
        val isSocketTimedOut = throwable is java.net.SocketTimeoutException
        val isSocketError = throwable is java.net.SocketException

        override val errorCode: String
            get() = when (throwable) {
                is java.net.UnknownHostException -> ERRORCODE_NETWORK_DNS
                is java.net.ConnectException -> ERRORCODE_NETWORK_CONNECTION_TIMEDOUT
                is java.net.SocketTimeoutException -> ERRORCODE_NETWORK_SOCKET_TIMEDOUT
                is java.net.SocketException -> ERRORCODE_NETWORK_SOCKET
                is javax.net.ssl.SSLHandshakeException -> ERRORCODE_NETWORK_SSL_HANDSHAKE
                else -> error("unexpected: $throwable")
            }

        override val errorMessage
            get() = throwable.javaClass.canonicalName ?: ""
        override val errorMessageApi: String
            get() = throwable.javaClass.canonicalName ?: ""

    }

    data class SystemException(val throwable: Throwable) : DomainError() {
        override val errorCode: String
            get() = ERRORCODE_SYSTEM
        override val errorMessage: String
            get() = throwable.message.toString()
        override val errorMessageApi: String
            get() = throwable.message.toString()
    }
}

interface BackendTypedError

data class BaseError(
    @SerializedName("code")
    val code: String? = null,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("name")
    val name: String? = null
) : BackendTypedError

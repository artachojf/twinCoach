package com.example.healthconnect.codelab.data.model.failure

sealed class ResponseFailure {

    object ServerError: ResponseFailure()
    object UnknownError: ResponseFailure()
    object BadRequestError: ResponseFailure()
    object NoInternetError: ResponseFailure()
}
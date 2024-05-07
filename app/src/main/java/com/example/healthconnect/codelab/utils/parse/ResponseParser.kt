package com.example.healthconnect.codelab.utils.parse

import arrow.core.Either
import com.example.healthconnect.codelab.data.model.failure.ResponseFailure
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException

object ResponseParser {

    inline fun <reified T : Any> parseResponse(response: Response<ResponseBody>): Either<ResponseFailure, T> {
        return if (response.isSuccessful)
            Either.Right(Gson().fromJson(response.body()?.string(), T::class.java))
        else if (response.code() in 400..<500)
            Either.Left(ResponseFailure.BadRequestError)
        else
            Either.Left(ResponseFailure.ServerError)
    }

    fun parseError(e: Exception): Either.Left<ResponseFailure> {
        return when (e) {
            is SocketTimeoutException -> Either.Left(ResponseFailure.ServerError)
            is ConnectException -> Either.Left(ResponseFailure.NoInternetError)
            else -> Either.Left(ResponseFailure.UnknownError)
        }
    }

    fun parseEmptyResponse(response: Response<ResponseBody>): Either<ResponseFailure, Unit> {
        return if (response.isSuccessful)
            Either.Right(Unit)
        else if (response.code() in 400..<500)
            Either.Left(ResponseFailure.BadRequestError)
        else
            Either.Left(ResponseFailure.ServerError)
    }
}
package com.example.healthconnect.codelab.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.example.healthconnect.codelab.R
import com.example.healthconnect.codelab.data.model.failure.ResponseFailure

object ViewUtils {

    fun showWarningDialog(context: Context, resId: Int, successCallback: () -> Unit, cancelCallback: () -> Unit) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(resId)
        builder.setPositiveButton(R.string.ok) { dialog, id ->
            successCallback.invoke()
        }
        builder.setNegativeButton(R.string.cancel) { dialog, id ->
            cancelCallback.invoke()
        }
        builder.show()
    }

    fun getErrorStringId(r: ResponseFailure): Int {
        return when (r) {
            is ResponseFailure.UnknownError -> R.string.unknown_error
            is ResponseFailure.ServerError -> R.string.server_error
            is ResponseFailure.BadRequestError -> R.string.bad_request_error
            is ResponseFailure.NoInternetError -> R.string.no_internet_error
        }
    }
}
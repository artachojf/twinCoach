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

fun Int.toTimeString(): String {
    return if (this / 3600 > 0) {
        "${String.format("%02d:%02d:%02d", this / 3600, (this % 3600) / 60, this % 60)} h"
    } else if (this / 60 > 0) {
        "${String.format("%02d:%02d", (this % 3600) / 60, this % 60)} min"
    } else {
        "${this % 60} sec"
    }
}

fun Int.toDistanceString(): String {
    return if (this >= 1000) (this.toDouble() / 1000.0).toString() + " km" else "$this m"
}

fun Int.toHeartRateString(): String {
    return "$this bpm"
}

fun Double.toSpeedString(): String {
    return "${String.format("%02d:%02d", this.toInt(), ((this - this.toInt()) * 60).toInt())} min/km"
}

fun Int.toHeightString(): String {
    return "${this.toDouble() / 100} m"
}

fun Double.toWeigthString(): String {
    return "$this kg"
}
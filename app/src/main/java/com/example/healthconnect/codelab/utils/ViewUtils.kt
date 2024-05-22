package com.example.healthconnect.codelab.utils

import android.content.Context
import android.content.res.ColorStateList
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.healthconnect.codelab.R
import com.example.healthconnect.codelab.data.model.failure.ResponseFailure
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object ViewUtils {

    fun showDialog(context: Context, resId: Int, successCallback: () -> Unit, cancelCallback: () -> Unit) {
        showDialog(context, context.getString(resId), successCallback, cancelCallback)
    }

    fun showDialog(context: Context, message: String, successCallback: () -> Unit, cancelCallback: () -> Unit) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(message)
        builder.setPositiveButton(R.string.ok) { dialog, id ->
            successCallback.invoke()
        }
        builder.setNegativeButton(R.string.cancel) { dialog, id ->
            cancelCallback.invoke()
        }
        builder.show()
    }

    fun showToast(context: Context, resId: Int) {
        showToast(context, context.getString(resId))
    }

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
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

fun LocalDateTime.toFormatString(): String {
    return this.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
}

fun EditText.resetColors() {
    setHintTextColor(context.getColor(R.color.soft_grey))
    backgroundTintList = ColorStateList.valueOf(context.getColor(R.color.grey))
}

fun EditText.onError() {
    setHintTextColor(context.getColor(R.color.soft_red))
    backgroundTintList = ColorStateList.valueOf(context.getColor(R.color.red))
}
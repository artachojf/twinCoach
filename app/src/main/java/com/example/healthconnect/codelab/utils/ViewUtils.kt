package com.example.healthconnect.codelab.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.example.healthconnect.codelab.R

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
}
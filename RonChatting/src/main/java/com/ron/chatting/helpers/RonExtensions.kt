package com.ron.chatting.helpers

import android.R
import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.widget.TextView
import androidx.core.text.HtmlCompat
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Date


internal fun dateTimeFromTimeStamp(timeStamp: String): String {
    return try {
        val sdf = SimpleDateFormat("dd-MMM-yy hh:mm aa")
        val netDate = Date(timeStamp.toLong() * 1000)
        sdf.format(netDate)
    } catch (e: Exception) {
        ""
    }
}

internal fun Context.showSnackBar(message: String?) {
    if (message != null) {
        val snackBar =
            Snackbar.make(
                (this as Activity).findViewById(R.id.content),
                message,
                Snackbar.LENGTH_LONG
            )
        val view = snackBar.view
        val textView = view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.maxLines = 5
        textView.ellipsize = TextUtils.TruncateAt.END
        val text = textView.text.toString()
        textView.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT)
        snackBar.show()
    }
}
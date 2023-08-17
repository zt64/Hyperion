package dev.zt64.hyperion.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

tailrec fun Context.findActivity(): Activity = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> error("Activity not found")
}
package com.hyperion.util

import android.content.res.Resources
import android.icu.text.CompactDecimalFormat
import androidx.core.os.ConfigurationCompat

private val currentLocale = ConfigurationCompat.getLocales(Resources.getSystem().configuration)[0]

fun Number.toCompact(): String =
    CompactDecimalFormat.getInstance(currentLocale, CompactDecimalFormat.CompactStyle.SHORT).format(toInt())
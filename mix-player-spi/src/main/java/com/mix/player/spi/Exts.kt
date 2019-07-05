package com.mix.player.spi

import android.content.ContentResolver
import android.content.Context
import android.content.res.AssetFileDescriptor
import android.net.Uri
import android.text.TextUtils
import java.io.IOException

fun Context.getAssetsFileDescriptor(assetsPath: String?): AssetFileDescriptor? {

    try {
        return if (TextUtils.isEmpty(assetsPath)) null else assets.openFd(assetsPath!!)
    } catch (e: IOException) {
        e.printStackTrace()
    }

    return null
}

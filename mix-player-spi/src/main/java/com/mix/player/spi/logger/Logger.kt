package com.mix.player.spi.logger

import android.util.Log
import com.mix.player.spi.BuildConfig

/**
 * @author qingfei.chain
 */
object Logger {
    fun d(tag: String, message: String) {
        if (!BuildConfig.DEBUG)
            return
        Log.d(tag, message)
    }

    fun w(tag: String, message: String) {
        if (!BuildConfig.DEBUG)
            return
        Log.w(tag, message)
    }

    fun e(tag: String, message: String) {
        if (!BuildConfig.DEBUG)
            return
        Log.e(tag, message)
    }
}


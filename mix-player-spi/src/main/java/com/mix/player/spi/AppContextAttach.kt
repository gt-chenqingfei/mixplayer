package com.mix.player.spi

import android.content.Context
import android.util.Log

/**
 * Author:ricebook
 * Time:2019-06-10  12:36
 */
object AppContextAttach {
    private var sAppContext: Context? = null

    fun attach(context: Context) {
        sAppContext = context.applicationContext
    }

    fun getApplicationContext(): Context {
        if (sAppContext == null) {
            Log.e("AppContextAttach", "app context not init !!!")
            throw RuntimeException("if you need context for using decoder, you must call PlayerLibrary.init(context).")
        }

        return sAppContext!!
    }
}
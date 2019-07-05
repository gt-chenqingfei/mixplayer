package com.mix.player.spi.listener

import android.os.Bundle

interface OnBufferingUpdateListener {
    /**
     * Returns an estimate of the position in the current content window or ad up to which data is
     * buffered, in milliseconds.
     */
    fun onBufferingUpdate(bufferedPosition: Int, bundle: Bundle?)
}
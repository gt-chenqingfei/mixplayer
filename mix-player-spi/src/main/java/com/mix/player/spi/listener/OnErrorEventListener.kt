package com.mix.player.spi.listener

import android.os.Bundle


const val ERROR_EVENT_DATA_PROVIDER_ERROR = -88000

//A error that causes a play to terminate
const val ERROR_EVENT_COMMON = -88011

const val ERROR_EVENT_UNKNOWN = -88012

const val ERROR_EVENT_SERVER_DIED = -88013

const val ERROR_EVENT_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK = -88014

const val ERROR_EVENT_IO = -88015

const val ERROR_EVENT_MALFORMED = -88016

const val ERROR_EVENT_UNSUPPORTED = -88017

const val ERROR_EVENT_TIMED_OUT = -88018

interface OnErrorEventListener {
    fun onErrorEvent(eventCode: Int, bundle: Bundle?)
}
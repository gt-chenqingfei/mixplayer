package com.mix.player.spi.listener

import android.os.Bundle


/**
 * when decoder set data source
 */
const val PLAYER_EVENT_ON_DATA_SOURCE_SET = -99001

/**
 * when surface holder update
 */
const val PLAYER_EVENT_ON_SURFACE_HOLDER_UPDATE = -99002

/**
 * when surface update
 */
const val PLAYER_EVENT_ON_SURFACE_UPDATE = -99003

/**
 * when you call [IPlayer.start]
 */
const val PLAYER_EVENT_ON_START = -99004

/**
 * when you call [IPlayer.pause]
 */
const val PLAYER_EVENT_ON_PAUSE = -99005

/**
 * when you call [IPlayer.resume]
 */
const val PLAYER_EVENT_ON_RESUME = -99006

/**
 * when you call [IPlayer.stop]
 */
const val PLAYER_EVENT_ON_STOP = -99007

/**
 * when you call [IPlayer.reset]
 */
const val PLAYER_EVENT_ON_RESET = -99008

/**
 * when you call [IPlayer.destroy]
 */
const val PLAYER_EVENT_ON_DESTROY = -99009

/**
 * when decoder start buffering stream
 */
const val PLAYER_EVENT_ON_BUFFERING_START = -99010

/**
 * when decoder buffering stream end
 */
const val PLAYER_EVENT_ON_BUFFERING_END = -99011

/**
 * when decoder buffering percentage update
 */
@Deprecated("")
const val PLAYER_EVENT_ON_BUFFERING_UPDATE = -99012

/**
 * when you call [IPlayer.seekTo]
 */
const val PLAYER_EVENT_ON_SEEK_TO = -99013

/**
 * when seek complete
 */
const val PLAYER_EVENT_ON_SEEK_COMPLETE = -99014

/**
 * when player start render video stream
 */
const val PLAYER_EVENT_ON_VIDEO_RENDER_START = -99015

/**
 * when play complete
 */
const val PLAYER_EVENT_ON_PLAY_COMPLETE = -99016

/**
 * on video size change
 */
const val PLAYER_EVENT_ON_VIDEO_SIZE_CHANGE = -99017

/**
 * on decoder prepared
 */
const val PLAYER_EVENT_ON_PREPARED = -99018

/**
 * on player timer counter update [TimerCounterProxy]
 * if timer stopped, you could not receive this event code.
 */
const val PLAYER_EVENT_ON_TIMER_UPDATE = -99019

/**
 * on get video rotation.
 */
const val PLAYER_EVENT_ON_VIDEO_ROTATION_CHANGED = 99020

/**
 * when player start render audio stream
 */
const val PLAYER_EVENT_ON_AUDIO_RENDER_START = -99021

/**
 * when audio decoder start
 */
const val PLAYER_EVENT_ON_AUDIO_DECODER_START = -99022

/**
 * when audio seek rendering start
 */
const val PLAYER_EVENT_ON_AUDIO_SEEK_RENDERING_START = -99023

/**
 * network bandwidth
 */
const val PLAYER_EVENT_ON_NETWORK_BANDWIDTH = -99024

/**
 * bad interleaving
 */
const val PLAYER_EVENT_ON_BAD_INTERLEAVING = -99025

/**
 * not support seek ,may be live.
 */
const val PLAYER_EVENT_ON_NOT_SEEK_ABLE = -99026

/**
 * on meta data update
 */
const val PLAYER_EVENT_ON_METADATA_UPDATE = -99027

/**
 * Failed to handle timed text track properly.
 */
const val PLAYER_EVENT_ON_TIMED_TEXT_ERROR = -99028

/**
 * Subtitle track was not supported by the media framework.
 */
const val PLAYER_EVENT_ON_UNSUPPORTED_SUBTITLE = -99029

/**
 * Reading the subtitle track takes too long.
 */
const val PLAYER_EVENT_ON_SUBTITLE_TIMED_OUT = -99030

/**
 * on play status update
 */
const val PLAYER_EVENT_ON_STATUS_CHANGE = -99031


/**
 * if you set data provider for player, call back this method when provider start load data.
 */
const val PLAYER_EVENT_ON_PROVIDER_DATA_START = -99050

/**
 * call back this method when provider load data success.
 */
const val PLAYER_EVENT_ON_PROVIDER_DATA_SUCCESS = -99051

/**
 * call back this method when provider load data error.
 */
const val PLAYER_EVENT_ON_PROVIDER_DATA_ERROR = -99052

interface OnPlayerEventListener {
    fun onPlayerEvent(eventCode: Int, bundle: Bundle?)
}

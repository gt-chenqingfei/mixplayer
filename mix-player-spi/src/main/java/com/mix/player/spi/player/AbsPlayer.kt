package com.mix.player.spi.player

import android.os.Bundle
import android.util.Log
import com.mix.player.spi.listener.OnBufferingUpdateListener
import com.mix.player.spi.listener.OnErrorEventListener
import com.mix.player.spi.listener.OnPlayerEventListener
import com.mix.player.spi.listener.PLAYER_EVENT_ON_STATUS_CHANGE

/**
 * @author qingfei.chain
 */
abstract class AbsPlayer : IPlayer {
    protected var mCurrentState = STATE_IDLE

    private var mOnPlayerEventListener: OnPlayerEventListener? = null
    private var mOnErrorEventListener: OnErrorEventListener? = null
    private var mOnBufferingListener: OnBufferingUpdateListener? = null

    private var mBufferPercentage: Int = 0

    override fun option(code: Int, bundle: Bundle) {
        //nothing to do
    }

    override fun setOnBufferingUpdateListener(onBufferingListener: OnBufferingUpdateListener) {
        this.mOnBufferingListener = onBufferingListener
    }

    override fun setOnErrorEventListener(onErrorEventListener: OnErrorEventListener) {
        mOnErrorEventListener = onErrorEventListener
    }

    override fun setOnPlayerEventListener(onPlayerEventListener: OnPlayerEventListener) {
        mOnPlayerEventListener = onPlayerEventListener
    }

    override fun getBufferPercentage(): Int {
        return mBufferPercentage
    }

    protected fun handleBufferingUpdate(bufferedPosition: Int, bundle: Bundle?) {
        mBufferPercentage = bufferedPosition
        mOnBufferingListener?.onBufferingUpdate(bufferedPosition, bundle)
    }

    protected fun handleErrorEvent(eventCode: Int, bundle: Bundle?) {
        Log.d("AbsPlayer", "handleErrorEvent eventCode:$eventCode")
        mOnErrorEventListener?.onErrorEvent(eventCode, bundle)
    }

    protected fun handlePlayerEvent(eventCode: Int, bundle: Bundle?) {
        Log.d("AbsPlayer", "handlePlayerEvent eventCode:$eventCode,currentStatus:$mCurrentState")
        mOnPlayerEventListener?.onPlayerEvent(eventCode, bundle)
    }

    protected fun changeStatus(status: Int) {
        mCurrentState = status
        Log.d("AbsPlayer", "changeStatus currentState:$mCurrentState")
        handlePlayerEvent(PLAYER_EVENT_ON_STATUS_CHANGE, null)
    }

    fun handleException(e: Exception?) {
        e?.printStackTrace()
        Log.d("AbsPlayer", "handleException e:${e.toString()}")
        reset()
    }
}
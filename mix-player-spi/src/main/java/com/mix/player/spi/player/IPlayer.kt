package com.mix.player.spi.player

import android.os.Bundle
import android.view.Surface
import android.view.SurfaceHolder
import com.mix.player.spi.entity.DataSource
import com.mix.player.spi.listener.OnBufferingUpdateListener
import com.mix.player.spi.listener.OnErrorEventListener
import com.mix.player.spi.listener.OnPlayerEventListener

/**
 * The player has been invoke release method
 */
const val STATE_END = -2
/**
 * The player play error
 */
const val STATE_ERROR = -1
/**
 * The player does not have any media to play.
 */
const val STATE_IDLE = 0
/**
 * When the player invoke #IPlayer.setDataSource() can  transition to  initialized status
 */
const val STATE_INITIALIZED = 1
/**
 *  When the media  source is ready for playback.
 */
const val STATE_PREPARED = 2
/**
 * When invoke #IPlayer.start() and #IPlayer.resume() can transition to the status
 */
const val STATE_STARTED = 3
/**
 * When invoke #IPlayer.pause{} can transition to the status
 */
const val STATE_PAUSED = 4
/**
 * When invoke #IPlayer.stop{} can transition to the status
 */
const val STATE_STOPPED = 5
/**
 * The end of a media source  has been reached during playback
 */
const val STATE_PLAYBACK_COMPLETE = 6

interface IPlayer {


    /**
     * with this method, you can send some params for player init or switch some setting.
     * such as some configuration option (use mediacodec or timeout or reconnect and so on) for decoder init.
     * @param code the code value custom yourself.
     * @param bundle deliver some data if you need.
     */
    fun option(code: Int, bundle: Bundle)

    fun setDataSource(dataSource: DataSource)
    /**
     * Sets the {@link SurfaceHolder} that holds the {@link Surface} onto which video will be
     * rendered. The player will track the lifecycle of the surface automatically.
     *
     * @param surfaceHolder The surface holder.
     */
    fun setDisplay(surfaceHolder: SurfaceHolder)

    /**
     * Sets the {@link Surface} onto which video will be rendered. The caller is responsible for
     * tracking the lifecycle of the surface, and must clear the surface by calling {@code
     * setVideoSurface(null)} if the surface is destroyed.
     *
     * <p>If the surface is held by a {@link SurfaceView}, {@link TextureView} or {@link
     * SurfaceHolder} then it's recommended to use {@link #setVideoSurfaceView(SurfaceView)}, {@link
     * #setVideoTextureView(TextureView)} or {@link #setVideoSurfaceHolder(SurfaceHolder)} rather
     * than this method, since passing the holder allows the player to track the lifecycle of the
     * surface automatically.
     *
     * @param surface The {@link Surface}.
     */
    fun setSurface(surface: Surface)

    /**
     * Sets the audio volume, with 0 being silence and 1 being unity gain.
     *
     * @param audioVolume The audio volume.
     */
    fun setVolume(left: Float, right: Float)

    fun setSpeed(speed: Float)

    fun setOnPlayerEventListener(onPlayerEventListener: OnPlayerEventListener)
    fun setOnErrorEventListener(onErrorEventListener: OnErrorEventListener)
    fun setOnBufferingUpdateListener(onBufferingListener: OnBufferingUpdateListener)
    /**
     * Returns an estimate of the percentage in the current content window or ad up to which data is
     * buffered, or 0 if no estimate is available.
     */
    fun getBufferPercentage(): Int

    fun isPlaying(): Boolean
    /** Returns the playback position in the current content window or ad, in milliseconds. */
    fun getCurrentPosition(): Int

    /**
     * Returns the duration of the current content window or ad in milliseconds, or {@link
     * C#TIME_UNSET} if the duration is not known.
     */
    fun getDuration(): Int

    /** Returns the audio session identifier, or {@link C#AUDIO_SESSION_ID_UNSET} if not set. */
    fun getAudioSessionId(): Int

    fun getVideoWidth(): Int
    fun getVideoHeight(): Int
    fun getState(): Int

    fun start()

    fun start(msc: Int)
    /**
     * If the player is already in the start state and prepared state {@link #STATE_STARTED,STATE_PREPARED}
     * then this method can be used to resume
     */
    fun pause()

    /**
     * If the player is already in the pause state {@link #STATE_PAUSED} then this method can be used to resume
     */
    fun resume()

    /**
     * Seeks to a position specified in milliseconds in the current window.
     *
     * @param msc The seek position in the current window, or {@link C#TIME_UNSET} to seek to
     *     the window's default position.
     */
    fun seekTo(msc: Int)

    /**
     * Calling this method will cause the playback state to transition to {@link #STATE_STOPPED}. The
     * player instance can still be used
     */
    fun stop()

    /**
     * Calling this method will cause the playback state to transition to {@link #STATE_IDLE}. The
     * player instance can still be used, and {@link #release()} must still be called on the player if
     * it's no longer required.
     */
    fun reset()

    /**
     * Releases the player. This method must be called when the player is no longer required. The
     * player must not be used after calling this method.
     */
    fun release()
}
package com.mix.player.spi.player

import com.mix.player.spi.entity.DataSource
import com.mix.player.spi.render.AspectRatio
import com.mix.player.spi.render.IRender

/**
 * @author qingfei.chain
 */
interface IVideoView {
    fun setDataSource(url: String)
    fun setDataSource(dataSource: DataSource)

    fun setRenderType(renderType: Int)
    fun setAspectRatio(aspectRatio: AspectRatio)
    fun setVolume(left: Float, right: Float)
    fun setSpeed(speed: Float)

    fun getRender(): IRender?

    fun isPlaying(): Boolean
    fun getCurrentPosition(): Int
    fun getDuration(): Int
    fun getAudioSessionId(): Int
    fun getBufferPercentage(): Int
    /**
     * See also
     * {@link IPlayer#STATE_END}
     * {@link IPlayer#STATE_ERROR}
     * {@link IPlayer#STATE_IDLE}
     * {@link IPlayer#STATE_INITIALIZED}
     * {@link IPlayer#STATE_PREPARED}
     * {@link IPlayer#STATE_STARTED}
     * {@link IPlayer#STATE_PAUSED}
     * {@link IPlayer#STATE_STOPPED}
     * {@link IPlayer#STATE_PLAYBACK_COMPLETE}
     */
    fun getState(): Int

    fun start()
    fun start(msc: Int)
    fun pause()
    fun resume()
    fun seekTo(msc: Int)
    fun stop()
    fun stopPlayback()

}
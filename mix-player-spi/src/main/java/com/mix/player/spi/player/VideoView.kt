package com.mix.player.spi.player

import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.widget.FrameLayout
import com.mix.player.spi.entity.DataSource
import com.mix.player.spi.listener.*
import com.mix.player.spi.render.*

/**
 * @author:qingfei.chain
 */
class VideoView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), IRender.IRenderCallback, OnPlayerEventListener,
    OnBufferingUpdateListener, OnErrorEventListener, IVideoView {


    internal val TAG = "VideoView"

    private var mPlayer: AbsPlayer? = null
    private var mRenderView: IRender? = null
    private var mRenderHolder: IRender.IRenderHolder? = null
    private var mVideoWidth: Int = 0
    private var mVideoHeight: Int = 0
    private var mVideoSarNum: Int = 0
    private var mVideoSarDen: Int = 0
    private var mVideoRotation: Int = 0
    private var mRenderType: Int = RENDER_TYPE_TEXTURE
    private var isBuffering: Boolean = false
    private var mAspectRatio: AspectRatio? = null
    private var mDataSource: DataSource? = null

    init {
        mPlayer = createPlayer()
        mPlayer?.apply {
            setOnPlayerEventListener(this@VideoView)
            setOnBufferingUpdateListener(this@VideoView)
            setOnErrorEventListener(this@VideoView)
        }
    }

    override fun setDataSource(dataSource: DataSource) {
        requestAudioFocus()
        initRenderView()
        mDataSource = dataSource
        mPlayer?.setDataSource(dataSource)
    }

    override fun setDataSource(url: String) {
        val dataSource = DataSource()
        dataSource.data = url
        setDataSource(dataSource)
    }

    override fun setRenderType(renderType: Int) {
        mRenderType = renderType
    }

    override fun setAspectRatio(aspectRatio: AspectRatio) {
        mAspectRatio = aspectRatio
        mRenderView?.updateAspectRatio(aspectRatio)
    }

    override fun setVolume(left: Float, right: Float) {
        mPlayer?.setVolume(left, right)
    }

    override fun setSpeed(speed: Float) {
        mPlayer?.setSpeed(speed)
    }

    override fun getRender(): IRender? {
        return mRenderView
    }

    override fun isPlaying(): Boolean {
        return mPlayer?.isPlaying() ?: false
    }

    override fun getCurrentPosition(): Int {
        return mPlayer?.getCurrentPosition() ?: 0
    }

    override fun getDuration(): Int {
        return mPlayer?.getDuration() ?: 0
    }

    override fun getAudioSessionId(): Int {
        return mPlayer?.getAudioSessionId() ?: 0
    }

    override fun getBufferPercentage(): Int {
        return mPlayer?.getBufferPercentage() ?: 0
    }

    override fun getState(): Int {
        return mPlayer?.getState() ?: STATE_IDLE
    }

    override fun start() {
        mPlayer?.start()
    }

    override fun start(msc: Int) {
        mPlayer?.start(msc)
    }

    override fun pause() {
        mPlayer?.pause()
    }

    override fun resume() {
        mPlayer?.resume()
    }

    override fun seekTo(msc: Int) {
        mPlayer?.seekTo(msc)
    }

    override fun stop() {
        mPlayer?.stop()
    }

    override fun stopPlayback() {
        Log.e(TAG, "stopPlayback release.")
        releaseAudioFocus()
        mPlayer?.release()
        mRenderHolder = null
        releaseRender()
    }


    private fun initRenderView() {

        if (mRenderView == null) {
            mRenderView = createRender()
        } else if (mRenderView?.getRenderType() != mRenderType) {
            releaseRender()
            mRenderView = createRender()
        }

    }

    private fun createRender(): IRender? {
        when (mRenderType) {

            RENDER_TYPE_SURFACE -> {
                mRenderView = MixSurfaceView(context)
            }
            RENDER_TYPE_TEXTURE -> {
                mRenderView = MixTextureView(context)
            }
        }
        mRenderView?.apply {
            addView(getRenderView())
            setRenderCallback(this@VideoView)
        }

        return mRenderView
    }

    override fun onBufferingUpdate(bufferedPosition: Int, bundle: Bundle?) {

    }

    override fun onErrorEvent(eventCode: Int, bundle: Bundle?) {
        Log.d(TAG, "onErrorEvent $eventCode")
    }

    override fun onPlayerEvent(eventCode: Int, bundle: Bundle?) {
        when (eventCode) {
            //when get video size , need update render for measure.
            PLAYER_EVENT_ON_VIDEO_SIZE_CHANGE -> if (bundle != null) {
                mVideoWidth = bundle.getInt(BundlePool.INT_ARG1)
                mVideoHeight = bundle.getInt(BundlePool.INT_ARG2)
                mVideoSarNum = bundle.getInt(BundlePool.INT_ARG3)
                mVideoSarDen = bundle.getInt(BundlePool.INT_ARG4)
                Log.d(
                    TAG, "onVideoSizeChange : videoWidth = " + mVideoWidth
                            + ", videoHeight = " + mVideoHeight
                            + ", videoSarNum = " + mVideoSarNum
                            + ", videoSarDen = " + mVideoSarDen
                )
                mRenderView?.apply {
                    //update video size
                    updateVideoSize(mVideoWidth, mVideoHeight)
                    //update video sarNum,sarDen
                    setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen)
                }
            }
            //when get video rotation, need update render rotation.
            PLAYER_EVENT_ON_VIDEO_ROTATION_CHANGED -> if (bundle != null) {
                //if rotation change need update render.
                mVideoRotation = bundle.getInt(BundlePool.INT_DATA)
                Log.d(TAG, "onVideoRotationChange : videoRotation = $mVideoRotation")
                mRenderView?.setVideoRotation(mVideoRotation)
            }
            //when prepared bind surface.
            PLAYER_EVENT_ON_PREPARED -> {
                if (bundle != null) {
                    mVideoWidth = bundle.getInt(BundlePool.INT_ARG1)
                    mVideoHeight = bundle.getInt(BundlePool.INT_ARG2)
                    mRenderView?.updateVideoSize(mVideoWidth, mVideoHeight)
                }
                bindRenderHolder(mRenderHolder)
            }
            PLAYER_EVENT_ON_BUFFERING_START -> isBuffering = true
            PLAYER_EVENT_ON_BUFFERING_END -> isBuffering = false
        }
    }

    override fun onSurfaceCreated(renderHolder: IRender.IRenderHolder, width: Int, height: Int) {
        mRenderHolder = renderHolder
        bindRenderHolder(mRenderHolder)
    }

    override fun onSurfaceChanged(renderHolder: IRender.IRenderHolder, format: Int, width: Int, height: Int) {
    }

    override fun onSurfaceDestroy(renderHolder: IRender.IRenderHolder) {
        mRenderHolder = null
    }

    private fun bindRenderHolder(renderHolder: IRender.IRenderHolder?) {
        renderHolder?.bindPlayer(mPlayer)
    }

    private fun createPlayer(): AbsPlayer? {
        return DefaultMediaPlayer()
    }

    /**
     * release render
     */
    private fun releaseRender() {
        mRenderView?.release()
        mRenderView = null
    }

    private fun requestAudioFocus() {
        Log.d(TAG, "requestAudioFocus")
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
    }

    private fun releaseAudioFocus() {
        Log.d(TAG, "releaseAudioFocus")
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        am.abandonAudioFocus(null)
    }
}
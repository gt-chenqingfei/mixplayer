package com.mix.player.spi.player

import android.media.AudioManager
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import com.mix.player.spi.AppContextAttach
import com.mix.player.spi.entity.DataSource
import com.mix.player.spi.getAssetsFileDescriptor
import com.mix.player.spi.listener.*

/**
 * @author:qingfei.chain
 */
class DefaultMediaPlayer : AbsPlayer() {

    private val TAG = "DefaultMediaPlayer"
    private val MEDIA_INFO_NETWORK_BANDWIDTH = 703

    private var mMediaPlayer: MediaPlayer? = null
    private var mTargetState: Int = -1
    private var mBandWidth: Long = 0
    private var mVideoWidth: Int = 0
    private var mVideoHeight: Int = 0
    private var startSeekPos: Int = 0

    private lateinit var mDataSource: DataSource


    override fun setDataSource(dataSource: DataSource) {
        try {
            Log.d(TAG, "setDataSource data:${dataSource.toString()}")

            if (mMediaPlayer == null) {
                mMediaPlayer = MediaPlayer()
            } else {
                stop()
                reset()
                resetListener()
            }
            mMediaPlayer?.let {
                it.setOnPreparedListener(mPreparedListener)
                it.setOnVideoSizeChangedListener(mSizeChangedListener)
                it.setOnCompletionListener(mCompletionListener)
                it.setOnErrorListener(mErrorListener)
                it.setOnInfoListener(mInfoListener)
                it.setOnSeekCompleteListener(mOnSeekCompleteListener)
                it.setOnBufferingUpdateListener(mBufferingUpdateListener)

                changeStatus(STATE_INITIALIZED)

                this.mDataSource = dataSource
                val applicationContext = AppContextAttach.getApplicationContext()
                val data = dataSource.data
                val uri = dataSource.uri
                val assetsPath = dataSource.assetsPath
                val headers = dataSource.extra
                val rawId = dataSource.rawId
                if (data != null) {
                    it.setDataSource(data)
                } else if (uri != null) {
                    if (headers == null)
                        it.setDataSource(applicationContext, uri)
                    else
                        it.setDataSource(applicationContext, uri, headers)
                } else if (!TextUtils.isEmpty(assetsPath)) {
                    //assets play. use FileDescriptor play
                    val fileDescriptor = applicationContext.getAssetsFileDescriptor(dataSource.assetsPath)
                    fileDescriptor?.let { afd ->
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            it.setDataSource(afd)
                        } else {
                            it.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength())
                        }
                    }
                } else if (rawId > 0) {
                    val rawUri = DataSource.buildRawPath(applicationContext.getPackageName(), rawId)
                    it.setDataSource(applicationContext, rawUri)
                }

                it.setAudioStreamType(AudioManager.STREAM_MUSIC)
                it.setScreenOnWhilePlaying(true)
                it.prepareAsync()

                val bundle = BundlePool.obtain()
                bundle.putSerializable(BundlePool.SERIALIZABLE_DATA, dataSource)
                handlePlayerEvent(PLAYER_EVENT_ON_DATA_SOURCE_SET, bundle)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            changeStatus(STATE_ERROR)
            mTargetState = STATE_ERROR
        }
    }

    override fun setDisplay(surfaceHolder: SurfaceHolder) {
        try {
            Log.d(TAG, "setDisplay")
            mMediaPlayer?.setDisplay(surfaceHolder)
            handlePlayerEvent(PLAYER_EVENT_ON_SURFACE_HOLDER_UPDATE, null)
        } catch (e: Exception) {
            handleException(e)
        }

    }

    override fun setSurface(surface: Surface) {
        try {
            Log.d(TAG, "setSurface")
            mMediaPlayer?.setSurface(surface)
            handlePlayerEvent(PLAYER_EVENT_ON_SURFACE_UPDATE, null)
        } catch (e: Exception) {
            handleException(e)
        }
    }

    override fun setVolume(left: Float, right: Float) {
        mMediaPlayer?.setVolume(left, right)
    }

    override fun setSpeed(speed: Float) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val playbackParams = mMediaPlayer?.playbackParams
                playbackParams?.speed = speed
                /**
                 * Sets playback rate using [PlaybackParams]. The object sets its internal
                 * PlaybackParams to the input, except that the object remembers previous speed
                 * when input speed is zero. This allows the object to resume at previous speed
                 * when start() is called. Calling it before the object is prepared does not change
                 * the object state. After the object is prepared, calling it with zero speed is
                 * equivalent to calling pause(). After the object is prepared, calling it with
                 * non-zero speed is equivalent to calling start().
                 */
                mMediaPlayer?.playbackParams = playbackParams
                if (speed <= 0) {
                    pause()
                } else if (speed > 0 && getState() == STATE_PAUSED) {
                    resume()
                }
            } else {
                Log.e(TAG, "not support play speed setting.")
            }
        } catch (e: Exception) {
            Log.e(
                TAG,
                "IllegalStateException， if the internal player engine has not been initialized " + "or has been released."
            )
        }

    }

    override fun isPlaying(): Boolean {
        return if (getState() != STATE_ERROR) {
            mMediaPlayer?.isPlaying ?: false
        } else false
    }

    override fun getCurrentPosition(): Int {
        return if ((getState() == STATE_PREPARED || getState() == STATE_STARTED
                    || getState() == STATE_PAUSED || getState() == STATE_PLAYBACK_COMPLETE)
        ) {
            mMediaPlayer?.currentPosition ?: 0
        } else 0
    }

    override fun getDuration(): Int {
        return if (
            getState() != STATE_ERROR && getState() != STATE_INITIALIZED && getState() != STATE_IDLE
        ) {
            mMediaPlayer?.duration ?: 0
        } else 0
    }

    override fun getAudioSessionId(): Int {
        return mMediaPlayer?.audioSessionId ?: 0
    }

    override fun getVideoWidth(): Int {
        return mMediaPlayer?.getVideoWidth() ?: 0
    }

    override fun getVideoHeight(): Int {
        return mMediaPlayer?.getVideoHeight() ?: 0
    }

    override fun getState(): Int {
        return mCurrentState
    }

    override fun start() {
        try {
            if ((getState() == STATE_PREPARED
                        || getState() == STATE_PAUSED
                        || getState() == STATE_PLAYBACK_COMPLETE)
            ) {
                mMediaPlayer?.start()
                changeStatus(STATE_STARTED)
                handlePlayerEvent(PLAYER_EVENT_ON_START, null)
            }
        } catch (e: Exception) {
            handleException(e)
        }

        mTargetState = STATE_STARTED
    }

    override fun start(msc: Int) {
        if (msc > 0) {
            startSeekPos = msc
        }
        start()

    }

    override fun pause() {
        try {
            val state = getState()
            if (
                state != STATE_END
                && state != STATE_ERROR
                && state != STATE_IDLE
                && state != STATE_INITIALIZED
                && state != STATE_PAUSED
                && state != STATE_STOPPED
            ) {
                mMediaPlayer?.pause()
                changeStatus(STATE_PAUSED)
                handlePlayerEvent(PLAYER_EVENT_ON_PAUSE, null)
            }
        } catch (e: Exception) {
            handleException(e)
        }

        mTargetState = STATE_PAUSED
    }

    override fun resume() {
        try {
            if (getState() == STATE_PAUSED) {
                mMediaPlayer?.start()
                changeStatus(STATE_STARTED)
                handlePlayerEvent(PLAYER_EVENT_ON_RESUME, null)
            }
        } catch (e: Exception) {
            handleException(e)
        }

        mTargetState = STATE_STARTED
    }

    override fun seekTo(msc: Int) {
        if ((getState() == STATE_PREPARED
                    || getState() == STATE_STARTED
                    || getState() == STATE_PAUSED
                    || getState() == STATE_PLAYBACK_COMPLETE)
        ) {
            mMediaPlayer?.seekTo(msc)
            val bundle = BundlePool.obtain()
            bundle.putInt(BundlePool.INT_DATA, msc)
            handlePlayerEvent(PLAYER_EVENT_ON_SEEK_TO, bundle)
        }
    }

    override fun stop() {
        if ((getState() == STATE_PREPARED
                    || getState() == STATE_STARTED
                    || getState() == STATE_PAUSED
                    || getState() == STATE_PLAYBACK_COMPLETE)
        ) {
            mMediaPlayer?.stop()
            changeStatus(STATE_STOPPED)
            handlePlayerEvent(PLAYER_EVENT_ON_STOP, null)
        }
        mTargetState = STATE_STOPPED
    }

    override fun reset() {

        mMediaPlayer?.reset()
        changeStatus(STATE_IDLE)
        handlePlayerEvent(PLAYER_EVENT_ON_RESET, null)

        mTargetState = STATE_IDLE
    }

    override fun release() {
        changeStatus(STATE_END)
        resetListener()
        mMediaPlayer?.release()
        handlePlayerEvent(PLAYER_EVENT_ON_DESTROY, null)
    }

    private fun resetListener() {
        mMediaPlayer?.let {
            it.setOnPreparedListener(null)
            it.setOnVideoSizeChangedListener(null)
            it.setOnCompletionListener(null)
            it.setOnErrorListener(null)
            it.setOnInfoListener(null)
            it.setOnBufferingUpdateListener(null)
        }

    }

    internal var mPreparedListener: MediaPlayer.OnPreparedListener = MediaPlayer.OnPreparedListener { mp ->
        Log.d(TAG, "onPrepared...")
        changeStatus(STATE_PREPARED)

        mVideoWidth = mp.videoWidth
        mVideoHeight = mp.videoHeight

        val bundle = BundlePool.obtain()
        bundle.putInt(BundlePool.INT_ARG1, mVideoWidth)
        bundle.putInt(BundlePool.INT_ARG2, mVideoHeight)

        handlePlayerEvent(PLAYER_EVENT_ON_PREPARED, bundle)

        val seekToPosition = startSeekPos  // mSeekWhenPrepared may be changed after seekTo() call
        if (seekToPosition != 0) {
            //seek to start position
            mMediaPlayer?.seekTo(seekToPosition)
            startSeekPos = 0
        }

        // We don't know the video size yet, but should start anyway.
        // The video size might be reported to us later.
        Log.d(TAG, "mTargetState = $mTargetState")
        if (mTargetState == STATE_STARTED) {
            start()
        } else if (mTargetState == STATE_PAUSED) {
            pause()
        } else if (mTargetState == STATE_STOPPED || mTargetState == STATE_IDLE) {
            reset()
        }
        attachTimedTextSource()
    }

    private val mSizeChangedListener: MediaPlayer.OnVideoSizeChangedListener =
        MediaPlayer.OnVideoSizeChangedListener { mp, width, height ->
            mVideoWidth = mp.videoWidth
            mVideoHeight = mp.videoHeight
            val bundle = BundlePool.obtain()
            bundle.putInt(BundlePool.INT_ARG1, mVideoWidth)
            bundle.putInt(BundlePool.INT_ARG2, mVideoHeight)
            handlePlayerEvent(PLAYER_EVENT_ON_VIDEO_SIZE_CHANGE, bundle)
        }

    private val mCompletionListener = MediaPlayer.OnCompletionListener {
        changeStatus(STATE_PLAYBACK_COMPLETE)
        mTargetState = STATE_PLAYBACK_COMPLETE
        handlePlayerEvent(PLAYER_EVENT_ON_PLAY_COMPLETE, null)
    }

    private val mInfoListener = MediaPlayer.OnInfoListener { mp, arg1, arg2 ->
        when (arg1) {
            MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING -> Log.d(TAG, "MEDIA_INFO_VIDEO_TRACK_LAGGING:")
            MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START -> {
                Log.d(TAG, "MEDIA_INFO_VIDEO_RENDERING_START")
                startSeekPos = 0
                handlePlayerEvent(PLAYER_EVENT_ON_VIDEO_RENDER_START, null)
            }
            MediaPlayer.MEDIA_INFO_BUFFERING_START -> {
                Log.d(TAG, "MEDIA_INFO_BUFFERING_START:$arg2")
                val bundle = BundlePool.obtain()
                bundle.putLong(BundlePool.LONG_DATA, mBandWidth)
                handlePlayerEvent(PLAYER_EVENT_ON_BUFFERING_START, bundle)
            }
            MediaPlayer.MEDIA_INFO_BUFFERING_END -> {
                Log.d(TAG, "MEDIA_INFO_BUFFERING_END:$arg2")
                val bundle1 = BundlePool.obtain()
                bundle1.putLong(BundlePool.LONG_DATA, mBandWidth)
                handlePlayerEvent(PLAYER_EVENT_ON_BUFFERING_END, bundle1)
            }
            MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING -> {
                Log.d(TAG, "MEDIA_INFO_BAD_INTERLEAVING:")
                handlePlayerEvent(PLAYER_EVENT_ON_BAD_INTERLEAVING, null)
            }
            MediaPlayer.MEDIA_INFO_NOT_SEEKABLE -> {
                Log.d(TAG, "MEDIA_INFO_NOT_SEEKABLE:")
                handlePlayerEvent(PLAYER_EVENT_ON_NOT_SEEK_ABLE, null)
            }
            MediaPlayer.MEDIA_INFO_METADATA_UPDATE -> {
                Log.d(TAG, "MEDIA_INFO_METADATA_UPDATE:")
                handlePlayerEvent(PLAYER_EVENT_ON_METADATA_UPDATE, null)
            }
            MediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE -> {
                Log.d(TAG, "MEDIA_INFO_UNSUPPORTED_SUBTITLE:")
                handlePlayerEvent(PLAYER_EVENT_ON_UNSUPPORTED_SUBTITLE, null)
            }
            MediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT -> {
                Log.d(TAG, "MEDIA_INFO_SUBTITLE_TIMED_OUT:")
                handlePlayerEvent(PLAYER_EVENT_ON_SUBTITLE_TIMED_OUT, null)
            }
            MEDIA_INFO_NETWORK_BANDWIDTH -> {
                Log.d(TAG, "band_width : $arg2")
                mBandWidth = (arg2 * 1000).toLong()
            }
        }
        true
    }

    private val mOnSeekCompleteListener = MediaPlayer.OnSeekCompleteListener {
        Log.d(TAG, "EVENT_CODE_SEEK_COMPLETE")
        handlePlayerEvent(PLAYER_EVENT_ON_SEEK_COMPLETE, null)
    }

    private val mErrorListener = MediaPlayer.OnErrorListener { mp, framework_err, impl_err ->
        Log.d(TAG, "Error: $framework_err,$impl_err")
        changeStatus(STATE_ERROR)
        mTargetState = STATE_ERROR

        var eventCode = ERROR_EVENT_COMMON

        when (framework_err) {
            MediaPlayer.MEDIA_ERROR_IO -> eventCode = ERROR_EVENT_IO
            MediaPlayer.MEDIA_ERROR_MALFORMED -> eventCode = ERROR_EVENT_MALFORMED
            MediaPlayer.MEDIA_ERROR_TIMED_OUT -> eventCode = ERROR_EVENT_TIMED_OUT
            MediaPlayer.MEDIA_ERROR_UNKNOWN -> eventCode = ERROR_EVENT_UNKNOWN
            MediaPlayer.MEDIA_ERROR_UNSUPPORTED -> eventCode = ERROR_EVENT_UNSUPPORTED
            MediaPlayer.MEDIA_ERROR_SERVER_DIED -> eventCode = ERROR_EVENT_SERVER_DIED
            MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK -> eventCode =
                ERROR_EVENT_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK
        }

        /* If an error handler has been supplied, use it and finish. */
        val bundle = BundlePool.obtain()
        handleErrorEvent(eventCode, bundle)
        true
    }

    private val mBufferingUpdateListener =
        MediaPlayer.OnBufferingUpdateListener { mp, percent -> handleBufferingUpdate(percent, null) }


    private fun attachTimedTextSource() {
        val timedTextSource = mDataSource.timedTextSource ?: return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mMediaPlayer?.addTimedTextSource(timedTextSource.path, timedTextSource.mimeType)
                val trackInfos = mMediaPlayer?.trackInfo
                if (trackInfos != null) {
                    for ((index, trackInfo) in trackInfos.withIndex()) {
                        if (trackInfo.trackType == MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT) {
                            mMediaPlayer?.selectTrack(index)
                            break
                        }

                    }
                }

            } else {
                Log.e(TAG, "not support setting timed text source !")
            }
        } catch (e: Exception) {
            Log.e(TAG, "addTimedTextSource error !")
            e.printStackTrace()
        }

    }
}


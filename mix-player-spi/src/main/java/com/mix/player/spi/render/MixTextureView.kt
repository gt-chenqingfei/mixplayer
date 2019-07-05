package com.mix.player.spi.render

import android.content.Context
import android.graphics.SurfaceTexture
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.view.View

/**
 * @author:qingfei.chain
 */
class MixTextureView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : TextureView(context, attrs, defStyleAttr), IRender {
    internal val TAG = "MixTextureView"
    private var mRenderCallback: IRender.IRenderCallback? = null
    private val mRenderMeasure: RenderMeasure
    private var mSurfaceTexture: SurfaceTexture? = null
    private var mTakeOverSurfaceTexture: Boolean = false
    var mSurface: Surface? = null
    private var isReleased: Boolean = false

    init {
        mRenderMeasure = RenderMeasure()
        surfaceTextureListener = MixSurfaceTextureListener(this)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        mRenderMeasure.doMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(mRenderMeasure.getMeasureWidth(), mRenderMeasure.getMeasureHeight())
    }

    override fun setRenderCallback(renderCallback: IRender.IRenderCallback) {
        this.mRenderCallback = renderCallback
    }

    override fun setVideoRotation(degree: Int) {
        mRenderMeasure.setVideoRotation(degree)
        rotation = degree.toFloat()
    }

    override fun setVideoSampleAspectRatio(videoSarNum: Int, videoSarDen: Int) {
        if (videoSarNum > 0 && videoSarDen > 0) {
            mRenderMeasure.setVideoSampleAspectRatio(videoSarNum, videoSarDen)
            requestLayout()
        }
    }

    override fun updateAspectRatio(aspectRatio: AspectRatio) {
        mRenderMeasure.setAspectRatio(aspectRatio)
        requestLayout()
    }

    override fun updateVideoSize(videoWidth: Int, videoHeight: Int) {
        Log.d(TAG, "onUpdateVideoSize : videoWidth = $videoWidth ,videoHeight = $videoHeight")
        mRenderMeasure.setVideoSize(videoWidth, videoHeight)
        requestLayout()
    }

    override fun getRenderView(): View {
        return this
    }

    override fun getRenderType(): Int {
        return RENDER_TYPE_TEXTURE
    }

    override fun release() {
        if (mSurfaceTexture != null) {
            mSurfaceTexture?.release()
            mSurfaceTexture = null
        }
        if (mSurface != null) {
            mSurface?.release()
            mSurface = null
        }
        surfaceTextureListener = null
        isReleased = true
    }

    override fun isReleased(): Boolean {
        return isReleased
    }

    internal fun getOwnSurfaceTexture(): SurfaceTexture? {
        return mSurfaceTexture
    }

    fun isTakeOverSurfaceTexture(): Boolean {
        return mTakeOverSurfaceTexture
    }


    private inner class MixSurfaceTextureListener(val textureView: MixTextureView) : SurfaceTextureListener {

        override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
            Log.d(TAG, "onSurfaceTextureAvailable : width = $width height = $height")
            mRenderCallback?.onSurfaceCreated(
                TextureRenderHolder(textureView, surface), width, height
            )
        }

        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
            Log.d(TAG, "onSurfaceTextureSizeChanged : width = $width height = $height")

            mRenderCallback?.onSurfaceChanged(
                TextureRenderHolder(textureView, surface), 0, width, height
            )

        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
            Log.d(TAG, "onSurfaceTextureDestroyed")

            mRenderCallback?.onSurfaceDestroy(
                TextureRenderHolder(textureView, surface)
            )

            if (mTakeOverSurfaceTexture) {
                mSurfaceTexture = surface
            }
            return if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                false
            } else !mTakeOverSurfaceTexture
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {

        }
    }


}
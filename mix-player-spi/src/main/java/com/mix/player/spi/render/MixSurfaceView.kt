package com.mix.player.spi.render

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View

/**
 * @author qingfei.chain
 */
class MixSurfaceView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : SurfaceView(context, attrs, defStyleAttr), IRender {


    internal val TAG = "MixSurfaceView"

    private var mRenderCallback: IRender.IRenderCallback? = null
    private val mRenderMeasure: RenderMeasure
    private var isReleased: Boolean = false
    private var holderCallback: SurfaceHolder.Callback? = null

    init {
        mRenderMeasure = RenderMeasure()
        holderCallback = SurfaceHolderCallback()
        holder.addCallback(holderCallback)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mRenderMeasure.doMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(mRenderMeasure.getMeasureWidth(), mRenderMeasure.getMeasureHeight())
    }

    override fun setRenderCallback(renderCallback: IRender.IRenderCallback) {
        this.mRenderCallback = renderCallback
    }

    override fun setVideoRotation(degree: Int) {
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
        mRenderMeasure.setVideoSize(videoWidth, videoHeight)
        fixedSize(videoWidth, videoHeight)
        requestLayout()
    }

    override fun getRenderView(): View {
        return this
    }

    override fun getRenderType(): Int {
        return RENDER_TYPE_SURFACE
    }

    override fun release() {
        holder.removeCallback(holderCallback)
        isReleased = true
    }

    override fun isReleased(): Boolean {
        return isReleased
    }

    private inner class SurfaceHolderCallback : SurfaceHolder.Callback {

        override fun surfaceCreated(holder: SurfaceHolder) {
            Log.d(TAG, "surfaceCreated")

            mRenderCallback?.onSurfaceCreated(SurfaceRenderHolder(holder), 0, 0)
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            Log.d(TAG, "surfaceChanged : width = $width ,height = $height")

            mRenderCallback?.onSurfaceChanged(SurfaceRenderHolder(holder), format, width, height)
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            Log.d(TAG, "surfaceDestroyed")
            mRenderCallback?.onSurfaceDestroy(SurfaceRenderHolder(holder))
        }

    }

    private fun fixedSize(videoWidth: Int, videoHeight: Int) {
        if (videoWidth != 0 && videoHeight != 0) {
            holder.setFixedSize(videoWidth, videoHeight)
        }
    }
}
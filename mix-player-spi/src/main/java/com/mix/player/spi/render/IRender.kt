package com.mix.player.spi.render

import android.view.View
import com.mix.player.spi.player.IPlayer

const val RENDER_TYPE_TEXTURE = 0
const val RENDER_TYPE_SURFACE = 1

interface IRender {

    fun setRenderCallback(renderCallback: IRenderCallback)

    /**
     * update video rotation, such as some video maybe rotation 90 degree.
     * @param degree
     */
    fun setVideoRotation(degree: Int)

    fun setVideoSampleAspectRatio(videoSarNum: Int, videoSarDen: Int)

    /**
     * update video show aspect ratio
     *
     * see also
     * [AspectRatio.AspectRatio_16_9]
     * [AspectRatio.AspectRatio_4_3]
     * [AspectRatio.AspectRatio_FIT_PARENT]
     * [AspectRatio.AspectRatio_FILL_PARENT]
     * [AspectRatio.AspectRatio_MATCH_PARENT]
     * [AspectRatio.AspectRatio_ORIGIN]
     *
     * @param aspectRatio
     */
    fun updateAspectRatio(aspectRatio: AspectRatio)

    /**
     * update video size ,width and height.
     * @param videoWidth
     * @param videoHeight
     */
    fun updateVideoSize(videoWidth: Int, videoHeight: Int)

    fun getRenderView(): View

    fun getRenderType(): Int

    /**
     * release render,the render will become unavailable
     */
    fun release()

    /**
     * render is released ?
     * @return
     */
    fun isReleased(): Boolean

    /**
     * IRenderHolder is responsible for associate the decoder with rendering views.
     *
     * see also
     * {@link RenderSurfaceView.InternalRenderHolder#bindPlayer(IPlayer)}
     * {@link RenderTextureView.InternalRenderHolder#bindPlayer(IPlayer)}
     *
     */

    /**
     * IRenderHolder is responsible for associate the decoder with rendering views.
     *
     * see also
     * [RenderSurfaceView.InternalRenderHolder.bindPlayer]
     * [RenderTextureView.InternalRenderHolder.bindPlayer]
     *
     */
    interface IRenderHolder {
        fun bindPlayer(player: IPlayer?)
    }

    /**
     *
     * see also
     * [RenderSurfaceView.IRenderCallback]
     * [RenderTextureView.IRenderCallback]
     *
     */
    interface IRenderCallback {
        fun onSurfaceCreated(renderHolder: IRenderHolder, width: Int, height: Int)
        fun onSurfaceChanged(renderHolder: IRenderHolder, format: Int, width: Int, height: Int)
        fun onSurfaceDestroy(renderHolder: IRenderHolder)
    }
}
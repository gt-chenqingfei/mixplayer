package com.mix.player.spi.render

import android.graphics.SurfaceTexture
import android.os.Build
import android.util.Log
import android.view.Surface
import com.mix.player.spi.player.IPlayer
import java.lang.ref.WeakReference

/**
 * @author:qingfei.chain
 */
class TextureRenderHolder(textureView: MixTextureView, val surfaceTexture: SurfaceTexture) :
    IRender.IRenderHolder {
    val TAG = "TextureRenderHolder"
    private val mSurfaceRefer: WeakReference<Surface>?
    private val mTextureRefer: WeakReference<MixTextureView>?

    internal val textureView: MixTextureView?
        get() = if (mTextureRefer != null) {
            mTextureRefer.get()
        } else null

    init {
        mTextureRefer = WeakReference<MixTextureView>(textureView)
        mSurfaceRefer = WeakReference(Surface(surfaceTexture))
    }


    override fun bindPlayer(player: IPlayer?) {
        player ?: return

        textureView?.also {

            val surfaceTexture = it.getOwnSurfaceTexture()
            val useTexture = it.surfaceTexture
            var isReleased = false
            //check the SurfaceTexture is released is Android O.
            if (surfaceTexture != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                isReleased = surfaceTexture.isReleased()
            }
            val available = surfaceTexture != null && !isReleased
            //When the user sets the takeover flag and SurfaceTexture is available.
            if (it.isTakeOverSurfaceTexture() && available
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
            ) {
                //if SurfaceTexture not set or current is null, need set it.
                if (surfaceTexture != useTexture) {
                    it.surfaceTexture = surfaceTexture
                    Log.d(TAG, "setSurfaceTexture")
                } else {
                    val surface = it.mSurface
                    //release current Surface if not null.
                    if (surface != null) {
                        surface.release()
                    }
                    //create Surface use update SurfaceTexture
                    val newSurface = Surface(surfaceTexture)
                    //set it for player
                    player.setSurface(newSurface)
                    //record the new Surface
                    it.mSurface = newSurface
                    Log.d(TAG, "bindSurface")
                }
            } else {
                val surface = mSurfaceRefer?.get()
                if (surface != null) {
                    player.setSurface(surface)
                    //record the Surface
                    it.mSurface = surface
                    Log.d(TAG, "bindSurface")
                }
            }

        }


    }
}
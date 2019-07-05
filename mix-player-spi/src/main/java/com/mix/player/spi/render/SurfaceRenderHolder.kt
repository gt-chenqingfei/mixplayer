package com.mix.player.spi.render

import android.view.SurfaceHolder
import com.mix.player.spi.player.IPlayer
import java.lang.ref.WeakReference

/**
 * @author:qingfei.chain
 * Time:2019-06-10  21:04
 */
class SurfaceRenderHolder(surfaceHolder: SurfaceHolder) : IRender.IRenderHolder {

    private val mSurfaceHolder: WeakReference<SurfaceHolder>

    init {
        this.mSurfaceHolder = WeakReference(surfaceHolder)
    }

    override fun bindPlayer(player: IPlayer?) {
        if (player != null && mSurfaceHolder.get() != null) {
            player.setDisplay(mSurfaceHolder.get()!!)
        }
    }
}
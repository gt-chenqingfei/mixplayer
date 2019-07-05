package com.mix.player.qiniu

import android.view.Surface
import android.view.SurfaceHolder
import com.mix.player.spi.entity.DataSource
import com.mix.player.spi.player.AbsPlayer

/**
 * @author: qingfei.chain
 */
class QNMediaPlayer : AbsPlayer() {
    override fun setDataSource(dataSource: DataSource) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setDisplay(surfaceHolder: SurfaceHolder) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setSurface(surface: Surface) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setVolume(left: Float, right: Float) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setSpeed(speed: Float) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isPlaying(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCurrentPosition(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getDuration(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAudioSessionId(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getVideoWidth(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getVideoHeight(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getState(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun start() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun start(msc: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun pause() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun resume() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun seekTo(msc: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun stop() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun reset() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun release() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
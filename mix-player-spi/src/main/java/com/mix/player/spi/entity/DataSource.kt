package com.mix.player.spi.entity

import android.content.ContentResolver
import android.content.Context
import android.content.res.AssetFileDescriptor
import android.net.Uri
import android.text.TextUtils
import java.io.IOException
import java.io.Serializable
import java.util.*

class DataSource : Serializable {
    /**
     * extension field, you can use it if you need.
     */
    var tag: String? = null

    /**
     * extension field, you can use it if you need.
     */
    var sid: String? = null

    /**
     * Usually it's a video url.
     */
    var data: String? = null

    /**
     * you can set video name to it.
     */
    var title: String? = null

    /**
     * extension field, you can use it if you need.
     */
    var id: Long = 0

    /**
     * if you want set uri data,you can use this filed.
     */
    var uri: Uri? = null

    /**
     * if you want set some data to decoder
     * or some extra data, you can set this field.
     */
    var extra: HashMap<String, String>? = null

    /**
     * timed text source for video
     */
    var timedTextSource: TimedTextSource? = null


    //eg. a video folder in assets, the path name is video/xxx.mp4
    var assetsPath: String? = null

    //when play android raw resource, set this.
    var rawId = -1

    /**
     * If you want to start play at a specified time,
     * please set this field.
     */
    var startPos: Int = 0

    var isLive: Boolean = false

    override fun toString(): String {
        return "DataSource{" +
                "tag='" + tag + '\''.toString() +
                ", sid='" + sid + '\''.toString() +
                ", data='" + data + '\''.toString() +
                ", title='" + title + '\''.toString() +
                ", id=" + id +
                ", uri=" + uri +
                ", extra=" + extra +
//                ", timedTextSource=" + timedTextSource +
                ", assetsPath='" + assetsPath + '\''.toString() +
                ", rawId=" + rawId +
                ", startPos=" + startPos +
                ", isLive=" + isLive +
                '}'.toString()
    }

    companion object {
        fun buildRawPath(packageName: String, rawId: Int): Uri {
            return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + packageName + "/" + rawId)
        }

        fun buildAssetsUri(assetsPath: String): Uri {
            return Uri.parse("file:///android_asset/$assetsPath")
        }

        fun getAssetsFileDescriptor(context: Context, assetsPath: String): AssetFileDescriptor? {
            try {
                return if (TextUtils.isEmpty(assetsPath)) null else context.assets.openFd(assetsPath)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return null
        }
    }
}
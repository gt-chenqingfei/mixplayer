package com.mix.player

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.mix.player.spi.AppContextAttach
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val list = arrayOf(
        "http://www.w3school.com.cn/example/html5/mov_bbb.mp4",
        "http://vfx.mtime.cn/Video/2017/03/31/mp4/170331093811717750.mp4",
        "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"

    )
    var index = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppContextAttach.attach(this)

//        video_view.start()

        start.setOnClickListener {
            if (index == list.size) {
                index = 0
            }
            val url = list[index]
            video_view.setDataSource(url)
            video_view.start()

            index++
        }

        stop.setOnClickListener {
            video_view.stop()
        }


        resume.setOnClickListener {
            video_view.resume()
        }

        pause.setOnClickListener {
            video_view.pause()
        }

        release.setOnClickListener {
            video_view.stopPlayback()
        }
    }


}

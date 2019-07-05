package com.mix.player.spi.render

import java.io.Serializable

enum class AspectRatio : Serializable {
    AspectRatio_16_9,
    AspectRatio_4_3,
    AspectRatio_MATCH_PARENT,
    AspectRatio_FILL_PARENT,
    AspectRatio_FIT_PARENT,
    AspectRatio_ORIGIN,
    AspectRatio_FILL_WIDTH,
    AspectRatio_FILL_HEIGHT
}

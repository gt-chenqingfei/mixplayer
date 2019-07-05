package com.mix.player.spi.entity

import java.io.Serializable

/**
 * Author:ricebook
 * Time:2019-06-10  14:45
 */
class TimedTextSource constructor(var path: String = "", val mimeType: String = "", flag: Int = 0) : Serializable {

}
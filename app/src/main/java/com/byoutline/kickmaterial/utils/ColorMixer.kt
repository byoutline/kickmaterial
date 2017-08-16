package com.byoutline.kickmaterial.utils

object ColorMixer {
    fun mixTwoColors(color1: Int, color2: Int, amount: Float): Int {
        val ALPHA_CHANNEL: Int = 24
        val RED_CHANNEL: Int = 16
        val GREEN_CHANNEL: Int = 8
        val BLUE_CHANNEL: Int = 0

        val inverseAmount = 1.0f - amount

        val a = ((color1 shr ALPHA_CHANNEL and 0xff).toFloat() * amount + (color2 shr ALPHA_CHANNEL and 0xff).toFloat() * inverseAmount).toInt() and 0xff
        val r = ((color1 shr RED_CHANNEL and 0xff).toFloat() * amount + (color2 shr RED_CHANNEL and 0xff).toFloat() * inverseAmount).toInt() and 0xff
        val g = ((color1 shr GREEN_CHANNEL and 0xff).toFloat() * amount + (color2 shr GREEN_CHANNEL and 0xff).toFloat() * inverseAmount).toInt() and 0xff
        val b = ((color1 and 0xff).toFloat() * amount + (color2 and 0xff).toFloat() * inverseAmount).toInt() and 0xff

        return a shl ALPHA_CHANNEL or (r shl RED_CHANNEL) or (g shl GREEN_CHANNEL) or (b shl BLUE_CHANNEL)
    }
}

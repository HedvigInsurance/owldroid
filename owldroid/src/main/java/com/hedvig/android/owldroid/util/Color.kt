package com.hedvig.android.owldroid.util

import android.graphics.Color
import android.support.annotation.ColorInt

@ColorInt
fun percentageFade(@ColorInt from: Int, @ColorInt to: Int, percentage: Float): Int {
    val fromAlpha = Color.alpha(from)
    val fromRed = Color.red(from)
    val fromGreen = Color.green(from)
    val fromBlue = Color.blue(from)

    val toAlpha = Color.alpha(to)
    val toRed = Color.red(to)
    val toGreen = Color.green(to)
    val toBlue = Color.blue(to)

    val diffAlpha = Math.abs(toAlpha - fromAlpha)
    val diffRed = Math.abs(toRed - fromRed)
    val diffGreen = Math.abs(toGreen - fromGreen)
    val diffBlue = Math.abs(toBlue - fromBlue)

    val factorAlpha = (diffAlpha * percentage).toInt()
    val factorRed = (diffRed * percentage).toInt()
    val factorGreen = (diffGreen * percentage).toInt()
    val factorBlue = (diffBlue * percentage).toInt()

    var resAlpha = if (fromAlpha > toAlpha) fromAlpha - factorAlpha else fromAlpha + factorAlpha
    var resRed = if (fromRed > toRed) fromRed - factorRed else fromRed + factorRed
    var resGreen = if (fromGreen > toGreen) fromGreen - factorGreen else fromGreen + factorGreen
    var resBlue = if (fromBlue > toBlue) fromBlue - factorBlue else fromBlue + factorBlue

    resAlpha = when (toAlpha) {
        in resAlpha..(fromAlpha - 1) -> toAlpha
        in (fromAlpha + 1)..resAlpha -> toAlpha
        else -> resAlpha
    }

    resRed = when (toRed) {
        in resRed..(fromRed - 1) -> toRed
        in (fromRed + 1)..resRed -> toRed
        else -> resRed
    }

    resGreen = when (toGreen) {
        in resGreen..(fromGreen - 1) -> toGreen
        in (fromGreen + 1)..resGreen -> toGreen
        else -> resGreen
    }

    resBlue = when (toBlue) {
        in resBlue..(fromBlue - 1) -> toBlue
        in (fromBlue + 1)..resBlue -> toBlue
        else -> resBlue
    }

    return Color.argb(resAlpha, resRed, resGreen, resBlue)
}
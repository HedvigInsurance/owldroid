package com.hedvig.android.owldroid.util.extensions.view

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator

fun View.animateExpand(
    duration: Long = 200,
    interpolator: TimeInterpolator = DecelerateInterpolator()
) {
    measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    val targetHeight = measuredHeight

    val currentHeight = height
    show()
    val valueAnimator = ValueAnimator.ofInt(currentHeight, targetHeight)
    valueAnimator.addUpdateListener { animation ->
        layoutParams.height = animation.animatedValue as Int
        requestLayout()
    }

    valueAnimator.interpolator = interpolator
    valueAnimator.duration = duration
    valueAnimator.start()
}

fun View.animateCollapse(
    targetHeight: Int = 0,
    duration: Long = 200,
    interpolator: TimeInterpolator = DecelerateInterpolator()
) {
    measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

    val currentHeight = height
    show()
    val valueAnimator = ValueAnimator.ofInt(currentHeight, targetHeight)
    valueAnimator.addUpdateListener { animation ->
        layoutParams.height = animation.animatedValue as Int
        requestLayout()
    }

    valueAnimator.interpolator = interpolator
    valueAnimator.duration = duration
    valueAnimator.start()
}

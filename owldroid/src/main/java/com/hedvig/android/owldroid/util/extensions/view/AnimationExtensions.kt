package com.hedvig.android.owldroid.util.extensions.view

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.TextView

fun View.animateExpand(
    duration: Long = 200,
    interpolator: TimeInterpolator = DecelerateInterpolator()
) {
    val targetHeight = if (this is TextView) {
        val parentWidth = (parent as View).measuredWidth
        measure(View.MeasureSpec.makeMeasureSpec(parentWidth, View.MeasureSpec.EXACTLY), ViewGroup.LayoutParams.WRAP_CONTENT)
        (measuredHeight + paint.fontSpacing).toInt()
    } else {
        measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        measuredHeight
    }

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

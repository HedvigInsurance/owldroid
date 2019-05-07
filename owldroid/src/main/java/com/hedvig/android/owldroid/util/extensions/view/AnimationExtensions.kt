package com.hedvig.android.owldroid.util.extensions.view

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.TextView

fun View.animateExpand(
    duration: Long = 200,
    interpolator: TimeInterpolator = DecelerateInterpolator(),
    updateCallback: (() -> Unit)? = null,
    withOpacity: Boolean = false
) {
    val targetHeight = if (this is TextView) {
        val parentWidth = (parent as View).measuredWidth
        measure(
            View.MeasureSpec.makeMeasureSpec(parentWidth, View.MeasureSpec.EXACTLY),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        (measuredHeight + paint.fontSpacing).toInt()
    } else {
        measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        measuredHeight
    }

    val currentHeight = height
    show()
    ValueAnimator.ofInt(currentHeight, targetHeight).apply {
        addUpdateListener { animation ->
            layoutParams.height = animation.animatedValue as Int
            if (withOpacity) {
                alpha = animation.animatedFraction
            }
            requestLayout()
            updateCallback?.invoke()
        }
        this.interpolator = interpolator
        this.duration = duration
        start()
    }
}

fun View.animateCollapse(
    targetHeight: Int = 0,
    duration: Long = 200,
    interpolator: TimeInterpolator = DecelerateInterpolator(),
    withOpacity: Boolean = false
) {
    measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

    val currentHeight = height
    show()
    ValueAnimator.ofInt(currentHeight, targetHeight).apply {
        addUpdateListener { animation ->
            layoutParams.height = animation.animatedValue as Int
            if (withOpacity) {
                alpha = 1.0f - animation.animatedFraction
            }
            requestLayout()
        }

        this.interpolator = interpolator
        this.duration = duration
        start()
    }
}

package com.hedvig.android.owldroid.util.extensions

import android.app.Activity
import android.content.Context
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.annotation.FontRes
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.content.res.AppCompatResources
import android.view.View
import android.view.inputmethod.InputMethodManager

fun Context.compatColor(@ColorRes color: Int) = ContextCompat.getColor(this, color)

fun Context.compatFont(@FontRes font: Int) = ResourcesCompat.getFont(this, font)

fun Context.compatDrawable(@DrawableRes drawable: Int) = AppCompatResources.getDrawable(this, drawable)

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

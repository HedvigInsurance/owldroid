package com.hedvig.android.owldroid.util.extensions

import android.support.annotation.*
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.LocalBroadcastManager
import com.hedvig.android.owldroid.ui.common.RoundedBottomSheetDialogFragment
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.app_bar.*

val Fragment.localBroadcastManager get() = LocalBroadcastManager.getInstance(requireContext())

fun FragmentManager.showBottomSheetDialog(@LayoutRes layout: Int) =
    RoundedBottomSheetDialogFragment.newInstance(layout).show(this, "BottomSheetDialogFragment")

fun Fragment.setupLargeTitle(
    @StringRes title: Int,
    @FontRes font: Int,
    @DrawableRes icon: Int? = null,
    @ColorInt backgroundColor: Int? = null,
    backAction: (() -> Unit)? = null
) {
    setupLargeTitle(getString(title), font, icon, backgroundColor, backAction)
}

fun Fragment.setupLargeTitle(
    title: String,
    @FontRes font: Int,
    @DrawableRes icon: Int? = null,
    @ColorInt backgroundColor: Int? = null,
    backAction: (() -> Unit)? = null
) {
    (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
    collapsingToolbar.title = title
    val resolvedFont = requireContext().compatFont(font)
    collapsingToolbar.setExpandedTitleTypeface(resolvedFont)
    collapsingToolbar.setCollapsedTitleTypeface(resolvedFont)

    backgroundColor?.let {color ->
        toolbar.setBackgroundColor(color)
        collapsingToolbar.setBackgroundColor(color)
    }

    icon?.let { toolbar.setNavigationIcon(it) }
    backAction?.let { toolbar.setNavigationOnClickListener { it() } }
}

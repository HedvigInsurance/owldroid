package com.hedvig.android.owldroid.ui.profile

import android.content.Context
import android.support.v7.widget.LinearLayoutCompat
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView
import com.hedvig.android.owldroid.R

class ProfileMenuRow(context: Context, attributeSet: AttributeSet) : LinearLayoutCompat(context, attributeSet) {
    init {
        inflate(context, R.layout.profile_menu_row, this)
    }

    private fun setupDynamicContent() {
        val attributesArray = arrayOf(R.attr.drawableSrc, R.attr.name, R.attr.description)
        val attributes = context.obtainStyledAttributes(attributesArray.toIntArray())
        attributes.recycle()
        findViewById<ImageView>(R.id.profile_menu_row_icon).setImageDrawable(context.resources.getDrawable(attributes.getIndex(0)))
        findViewById<TextView>(R.id.profile_menu_row_name).setText(attributes.getIndex(1))
        findViewById<TextView>(R.id.profile_menu_row_description).setText(attributes.getIndex(2))
    }
}

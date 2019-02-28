package com.hedvig.android.owldroid.ui.profile

import android.content.Context
import android.support.v7.widget.LinearLayoutCompat
import android.util.AttributeSet
import com.hedvig.android.owldroid.R
import kotlinx.android.synthetic.main.profile_menu_row.view.*

class ProfileMenuRow(context: Context, attributeSet: AttributeSet) : LinearLayoutCompat(context, attributeSet) {
    init {
        inflate(context, R.layout.profile_menu_row, this)
        setupDynamicContent(attributeSet)
    }

    private fun setupDynamicContent(attributeSet: AttributeSet) {
        val attributes = context.theme.obtainStyledAttributes(attributeSet, R.styleable.ProfileMenuRow, 0, 0)

        profile_menu_row_icon.setImageResource(attributes.getResourceId(R.styleable.ProfileMenuRow_drawableSrc, 0))
        profile_menu_row_description.text = attributes.getText(R.styleable.ProfileMenuRow_description)
        profile_menu_row_name.text = attributes.getText(R.styleable.ProfileMenuRow_name)

        attributes.recycle()
    }
}

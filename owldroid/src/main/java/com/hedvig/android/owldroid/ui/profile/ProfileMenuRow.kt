package com.hedvig.android.owldroid.ui.profile

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.Gravity
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.util.extensions.compatColor
import com.hedvig.android.owldroid.util.extensions.compatDrawable
import com.hedvig.android.owldroid.util.extensions.remove
import com.hedvig.android.owldroid.util.extensions.show
import kotlinx.android.synthetic.main.profile_menu_row.view.*

class ProfileMenuRow : ConstraintLayout {
    private var attributeSet: AttributeSet? = null
    private var defStyle: Int = 0

    constructor(
        context: Context,
        attributeSet: AttributeSet?,
        defStyle: Int
    ) : super(context, attributeSet, defStyle) {
        this.attributeSet = attributeSet
        this.defStyle = defStyle
        inflate(context, R.layout.profile_menu_row, this)
        setupDynamicContent()
    }

    constructor(
        context: Context,
        attributeSet: AttributeSet?
    ) : super(context, attributeSet) {
        this.attributeSet = attributeSet
        inflate(context, R.layout.profile_menu_row, this)
        setupDynamicContent()
    }

    constructor(context: Context) : super(context) {
        inflate(context, R.layout.profile_menu_row, this)
        setupDynamicContent()
    }

    var icon: Drawable? = null
        set(value) {
            field = value
            profile_menu_row_icon.setImageDrawable(field)
        }
    var name: CharSequence? = null
        set(value) {
            field = value
            profile_menu_row_name.text = field
        }
    var description: CharSequence? = null
        set(value) {
            field = value
            profile_menu_row_description.text = field
            profile_menu_row_description.show()
            profile_menu_row_name.gravity = Gravity.NO_GRAVITY
        }

    fun setHighlighted() {
        profile_menu_row.background = context.compatDrawable(R.drawable.purple_selectable)

        val resolvedColor = context.compatColor(R.color.white)
        profile_menu_row_name.setTextColor(resolvedColor)
        profile_menu_row_description.setTextColor(resolvedColor)

        iconNavigateNext.setColorFilter(resolvedColor)
    }

    private fun setupDynamicContent() {
        val attributes = context.obtainStyledAttributes(attributeSet, R.styleable.ProfileMenuRow, defStyle, 0)

        icon = attributes.getDrawable(R.styleable.ProfileMenuRow_iconImage)
        name = attributes.getText(R.styleable.ProfileMenuRow_name)

        val description = attributes.getText(R.styleable.ProfileMenuRow_description)
        description?.let { d ->
            profile_menu_row_description.text = d
        } ?: makeSingleLine()

        attributes.recycle()
    }

    private fun makeSingleLine() {
        profile_menu_row_description.remove()
        profile_menu_row_name.gravity = Gravity.CENTER_VERTICAL
    }
}

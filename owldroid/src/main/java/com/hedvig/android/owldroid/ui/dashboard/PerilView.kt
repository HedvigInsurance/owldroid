package com.hedvig.android.owldroid.ui.dashboard

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.LinearLayout
import com.hedvig.android.owldroid.R
import kotlinx.android.synthetic.main.peril_view.view.*

class PerilView : LinearLayout {
    private var attributeSet: AttributeSet? = null
    private var defStyle: Int = 0

    constructor(context: Context) : super(context) {
        inflate(context, R.layout.peril_view, this)
        setupAttributes()
    }

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {
        this.attributeSet = attributeSet
        setupAttributes()
    }

    constructor(context: Context, attributeSet: AttributeSet?, defStyle: Int) : super(context, attributeSet, defStyle) {
        this.attributeSet = attributeSet
        this.defStyle = defStyle
        setupAttributes()
    }

    var perilIcon: Drawable? = null
        set(value) {
            field = value
            image.setImageDrawable(value)
        }

    var perilName: CharSequence? = null
        set(value) {
            field = value
            text.text = value
        }

    fun setupAttributes() {
        val attributes = context.obtainStyledAttributes(
            attributeSet,
            R.styleable.PerilView,
            defStyle,
            0
        )

        perilIcon = attributes.getDrawable(R.styleable.PerilView_perilIcon)
        perilName = attributes.getText(R.styleable.PerilView_perilText)

        attributes.recycle()
    }
}

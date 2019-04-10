package com.hedvig.android.owldroid.ui.dashboard

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.util.extensions.compatDrawable
import kotlinx.android.synthetic.main.peril_view.view.*

class PerilView : LinearLayout {
    private var attributeSet: AttributeSet? = null
    private var defStyle: Int = 0

    private val iconSize: Int by lazy { resources.getDimensionPixelSize(R.dimen.peril_icon) }

    constructor(context: Context) : super(context) {
        inflate(context, R.layout.peril_view, this)
        setupAttributes()
    }

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {
        inflate(context, R.layout.peril_view, this)
        this.attributeSet = attributeSet
        setupAttributes()
    }

    constructor(context: Context, attributeSet: AttributeSet?, defStyle: Int) : super(context, attributeSet, defStyle) {
        inflate(context, R.layout.peril_view, this)
        this.attributeSet = attributeSet
        this.defStyle = defStyle
        setupAttributes()
    }

    var perilIcon: Drawable? = null
        set(value) {
            field = value
            image.setImageDrawable(value)
        }

    var perilIconId: String? = null
        set(value) {
            field = value
            value?.let { image.setImageDrawable(context.compatDrawable(PerilIcon.from(it))) }
        }

    var perilIconUrl: Uri? = null
        set(value) {
            field = value

            Glide
                .with(context)
                .load(value)
                .override(iconSize)
                .into(image)
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

package com.hedvig.android.owldroid.ui.dashboard

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import com.hedvig.android.owldroid.R
import kotlinx.android.synthetic.main.peril_category_view.view.*

class PerilCategoryView : FrameLayout {
    private var attributeSet: AttributeSet? = null
    private var defStyle: Int = 0

    constructor(context: Context) : super(context) {
        inflate(context, R.layout.peril_category_view, this)
        setupAttributes()
    }

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {
        this.attributeSet = attributeSet
        inflate(context, R.layout.peril_category_view, this)
        setupAttributes()
    }

    constructor(
        context: Context,
        attributeSet: AttributeSet?,
        defStyle: Int
    ) : super(context, attributeSet, defStyle) {
        this.attributeSet = attributeSet
        this.defStyle = defStyle
        inflate(context, R.layout.peril_category_view, this)
        setupAttributes()
    }

    var categoryIcon: CharSequence? = null
        set(value) {
            field = value

            Glide
                .with(context)
                .load(Uri.parse(value.toString()))
                .into(catIcon)
        }

    var title: CharSequence? = null
        set(value) {
            field = value
            categoryTitle.text = value
        }

    var subtitle: CharSequence? = null
        set(value) {
            field = value
            categorySubtitle.text = value
        }

    private fun setupAttributes() {
        val attributes = context.obtainStyledAttributes(
            attributeSet,
            R.styleable.PerilCategoryView,
            defStyle,
            0
        )

        categoryIcon = attributes.getText(R.styleable.PerilCategoryView_categoryUrl)
        title = attributes.getText(R.styleable.PerilCategoryView_title)
        subtitle = attributes.getText(R.styleable.PerilCategoryView_subtitle)

        attributes.recycle()
    }
}

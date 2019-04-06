package com.hedvig.android.owldroid.ui.dashboard

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.support.design.card.MaterialCardView
import android.util.AttributeSet
import com.bumptech.glide.Glide
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.util.whenApiVersion
import kotlinx.android.synthetic.main.peril_category_view.view.*

class PerilCategoryView : MaterialCardView {
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

    var categoryIconUrl: Uri? = null
        set(value) {
            field = value

            Glide
                .with(context)
                .load(value)
                .into(catIcon)
        }

    var categoryIcon: Drawable? = null
        set(value) {
            field = value
            catIcon.setImageDrawable(value)
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
        resources.getDimension(R.dimen.base_margin).let { bm ->
            radius = bm
            whenApiVersion(Build.VERSION_CODES.LOLLIPOP) {
                elevation = bm
            }
        }
        resources.getDimensionPixelSize(R.dimen.base_margin).let { setPadding(it, it, it, it) }
        clipToPadding = true

        val attributes = context.obtainStyledAttributes(
            attributeSet,
            R.styleable.PerilCategoryView,
            defStyle,
            0
        )

        attributes.getText(R.styleable.PerilCategoryView_categoryUrl)?.let { url ->
            categoryIconUrl = Uri.parse(url.toString())
        }
        title = attributes.getText(R.styleable.PerilCategoryView_title)
        subtitle = attributes.getText(R.styleable.PerilCategoryView_subtitle)

        attributes.recycle()
    }

    fun setLast() {
        val baseMargin = resources.getDimensionPixelSize(R.dimen.base_margin)
        val doubleMargin = resources.getDimensionPixelSize(R.dimen.base_margin_double)

        setPadding(baseMargin, baseMargin, baseMargin, doubleMargin)
    }

    fun setExpandedContent() {
        // TODO Add function to insert expanded content
    }
}

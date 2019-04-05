package com.hedvig.android.owldroid.ui.dashboard

import android.content.Context
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

        categoryIcon = attributes.getText(R.styleable.PerilCategoryView_categoryUrl)
        title = attributes.getText(R.styleable.PerilCategoryView_title)
        subtitle = attributes.getText(R.styleable.PerilCategoryView_subtitle)

        attributes.recycle()
    }

/*    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    inner class OutlineProvider(
        private val rect: Rect = Rect(),
        var scaleX: Float,
        var scaleY: Float,
        var yShift: Int
    ) : ViewOutlineProvider() {
        override fun getOutline(view: View?, outline: Outline?) {
            view?.background?.copyBounds()
        }
    }*/
}

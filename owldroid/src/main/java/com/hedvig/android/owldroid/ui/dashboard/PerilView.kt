package com.hedvig.android.owldroid.ui.dashboard

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.annotation.DrawableRes
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

            @DrawableRes val iconRes = when (value) {
                "ME.LEGAL" -> R.drawable.ic_legal
                "ME.ASSAULT" -> R.drawable.ic_assault
                "ME.TRAVEL.SICK" -> R.drawable.ic_illness
                "ME.TRAVEL.LUGGAGE.DELAY" -> R.drawable.ic_luggage_delay
                "HOUSE.BRF.FIRE" -> R.drawable.ic_fire_red
                "HOUSE.RENT.FIRE" -> R.drawable.ic_fire_red
                "HOUSE.SUBLET.BRF.FIRE" -> R.drawable.ic_fire_red
                "HOUSE.SUBLET.RENT.FIRE" -> R.drawable.ic_fire_red
                "HOUSE.BRF.APPLIANCES" -> R.drawable.ic_appliances
                "HOUSE.RENT.APPLIANCES" -> R.drawable.ic_appliances
                "HOUSE.SUBLET.BRF.APPLIANCES" -> R.drawable.ic_appliances
                "HOUSE.SUBLET.RENT.APPLIANCES" -> R.drawable.ic_appliances
                "HOUSE.BRF.WEATHER" -> R.drawable.ic_weather_red
                "HOUSE.RENT.WEATHER" -> R.drawable.ic_weather_red
                "HOUSE.SUBLET.BRF.WEATHER" -> R.drawable.ic_weather_red
                "HOUSE.SUBLET.RENT.WEATHER" -> R.drawable.ic_weather_red
                "HOUSE.BRF.WATER" -> R.drawable.ic_water_red
                "HOUSE.RENT.WATER" -> R.drawable.ic_water_red
                "HOUSE.SUBLET.BRF.WATER" -> R.drawable.ic_water_red
                "HOUSE.SUBLET.RENT.WATER" -> R.drawable.ic_water_red
                "HOUSE.BREAK-IN" -> R.drawable.ic_break_in
                "HOUSE.DAMAGE" -> R.drawable.ic_vandalism_red
                "STUFF.CARELESS" -> R.drawable.ic_accidental_damage
                "STUFF.THEFT" -> R.drawable.ic_theft
                "STUFF.DAMAGE" -> R.drawable.ic_vandalism_green
                "STUFF.BRF.FIRE" -> R.drawable.ic_fire_green
                "STUFF.RENT.FIRE" -> R.drawable.ic_fire_green
                "STUFF.SUBLET.BRF.FIRE" -> R.drawable.ic_fire_green
                "STUFF.SUBLET.RENT.FIRE" -> R.drawable.ic_fire_green
                "STUFF.BRF.WATER" -> R.drawable.ic_water_green
                "STUFF.RENT.WATER" -> R.drawable.ic_water_green
                "STUFF.SUBLET.BRF.WATER" -> R.drawable.ic_water_green
                "STUFF.SUBLET.RENT.WATER" -> R.drawable.ic_water_green
                "STUFF.BRF.WEATHER" -> R.drawable.ic_weather_green
                else -> R.drawable.ic_vandalism_green

            }

            image.setImageDrawable(context.compatDrawable(iconRes))
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

package com.hedvig.android.owldroid.ui.dashboard

import android.support.annotation.DrawableRes
import com.hedvig.android.owldroid.R

object PerilIcon {
    @DrawableRes
    fun from(id: String) = when (id) {
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
}

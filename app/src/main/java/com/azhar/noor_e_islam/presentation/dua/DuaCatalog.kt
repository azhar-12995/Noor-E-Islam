package com.azhar.noor_e_islam.presentation.dua

import androidx.annotation.DrawableRes
import com.azhar.noor_e_islam.R

/**
 * A single Du'a, identified by [id] (used as the navigation arg) and rendered
 * from the bundled [image] webp drawable.
 */
internal data class Dua(
    val id: String,
    val name: String,
    @DrawableRes val image: Int,
)

/**
 * Master list of available duas. [name] is derived from the drawable filename
 * and presented to the user; [id] is the stable lookup key passed via nav args.
 */
internal val duas: List<Dua> = listOf(
    Dua("morning",                 "Morning Dua",               R.drawable.morning_dua),
    Dua("evening",                 "Evening Dua",               R.drawable.evening_dua),
    Dua("after_woke_up",           "Dua After Waking Up",       R.drawable.after_woke_up_dua),
    Dua("before_sleeping",         "Dua Before Sleeping",       R.drawable.before_sleeping_dua),
    Dua("entering_home",           "Dua for Entering Home",     R.drawable.entering_home_dua),
    Dua("before_leaving_home",     "Dua Before Leaving Home",   R.drawable.before_leaving_home_dua),
    Dua("entering_mosque",         "Dua for Entering Mosque",   R.drawable.entering_mosque_dua),
    Dua("leaving_mosque",          "Dua for Leaving Mosque",    R.drawable.leaving_mosque_dua),
    Dua("entering_market",         "Dua for Entering Market",   R.drawable.entering_market_dua),
    Dua("before_entering_toilet",  "Dua Before Entering Toilet", R.drawable.before_entering_toilet_dua),
    Dua("leaving_toilet",          "Dua After Leaving Toilet",  R.drawable.leaving_tilot_dua),
    Dua("putting_on_dress",        "Dua When Putting on Dress", R.drawable.putting_on_dress_dua),
    Dua("greeting",                "Greeting Dua",              R.drawable.greeting_dua),
    Dua("gathering_end",           "Dua at End of Gathering",   R.drawable.gathering_end_dua),
    Dua("journey",                 "Dua for Journey",           R.drawable.journey_dua),
    Dua("traveller",               "Traveller's Dua",           R.drawable.traveller_dua),
    Dua("protection",              "Dua for Protection",        R.drawable.protection_dua),
)

internal fun findDua(id: String): Dua? = duas.firstOrNull { it.id == id }


package dev.enokk.schedato.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import dev.enokk.schedato.R

enum class
AppClass(@StringRes val labelRes: Int, @DrawableRes val drawableRes: Int) {
    BARBARIAN(R.string.class_barbarian, R.drawable.class_barbarian),
    BARD(R.string.class_bard, R.drawable.class_bard),
    CLERIC(R.string.class_cleric, R.drawable.class_cleric),
    DRUID(R.string.class_druid, R.drawable.class_druid),
    FIGHTER(R.string.class_fighter, R.drawable.class_fighter),
    MONK(R.string.class_monk, R.drawable.class_monk),
    PALADIN(R.string.class_paladin, R.drawable.class_paladin),
    RANGER(R.string.class_ranger, R.drawable.class_ranger),
    ROGUE(R.string.class_rogue, R.drawable.class_rogue),
    SORCERER(R.string.class_sorcerer, R.drawable.class_sorcerer),
    WARLOCK(R.string.class_warlock, R.drawable.class_warlock),
    WIZARD(R.string.class_wizard, R.drawable.class_wizard)
}

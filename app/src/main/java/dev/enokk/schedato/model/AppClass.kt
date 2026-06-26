package dev.enokk.schedato.model

import androidx.annotation.StringRes
import dev.enokk.schedato.R

enum class AppClass(@StringRes val labelRes: Int) {
    BARBARIAN(R.string.class_barbarian),
    BARD(R.string.class_bard),
    CLERIC(R.string.class_cleric),
    DRUID(R.string.class_druid),
    FIGHTER(R.string.class_fighter),
    MONK(R.string.class_monk),
    PALADIN(R.string.class_paladin),
    RANGER(R.string.class_ranger),
    ROGUE(R.string.class_rogue),
    SORCERER(R.string.class_sorcerer),
    WARLOCK(R.string.class_warlock),
    WIZARD(R.string.class_wizard)
}

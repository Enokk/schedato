package dev.enokk.schedato.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import dev.enokk.schedato.R

data class RaceGroup(
    @StringRes val labelRes: Int,
    @DrawableRes val drawableRes: Int,
    val races: List<AppRace>
)

val RACE_GROUPS: List<RaceGroup> = listOf(
    RaceGroup(R.string.race_group_human,    R.drawable.race_human,      listOf(AppRace.HUMAN_STANDARD, AppRace.HUMAN_VARIANT)),
    RaceGroup(R.string.race_group_elf,      R.drawable.race_elf,        listOf(AppRace.ELF_HIGH, AppRace.ELF_WOOD, AppRace.ELF_DARK, AppRace.HALF_ELF)),
    RaceGroup(R.string.race_group_dwarf,    R.drawable.race_dwarf,      listOf(AppRace.DWARF_HILL, AppRace.DWARF_MOUNTAIN)),
    RaceGroup(R.string.race_group_halfling, R.drawable.race_halfling,   listOf(AppRace.HALFLING_LIGHTFOOT, AppRace.HALFLING_STOUT)),
    RaceGroup(R.string.race_group_gnome,    R.drawable.race_gnome,      listOf(AppRace.GNOME_FOREST, AppRace.GNOME_ROCK)),
    RaceGroup(R.string.race_half_orc,       R.drawable.race_halforc,    listOf(AppRace.HALF_ORC)),
    RaceGroup(R.string.race_tiefling,       R.drawable.race_tiefling,   listOf(AppRace.TIEFLING)),
    RaceGroup(R.string.race_dragonborn,     R.drawable.race_dragonborn, listOf(AppRace.DRAGONBORN))
)

package dev.enokk.schedato.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import dev.enokk.schedato.R

enum class AppRace(@StringRes val labelRes: Int, @DrawableRes val drawableRes: Int) {
    DRAGONBORN(R.string.race_dragonborn,          R.drawable.race_dragonborn),
    DWARF_HILL(R.string.race_dwarf_hill,           R.drawable.race_dwarf),
    DWARF_MOUNTAIN(R.string.race_dwarf_mountain,   R.drawable.race_dwarf),
    ELF_HIGH(R.string.race_elf_high,               R.drawable.race_elf),
    ELF_WOOD(R.string.race_elf_wood,               R.drawable.race_elf),
    ELF_DARK(R.string.race_elf_dark,               R.drawable.race_elf),
    GNOME_FOREST(R.string.race_gnome_forest,       R.drawable.race_gnome),
    GNOME_ROCK(R.string.race_gnome_rock,           R.drawable.race_gnome),
    HALF_ELF(R.string.race_half_elf,               R.drawable.race_elf),
    HALF_ORC(R.string.race_half_orc,               R.drawable.race_halforc),
    HALFLING_LIGHTFOOT(R.string.race_halfling_lightfoot, R.drawable.race_halfling),
    HALFLING_STOUT(R.string.race_halfling_stout,   R.drawable.race_halfling),
    HUMAN_STANDARD(R.string.race_human_standard,   R.drawable.race_human),
    HUMAN_VARIANT(R.string.race_human_variant,     R.drawable.race_human),
    TIEFLING(R.string.race_tiefling,               R.drawable.race_tiefling)
}

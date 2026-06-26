package dev.enokk.schedato.model

import androidx.annotation.StringRes
import dev.enokk.schedato.R

enum class AppRace(@StringRes val labelRes: Int) {
    DRAGONBORN(R.string.race_dragonborn),
    DWARF_HILL(R.string.race_dwarf_hill),
    DWARF_MOUNTAIN(R.string.race_dwarf_mountain),
    ELF_HIGH(R.string.race_elf_high),
    ELF_WOOD(R.string.race_elf_wood),
    ELF_DARK(R.string.race_elf_dark),
    GNOME_FOREST(R.string.race_gnome_forest),
    GNOME_ROCK(R.string.race_gnome_rock),
    HALF_ELF(R.string.race_half_elf),
    HALF_ORC(R.string.race_half_orc),
    HALFLING_LIGHTFOOT(R.string.race_halfling_lightfoot),
    HALFLING_STOUT(R.string.race_halfling_stout),
    HUMAN_STANDARD(R.string.race_human_standard),
    HUMAN_VARIANT(R.string.race_human_variant),
    TIEFLING(R.string.race_tiefling)
}

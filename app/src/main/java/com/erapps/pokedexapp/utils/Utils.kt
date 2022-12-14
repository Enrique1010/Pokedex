package com.erapps.pokedexapp.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import com.erapps.pokedexapp.ui.theme.*

fun String.makeGoodTitle(): String {
    return this.replace("-", " ").capitalize(Locale.current)
}

fun String.getIdFromUrl(): String {
    return Regex("[0-9]+").findAll(this)
        .map(MatchResult::value)
        .toList()[1]
}

fun getSpecialConditionColor(conditionName: String): Color {
    return when(conditionName) {
        "legendary" -> SpAtkColor
        "mythic" -> SpdColor
        "baby" -> SpDefColor
        else -> Color.DarkGray
    }
}

fun getPokemonTypeToColor(type: String): Color {
    return when(type.lowercase()) {
        "normal" -> TypeNormal
        "fire" -> TypeFire
        "water" -> TypeWater
        "electric" -> TypeElectric
        "grass" -> TypeGrass
        "ice" -> TypeIce
        "fighting" -> TypeFighting
        "poison" -> TypePoison
        "ground" -> TypeGround
        "flying" -> TypeFlying
        "psychic" -> TypePsychic
        "bug" -> TypeBug
        "rock" -> TypeRock
        "ghost" -> TypeGhost
        "dragon" -> TypeDragon
        "dark" -> TypeDark
        "steel" -> TypeSteel
        "fairy" -> TypeFairy
        else -> Color.Black
    }
}

fun getMoveStatColor(stat: String): Color {
    return when(stat){
        "accuracy" -> AccuracyColor
        "power" -> PowerColor
        else -> Color.White
    }
}

fun getPokemonStatToColor(stat: String): Color {
    return when(stat.lowercase()) {
        "hp" -> HPColor
        "attack" -> AtkColor
        "defense" -> DefColor
        "special-attack" -> SpAtkColor
        "special-defense" -> SpDefColor
        "speed" -> SpdColor
        else -> Color.White
    }
}

fun abbrStat(stat: String): String {
    return when(stat.lowercase()) {
        "hp" -> "HP"
        "attack" -> "Atk"
        "defense" -> "Def"
        "special-attack" -> "SpAtk"
        "special-defense" -> "SpDef"
        "speed" -> "Spd"
        else -> ""
    }
}
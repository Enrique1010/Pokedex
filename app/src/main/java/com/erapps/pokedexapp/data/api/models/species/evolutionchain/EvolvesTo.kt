package com.erapps.pokedexapp.data.api.models.species.evolutionchain

data class EvolvesTo(
    val evolution_details: List<EvolutionDetail>,
    val evolves_to: List<EvolvesToX>,
    val is_baby: Boolean,
    val species: SpeciesXX
)
package com.erapps.pokedexapp.ui.screens.details.abilities

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.erapps.pokedexapp.R
import com.erapps.pokedexapp.data.api.models.abilities.AbilityDetails
import com.erapps.pokedexapp.data.api.models.abilities.EffectEntryX
import com.erapps.pokedexapp.data.api.models.abilities.FlavorTextEntry
import com.erapps.pokedexapp.ui.shared.DetailsPageWithState
import com.erapps.pokedexapp.ui.shared.SpacedDivider
import com.erapps.pokedexapp.utils.Constants.COLUMN1WEIGHT
import com.erapps.pokedexapp.utils.Constants.COLUMN2WEIGHT
import com.erapps.pokedexapp.utils.Constants.FILTERLANGUAGE
import com.erapps.pokedexapp.utils.makeGoodTitle

@Composable
fun AbilityDetailsScreen(
    viewModel: AbilityDetailsViewModel = hiltViewModel(),
    onBackPressed: () -> Unit
) {

    val uiState = viewModel.uiState.value
    val previousBackGroundColor = viewModel.backGroundColor.value

    DetailsPageWithState<AbilityDetails>(
        previousBackGroundColor = previousBackGroundColor,
        uiState = uiState,
        onBackPressed = onBackPressed
    ) { data ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(id = R.dimen.dimen_8dp))
                .background(MaterialTheme.colors.surface),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = data.name.makeGoodTitle(),
                fontSize = dimensionResource(id = R.dimen.ability_details_title).value.sp,
                fontWeight = FontWeight.Bold,
                color = previousBackGroundColor
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.dimen_16dp)))
            AbilityDetailsContent(
                flavorTextEntries = data.flavor_text_entries,
                effectEntries = data.effect_entries,
                color = previousBackGroundColor
            )
        }
    }
}

@Composable
private fun AbilityDetailsContent(
    modifier: Modifier = Modifier,
    flavorTextEntries: List<FlavorTextEntry>,
    effectEntries: List<EffectEntryX>,
    color: Color,
) {

    val effects = remember {
        getFilteredEffectEntries(effectEntries)[0]
    }
    val flavorTexts = remember {
        getFilteredFlavorEntries(flavorTextEntries)
    }

    Column(
        modifier = modifier.padding(dimensionResource(id = R.dimen.dimen_16dp)),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        SpacedDivider(modifier = modifier, color = color)
        EffectSection(effect = effects.effect, shortEffect = effects.short_effect, color = color)
        TableScreen(flavorTexts = flavorTexts, color = color)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TableScreen(
    modifier: Modifier = Modifier,
    flavorTexts: List<FlavorTextEntry>,
    color: Color,
) {

    //header
    Row(Modifier.background(color)) {
        AnnotatedStringTableCell(
            text = stringResource(id = R.string.feature_table_game_label),
            weight = COLUMN1WEIGHT,
            color = MaterialTheme.colors.onBackground
        )
        AnnotatedStringTableCell(
            text = stringResource(id = R.string.feature_table_feature_label),
            weight = COLUMN2WEIGHT,
            color = MaterialTheme.colors.onBackground
        )
    }
    //table
    CompositionLocalProvider(
        LocalOverscrollConfiguration provides null
    ) {
        LazyColumn(
            modifier
                .fillMaxSize()
                .padding(dimensionResource(id = R.dimen.dimen_4dp))
        ) {
            items(flavorTexts) { flavorTexts ->
                Row(
                    modifier = modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AnnotatedStringTableCell(
                        text = flavorTexts.version_group.name.makeGoodTitle(),
                        weight = COLUMN1WEIGHT,
                        color = color
                    )
                    TableCell(text = flavorTexts.flavor_text, weight = COLUMN2WEIGHT)
                }
            }
        }
    }
}

@Composable
private fun RowScope.TableCell(
    text: String,
    weight: Float,
) {
    Text(
        text = text,
        modifier = Modifier
            .border(dimensionResource(id = R.dimen.basic_border), MaterialTheme.colors.surface)
            .weight(weight)
            .padding(dimensionResource(id = R.dimen.dimen_8dp)),
        color = MaterialTheme.colors.onBackground
    )
}

@Composable
private fun RowScope.AnnotatedStringTableCell(
    modifier: Modifier = Modifier,
    text: String,
    weight: Float,
    color: Color
) {
    Text(
        text = buildAnnotatedString {
            append(
                AnnotatedString(
                    text,
                    spanStyle = SpanStyle(
                        fontWeight = FontWeight.Bold,
                    )
                )
            )
        },
        modifier = modifier
            .border(dimensionResource(id = R.dimen.basic_border), MaterialTheme.colors.surface)
            .weight(weight)
            .padding(dimensionResource(id = R.dimen.dimen_8dp)),
        color = color
    )
}

@Composable
private fun EffectSection(
    modifier: Modifier = Modifier,
    effect: String,
    shortEffect: String,
    color: Color
) {
    Text(
        text = stringResource(id = R.string.effect_label),
        fontSize = dimensionResource(id = R.dimen.dimen_24dp).value.sp,
        fontWeight = FontWeight.Bold
    )
    Text(
        text = effect,
        fontSize = dimensionResource(id = R.dimen.dimen_16dp).value.sp,
        maxLines = 10,
        overflow = TextOverflow.Ellipsis
    )
    Spacer(modifier = modifier.height(dimensionResource(id = R.dimen.dimen_4dp)))
    Text(
        text = shortEffect,
        fontSize = dimensionResource(id = R.dimen.dimen_16dp).value.sp,
        maxLines = 10,
        overflow = TextOverflow.Ellipsis
    )
    SpacedDivider(modifier = modifier, color = color)
    Text(
        text = stringResource(id = R.string.feature_list_per_game_label),
        fontSize = dimensionResource(id = R.dimen.font_size_medium).value.sp,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = modifier.height(dimensionResource(id = R.dimen.dimen_4dp)))
}

private fun getFilteredEffectEntries(list: List<EffectEntryX>): List<EffectEntryX> {

    return list.filter {
        it.language.name.contains(FILTERLANGUAGE)
    }
}

private fun getFilteredFlavorEntries(list: List<FlavorTextEntry>): List<FlavorTextEntry> {

    return list.filter {
        it.language.name.contains(FILTERLANGUAGE)
    }
}


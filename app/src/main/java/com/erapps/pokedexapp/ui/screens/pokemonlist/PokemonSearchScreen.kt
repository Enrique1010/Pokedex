package com.erapps.pokedexapp.ui.screens.pokemonlist

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.erapps.pokedexapp.R
import com.erapps.pokedexapp.data.api.models.ShortPokemon
import com.erapps.pokedexapp.ui.screens.getNetworkStatus
import com.erapps.pokedexapp.ui.shared.*
import com.erapps.pokedexapp.utils.TestTags.LISTOFPOKEMONS
import com.erapps.pokedexapp.utils.TestTags.SEARCHBARICONBUTTON
import com.erapps.pokedexapp.utils.TestTags.SEARCHBARTEXTFIELD
import com.erapps.pokedexapp.utils.TestTags.TRAILINGICONBUTTON
import com.erapps.pokedexapp.utils.getIdFromUrl

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PokemonListScreen(
    viewModel: PokemonSearchViewModel = hiltViewModel(),
    onPokemonClick: (String) -> Unit
) {

    val uiState = viewModel.uiState.value
    val isEmptyList = viewModel.isEmptyList.value
    val text = remember { viewModel.text }
    val focused = remember { viewModel.focused }
    val status = getNetworkStatus()
    val context = LocalContext.current
    val scrollState = rememberLazyGridState()

    Scaffold(
        topBar = {
            SearchBar(
                query = text,
                focused = focused,
                onSearchClick = {
                    if (text.value.isNotEmpty()) {
                        viewModel.filterPokemonsByName(text.value)
                    }
                },
                onBack = {
                    text.value = ""
                    viewModel.filterPokemonsByName("")
                }
            )
        }
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(id = R.dimen.dimen_8dp)),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (uiState) {
                UiState.Empty -> {
                    ScreenWithMessage(message = R.string.empty_text)
                }
                is UiState.Error -> {
                    ErrorScreen(
                        errorMessage = uiState.errorMessage,
                        errorStringResource = uiState.errorStringResource
                    )
                }
                is UiState.Success<*> -> {
                    @Suppress("UNCHECKED_CAST")
                    ListOfPokemons(
                        list = uiState.data as List<ShortPokemon>,
                        onCardClick = { id ->
                            //only can go to details if internet is available
                            if (status) {
                                onPokemonClick(id)
                                return@ListOfPokemons
                            }
                            Toast.makeText(
                                context,
                                context.getString(R.string.cant_see_details_without_internet_text),
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        scrollState = scrollState
                    )
                }
                is UiState.Loading -> {
                    LoadingScreen()
                }
                else -> {
                    when {
                        !status && isEmptyList -> {
                            ScreenWithMessage(message = R.string.search_something_wrong) {
                                viewModel.filterPokemonsByName("")
                            }
                        }
                        status && isEmptyList -> {
                            ScreenWithMessage(message = R.string.search_something_wrong) {
                                viewModel.filterPokemonsByName("")
                            }
                        }
                        else -> {
                            LoadingScreen()
                        }
                    }
                }
            }
        }
    }

}

@OptIn(ExperimentalComposeUiApi::class)
@ExperimentalAnimationApi
@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    query: MutableState<String>,
    onSearchClick: () -> Unit,
    onBack: () -> Unit,
    focused: MutableState<Boolean>
) {

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(dimensionResource(id = R.dimen.dimen_8dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {

        AnimatedVisibility(visible = focused.value) {
            // Back button
            IconButton(
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.dimen_4dp))
                    .testTag(SEARCHBARICONBUTTON),
                onClick = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                    onBack()
                }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            }
        }

        CustomTextField(
            focused = focused,
            value = query,
            onSearchClick = {
                onSearchClick()
                keyboardController?.hide()
            }
        )
    }
}

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    focused: MutableState<Boolean>,
    value: MutableState<String>,
    onSearchClick: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    TextField(
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { focused.value = it.isFocused }
            .focusRequester(focusRequester)
            .testTag(SEARCHBARTEXTFIELD),
        value = value.value,
        onValueChange = { value.value = it },
        placeholder = {
            Text(
                text = stringResource(id = R.string.search_textfield_hint),
                color = MaterialTheme.colors.onBackground
            )
        },
        singleLine = true,
        trailingIcon = {
            if (focused.value) {
                IconButton(
                    modifier = modifier.testTag(TRAILINGICONBUTTON),
                    onClick = onSearchClick
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        tint = MaterialTheme.colors.primary,
                        contentDescription = null
                    )
                }
            }
        },
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.rounded_corner_shape_basic)),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions { onSearchClick() },
        colors = TextFieldDefaults.textFieldColors(
            textColor = MaterialTheme.colors.onBackground,
            disabledTextColor = Color.Transparent,
            backgroundColor = MaterialTheme.colors.onSurface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun ListOfPokemons(
    modifier: Modifier = Modifier,
    list: List<ShortPokemon>,
    onCardClick: (String) -> Unit,
    scrollState: LazyGridState
) {

    LazyVerticalGrid(
        modifier = modifier.testTag(LISTOFPOKEMONS),
        columns = GridCells.Fixed(2),
        state = scrollState
    ) {
        items(list) { pokemon ->
            ListOfPokemonsItem(pokemon = pokemon) { onCardClick(pokemon.name) }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ListOfPokemonsItem(
    modifier: Modifier = Modifier,
    pokemon: ShortPokemon,
    onCardClick: () -> Unit
) {
    val defaultColor = MaterialTheme.colors.primary
    val cardMainColor = remember { mutableStateOf(defaultColor) }

    Card(
        modifier = modifier
            .padding(dimensionResource(id = R.dimen.dimen_4dp))
            .wrapContentSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        cardMainColor.value,
                        MaterialTheme.colors.surface
                    )
                )
            ),
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.rounded_corner_shape_basic)),
        elevation = dimensionResource(id = R.dimen.card_elevation),
        onClick = onCardClick
    ) {
        Column(
            modifier = modifier.background(
                Brush.verticalGradient(
                    listOf(
                        cardMainColor.value,
                        MaterialTheme.colors.surface
                    )
                )
            )
        ) {
            ImageSection(
                imageUrl = getPokemonImage(pokemon.url.getIdFromUrl().toInt()),
                cardMainColor = cardMainColor
            )
            TitleSection(pokemon = pokemon)
        }
    }
}

@Composable
private fun TitleSection(
    modifier: Modifier = Modifier,
    pokemon: ShortPokemon
) {
    Text(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = dimensionResource(id = R.dimen.dimen_4dp)),
        text = pokemon.name.capitalize(Locale.current),
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colors.onBackground,
        textAlign = TextAlign.Center,
        fontSize = dimensionResource(id = R.dimen.dimen_24dp).value.sp,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun ImageSection(
    modifier: Modifier = Modifier,
    imageUrl: String,
    cardMainColor: MutableState<Color>
) {
    val context = LocalContext.current

    SubcomposeAsyncImage(
        modifier = modifier
            .fillMaxWidth()
            .size(
                dimensionResource(id = R.dimen.card_image_width),
                dimensionResource(id = R.dimen.card_image_height)
            ),
        model = ImageRequest.Builder(context)
            .data(imageUrl)
            .placeholder(R.drawable.pokemon_face)
            .error(R.drawable.pokemon_face)
            .crossfade(true)
            .build(),
        contentDescription = null,
        alignment = Alignment.TopCenter,
        loading = { LinearProgressIndicator() },
        onSuccess = { painter ->
            getImageDominantColor(cardMainColor, painter.result.drawable)
        }
    )
}
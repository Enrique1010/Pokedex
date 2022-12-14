package com.erapps.pokedexapp.ui.screens.details.encounters

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erapps.pokedexapp.data.source.PokemonDetailsRepository
import com.erapps.pokedexapp.ui.shared.UiState
import com.erapps.pokedexapp.ui.shared.mapResultToUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject

@HiltViewModel
class EncountersViewModel @Inject constructor(
    private val repository: PokemonDetailsRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val _uiState = mutableStateOf<UiState?>(null)
    val uiState: State<UiState?> = _uiState

    val backGroundColor = mutableStateOf(Color.Transparent)

    init {
        savedStateHandle.get<String>("encountersUrl")?.let { url ->
            val decodeUrl = URLDecoder.decode(url, StandardCharsets.UTF_8.toString())
            getEncounters(decodeUrl)
        }
        savedStateHandle.get<String>("encountersColorRGB")?.let {
            backGroundColor.value = Color(it.toInt())
        }
    }

    private fun getEncounters(url: String) = viewModelScope.launch {
        repository.getEncounters(url).collect { result ->
            mapResultToUiState(result, _uiState) { response ->
                if (response.isEmpty()) {
                    _uiState.value = UiState.Empty
                    return@mapResultToUiState
                }

                _uiState.value = UiState.Success(response)
            }
        }
    }
}
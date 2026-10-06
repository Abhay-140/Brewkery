package com.abhay.brewkery.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhay.brewkery.data.model.BrewkeryResponse
import com.abhay.brewkery.data.repository.BrewkeryRepository
import com.abhay.brewkery.data.repository.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MenuViewModel(
    private val repository: BrewkeryRepository = BrewkeryRepository()
) : ViewModel() {

    private val _menuState = MutableStateFlow<UiState<BrewkeryResponse>>(UiState.Loading)
    val menuState: StateFlow<UiState<BrewkeryResponse>> = _menuState.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    val selectedCategoryId: StateFlow<String?> = _selectedCategoryId.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadMenu()
    }

    fun loadMenu() {
        viewModelScope.launch {
            _menuState.value = UiState.Loading
            repository.getMenu()
                .onSuccess { response ->
                    _menuState.value = UiState.Success(response)
                }
                .onFailure { error ->
                    _menuState.value = UiState.Error(
                        error.localizedMessage ?: "Failed to load Brewkery menu. Please check your connection."
                    )
                }
        }
    }

    fun selectCategory(categoryId: String?) {
        _selectedCategoryId.value = categoryId
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
}

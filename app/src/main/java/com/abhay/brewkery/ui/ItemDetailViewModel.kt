package com.abhay.brewkery.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhay.brewkery.data.model.MenuItem
import com.abhay.brewkery.data.model.MilkOption
import com.abhay.brewkery.data.model.SizeOption
import com.abhay.brewkery.data.repository.BrewkeryRepository
import com.abhay.brewkery.data.repository.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ItemDetailViewModel(
    private val repository: BrewkeryRepository = BrewkeryRepository()
) : ViewModel() {

    private val _itemState = MutableStateFlow<UiState<MenuItem>>(UiState.Loading)
    val itemState: StateFlow<UiState<MenuItem>> = _itemState.asStateFlow()

    private val _selectedSize = MutableStateFlow<SizeOption?>(null)
    val selectedSize: StateFlow<SizeOption?> = _selectedSize.asStateFlow()

    private val _selectedMilk = MutableStateFlow<MilkOption?>(null)
    val selectedMilk: StateFlow<MilkOption?> = _selectedMilk.asStateFlow()

    private val _selectedSugar = MutableStateFlow<String?>(null)
    val selectedSugar: StateFlow<String?> = _selectedSugar.asStateFlow()

    private val _quantity = MutableStateFlow(1)
    val quantity: StateFlow<Int> = _quantity.asStateFlow()

    fun loadItem(itemId: Int, fallbackItem: MenuItem? = null) {
        viewModelScope.launch {
            if (fallbackItem != null) {
                _itemState.value = UiState.Success(fallbackItem)
                setupDefaults(fallbackItem)
            } else {
                _itemState.value = UiState.Loading
            }

            repository.getItemDetail(itemId)
                .onSuccess { item ->
                    _itemState.value = UiState.Success(item)
                    setupDefaults(item)
                }
                .onFailure { error ->
                    if (fallbackItem == null) {
                        _itemState.value = UiState.Error(
                            error.localizedMessage ?: "Failed to load item details"
                        )
                    }
                }
        }
    }

    private fun setupDefaults(item: MenuItem) {
        val customizations = item.customizations
        if (customizations != null) {
            if (_selectedSize.value == null && customizations.sizes.isNotEmpty()) {
                _selectedSize.value = customizations.sizes.first()
            }
            if (_selectedMilk.value == null && customizations.milkOptions.isNotEmpty()) {
                _selectedMilk.value = customizations.milkOptions.first()
            }
            if (_selectedSugar.value == null && customizations.sugarLevels.isNotEmpty()) {
                _selectedSugar.value = customizations.sugarLevels.first()
            }
        }
    }

    fun selectSize(size: SizeOption) {
        _selectedSize.value = size
    }

    fun selectMilk(milk: MilkOption) {
        _selectedMilk.value = milk
    }

    fun selectSugar(sugar: String) {
        _selectedSugar.value = sugar
    }

    fun increaseQuantity() {
        _quantity.value += 1
    }

    fun decreaseQuantity() {
        if (_quantity.value > 1) {
            _quantity.value -= 1
        }
    }

    fun calculateTotalPrice(basePrice: Double): Double {
        val sizeExtra = _selectedSize.value?.extraPrice ?: 0.0
        val milkExtra = _selectedMilk.value?.extraPrice ?: 0.0
        val unitPrice = basePrice + sizeExtra + milkExtra
        return unitPrice * _quantity.value
    }
}

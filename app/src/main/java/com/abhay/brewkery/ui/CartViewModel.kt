package com.abhay.brewkery.ui

import androidx.lifecycle.ViewModel
import com.abhay.brewkery.data.model.CartItem
import com.abhay.brewkery.data.model.CartSummary
import com.abhay.brewkery.data.model.MenuItem
import com.abhay.brewkery.data.model.MilkOption
import com.abhay.brewkery.data.model.OrderInfo
import com.abhay.brewkery.data.model.OrderStatus
import com.abhay.brewkery.data.model.SizeOption
import com.abhay.brewkery.data.model.StoreMeta
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CartViewModel : ViewModel() {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _activeOrder = MutableStateFlow<OrderInfo?>(null)
    val activeOrder: StateFlow<OrderInfo?> = _activeOrder.asStateFlow()

    fun addToCart(
        menuItem: MenuItem,
        size: SizeOption?,
        milk: MilkOption?,
        sugar: String?,
        quantity: Int = 1
    ) {
        val currentList = _cartItems.value.toMutableList()
        // Check if an identical customized item exists in cart
        val existingIndex = currentList.indexOfFirst {
            it.menuItem.id == menuItem.id &&
                    it.selectedSize?.id == size?.id &&
                    it.selectedMilk?.id == milk?.id &&
                    it.selectedSugar == sugar
        }

        if (existingIndex != -1) {
            val existingItem = currentList[existingIndex]
            currentList[existingIndex] = existingItem.copy(quantity = existingItem.quantity + quantity)
        } else {
            currentList.add(
                CartItem(
                    menuItem = menuItem,
                    selectedSize = size,
                    selectedMilk = milk,
                    selectedSugar = sugar,
                    quantity = quantity
                )
            )
        }
        _cartItems.value = currentList
    }

    fun updateQuantity(cartItemId: String, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeFromCart(cartItemId)
            return
        }
        _cartItems.value = _cartItems.value.map { item ->
            if (item.id == cartItemId) {
                item.copy(quantity = newQuantity)
            } else {
                item
            }
        }
    }

    fun removeFromCart(cartItemId: String) {
        _cartItems.value = _cartItems.value.filter { it.id != cartItemId }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    fun calculateSummary(storeMeta: StoreMeta? = null): CartSummary {
        val subtotal = _cartItems.value.sumOf { it.totalPrice }
        if (subtotal == 0.0) {
            return CartSummary()
        }
        val deliveryFee = storeMeta?.deliveryFee ?: 2.50
        val taxRate = (storeMeta?.taxRatePercent ?: 8.0) / 100.0
        val taxAmount = subtotal * taxRate
        val total = subtotal + deliveryFee + taxAmount

        return CartSummary(
            subtotal = subtotal,
            deliveryFee = deliveryFee,
            taxAmount = taxAmount,
            total = total
        )
    }

    fun placeOrder(storeMeta: StoreMeta? = null): OrderInfo? {
        val itemsToOrder = _cartItems.value
        if (itemsToOrder.isEmpty()) return null

        val summary = calculateSummary(storeMeta)
        val ticketNumber = (1000..9999).random()
        val order = OrderInfo(
            ticketId = "#BK-$ticketNumber",
            items = itemsToOrder,
            summary = summary,
            status = OrderStatus.PREPARING,
            estimatedDeliveryTime = storeMeta?.estimatedDeliveryTime ?: "20 - 30 mins"
        )

        _activeOrder.value = order
        clearCart()
        return order
    }

    fun cancelOrder() {
        _activeOrder.value = null
    }
}

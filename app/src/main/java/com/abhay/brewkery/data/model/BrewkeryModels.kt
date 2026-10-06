package com.abhay.brewkery.data.model

import com.google.gson.annotations.SerializedName

data class BrewkeryResponse(
    @SerializedName("meta") val meta: StoreMeta,
    @SerializedName("categories") val categories: List<Category>,
    @SerializedName("items") val items: List<MenuItem>
)

data class StoreMeta(
    @SerializedName("app") val app: String = "Brewkery",
    @SerializedName("version") val version: String = "1.0.0",
    @SerializedName("tagline") val tagline: String = "Artisanal Coffee & Fresh Oven Bakes",
    @SerializedName("currency") val currency: String = "USD",
    @SerializedName("currency_symbol") val currencySymbol: String = "$",
    @SerializedName("delivery_fee") val deliveryFee: Double = 2.50,
    @SerializedName("tax_rate_percent") val taxRatePercent: Double = 8.0,
    @SerializedName("estimated_delivery_time") val estimatedDeliveryTime: String = "20 - 30 mins"
)

data class Category(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("icon") val icon: String,
    @SerializedName("item_count") val itemCount: Int
)

data class SizeOption(
    @SerializedName("id") val id: String,
    @SerializedName("label") val label: String,
    @SerializedName("extra_price") val extraPrice: Double = 0.0
)

data class MilkOption(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("extra_price") val extraPrice: Double = 0.0
)

data class Customizations(
    @SerializedName("sizes") val sizes: List<SizeOption> = emptyList(),
    @SerializedName("sugar_levels") val sugarLevels: List<String> = emptyList(),
    @SerializedName("milk_options") val milkOptions: List<MilkOption> = emptyList()
)

data class MenuItem(
    @SerializedName("id") val id: Int,
    @SerializedName("category_id") val categoryId: String,
    @SerializedName("name") val name: String,
    @SerializedName("tagline") val tagline: String,
    @SerializedName("description") val description: String,
    @SerializedName("base_price") val basePrice: Double,
    @SerializedName("rating") val rating: Double,
    @SerializedName("review_count") val reviewCount: Int,
    @SerializedName("prep_time") val prepTime: String,
    @SerializedName("calories") val calories: Int,
    @SerializedName("image_url") val imageUrl: String,
    @SerializedName("badge") val badge: String? = null,
    @SerializedName("ingredients") val ingredients: List<String> = emptyList(),
    @SerializedName("customizations") val customizations: Customizations? = null
)

// Cart & Order Domain Models
data class CartItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val menuItem: MenuItem,
    val selectedSize: SizeOption? = null,
    val selectedMilk: MilkOption? = null,
    val selectedSugar: String? = null,
    val quantity: Int = 1
) {
    val unitPrice: Double
        get() = menuItem.basePrice + (selectedSize?.extraPrice ?: 0.0) + (selectedMilk?.extraPrice ?: 0.0)

    val totalPrice: Double
        get() = unitPrice * quantity
}

data class CartSummary(
    val subtotal: Double = 0.0,
    val deliveryFee: Double = 2.50,
    val taxAmount: Double = 0.0,
    val total: Double = 0.0
)

enum class OrderStatus {
    PREPARING,
    OUT_FOR_DELIVERY,
    DELIVERED
}

data class OrderInfo(
    val ticketId: String,
    val items: List<CartItem>,
    val summary: CartSummary,
    val status: OrderStatus = OrderStatus.PREPARING,
    val timestamp: Long = System.currentTimeMillis(),
    val estimatedDeliveryTime: String = "20 - 30 mins"
)

fun Double.formatPrice(symbol: String = "$"): String {
    return String.format("%s%.2f", symbol, this)
}

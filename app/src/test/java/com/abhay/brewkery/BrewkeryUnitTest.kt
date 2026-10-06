package com.abhay.brewkery

import com.abhay.brewkery.data.model.MenuItem
import com.abhay.brewkery.data.model.MilkOption
import com.abhay.brewkery.data.model.SizeOption
import com.abhay.brewkery.data.model.StoreMeta
import com.abhay.brewkery.ui.CartViewModel
import com.abhay.brewkery.ui.ItemDetailViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class BrewkeryUnitTest {

    private lateinit var cartViewModel: CartViewModel
    private lateinit var detailViewModel: ItemDetailViewModel

    private val sampleItem = MenuItem(
        id = 1,
        categoryId = "cat_hot_coffee",
        name = "Toasted Caramel Macchiato",
        tagline = "Double shot espresso with velvety oat milk",
        description = "Rich espresso roast layered with velvety steamed milk",
        basePrice = 4.85,
        rating = 4.9,
        reviewCount = 312,
        prepTime = "5-7 mins",
        calories = 240,
        imageUrl = "https://images.unsplash.com/photo-1572442388796-11668a67e53d",
        badge = "BESTSELLER"
    )

    private val sizeGrande = SizeOption(id = "size_grande", label = "Grande (12 oz)", extraPrice = 0.65)
    private val milkAlmond = MilkOption(id = "milk_almond", name = "Roasted Almond Milk", extraPrice = 0.50)

    @Before
    fun setup() {
        cartViewModel = CartViewModel()
        detailViewModel = ItemDetailViewModel()
    }

    @Test
    fun `test item unit price calculation with customizations`() {
        // Base: 4.85 + Grande: 0.65 + Almond Milk: 0.50 = 6.00
        detailViewModel.selectSize(sizeGrande)
        detailViewModel.selectMilk(milkAlmond)

        val total = detailViewModel.calculateTotalPrice(sampleItem.basePrice)
        assertEquals(6.00, total, 0.001)
    }

    @Test
    fun `test adding item to cart updates list and summary`() {
        cartViewModel.addToCart(
            menuItem = sampleItem,
            size = sizeGrande,
            milk = milkAlmond,
            sugar = "50% Mild",
            quantity = 2
        )

        val cartItems = cartViewModel.cartItems.value
        assertEquals(1, cartItems.size)
        assertEquals(2, cartItems.first().quantity)
        assertEquals(12.00, cartItems.first().totalPrice, 0.001)

        val meta = StoreMeta(deliveryFee = 2.50, taxRatePercent = 8.0)
        val summary = cartViewModel.calculateSummary(meta)

        // Subtotal = 12.00
        // Delivery = 2.50
        // Tax = 12.00 * 0.08 = 0.96
        // Total = 15.46
        assertEquals(12.00, summary.subtotal, 0.001)
        assertEquals(2.50, summary.deliveryFee, 0.001)
        assertEquals(0.96, summary.taxAmount, 0.001)
        assertEquals(15.46, summary.total, 0.001)
    }

    @Test
    fun `test placing order generates ticket and clears cart`() {
        cartViewModel.addToCart(
            menuItem = sampleItem,
            size = sizeGrande,
            milk = milkAlmond,
            sugar = "100% Standard",
            quantity = 1
        )

        val meta = StoreMeta()
        val order = cartViewModel.placeOrder(meta)

        assertNotNull(order)
        assertTrue(order!!.ticketId.startsWith("#BK-"))
        assertTrue(cartViewModel.cartItems.value.isEmpty())
        assertNotNull(cartViewModel.activeOrder.value)
    }
}

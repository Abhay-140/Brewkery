package com.abhay.brewkery.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.abhay.brewkery.data.model.CartItem
import com.abhay.brewkery.data.model.CartSummary
import com.abhay.brewkery.data.model.StoreMeta
import com.abhay.brewkery.data.model.formatPrice
import com.abhay.brewkery.ui.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    cartVm: CartViewModel,
    storeMeta: StoreMeta?,
    onBackClick: () -> Unit,
    onPlaceOrderSuccess: () -> Unit,
    onBrowseMenuClick: () -> Unit
) {
    val cartItems by cartVm.cartItems.collectAsStateWithLifecycle()
    val summary = cartVm.calculateSummary(storeMeta)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Cart 🛒", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (cartItems.isNotEmpty()) {
                        IconButton(onClick = { cartVm.clearCart() }) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Clear Cart")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (cartItems.isEmpty()) {
                EmptyCartView(onBrowseMenuClick = onBrowseMenuClick)
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(cartItems, key = { it.id }) { item ->
                            CartItemRow(
                                item = item,
                                onDecrease = { cartVm.updateQuantity(item.id, item.quantity - 1) },
                                onIncrease = { cartVm.updateQuantity(item.id, item.quantity + 1) },
                                onRemove = { cartVm.removeFromCart(item.id) }
                            )
                        }

                        item {
                            OrderSummaryCard(summary = summary, storeMeta = storeMeta)
                        }
                    }

                    // Place Order Sticky Bar
                    Surface(
                        shadowElevation = 8.dp,
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Total Amount",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = summary.total.formatPrice(),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Button(
                                onClick = {
                                    val order = cartVm.placeOrder(storeMeta)
                                    if (order != null) {
                                        onPlaceOrderSuccess()
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .height(48.dp)
                                    .padding(start = 16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text(
                                    text = "Place Order 🎉",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItem,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.menuItem.imageUrl,
                contentDescription = item.menuItem.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = item.menuItem.name,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.DeleteOutline,
                            contentDescription = "Remove",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Customizations summary
                val optionsList = listOfNotNull(
                    item.selectedSize?.label,
                    item.selectedMilk?.name,
                    item.selectedSugar
                )
                if (optionsList.isNotEmpty()) {
                    Text(
                        text = optionsList.joinToString(" • "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.totalPrice.formatPrice(),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleSmall
                    )

                    // Quantity Stepper
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        IconButton(onClick = onDecrease, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                        }
                        Text(
                            text = "${item.quantity}",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp),
                            style = MaterialTheme.typography.bodySmall
                        )
                        IconButton(onClick = onIncrease, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderSummaryCard(summary: CartSummary, storeMeta: StoreMeta?) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Bill Details",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            SummaryRow(label = "Item Subtotal", value = summary.subtotal.formatPrice())
            Spacer(modifier = Modifier.height(6.dp))

            SummaryRow(
                label = "Delivery Fee",
                value = summary.deliveryFee.formatPrice()
            )
            Spacer(modifier = Modifier.height(6.dp))

            SummaryRow(
                label = "Taxes & Charges (${storeMeta?.taxRatePercent ?: 8.0}%)",
                value = summary.taxAmount.formatPrice()
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            Spacer(modifier = Modifier.height(12.dp))

            SummaryRow(
                label = "To Pay",
                value = summary.total.formatPrice(),
                isBold = true
            )
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String, isBold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = if (isBold) MaterialTheme.typography.titleSmall else MaterialTheme.colorScheme.onSurfaceVariant.let {
                MaterialTheme.typography.bodyMedium
            },
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = if (isBold) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = if (isBold) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            color = if (isBold) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun EmptyCartView(onBrowseMenuClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Your Cart is Empty",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Looks like you haven't added any fresh coffee or oven bakes yet.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onBrowseMenuClick,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Browse Brewkery Menu ☕")
        }
    }
}

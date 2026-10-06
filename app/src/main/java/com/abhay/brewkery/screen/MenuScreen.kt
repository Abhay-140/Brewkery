package com.abhay.brewkery.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.abhay.brewkery.data.model.BrewkeryResponse
import com.abhay.brewkery.data.model.MenuItem
import com.abhay.brewkery.data.model.OrderInfo
import com.abhay.brewkery.data.model.StoreMeta
import com.abhay.brewkery.data.model.formatPrice
import com.abhay.brewkery.data.repository.UiState
import com.abhay.brewkery.ui.CartViewModel
import com.abhay.brewkery.ui.MenuViewModel
import com.abhay.brewkery.ui.theme.GreenBadge
import com.abhay.brewkery.ui.theme.GreenBadgeContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    menuVm: MenuViewModel,
    cartVm: CartViewModel,
    onItemClick: (MenuItem) -> Unit,
    onCartClick: () -> Unit,
    onOrderClick: () -> Unit
) {
    val menuState by menuVm.menuState.collectAsStateWithLifecycle()
    val selectedCategory by menuVm.selectedCategoryId.collectAsStateWithLifecycle()
    val searchQuery by menuVm.searchQuery.collectAsStateWithLifecycle()
    val cartItems by cartVm.cartItems.collectAsStateWithLifecycle()
    val activeOrder by cartVm.activeOrder.collectAsStateWithLifecycle()

    val totalCartCount = cartItems.sumOf { it.quantity }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Brewkery ☕",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "Fresh Coffee & Bakes",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onCartClick) {
                        BadgedBox(
                            badge = {
                                if (totalCartCount > 0) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.secondary,
                                        contentColor = MaterialTheme.colorScheme.onSecondary
                                    ) {
                                        Text(text = "$totalCartCount")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Cart"
                            )
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
            when (val state = menuState) {
                is UiState.Loading -> LoadingView()
                is UiState.Error -> ErrorView(
                    message = state.message,
                    onRetry = { menuVm.loadMenu() }
                )
                is UiState.Success -> MenuContent(
                    response = state.data,
                    selectedCategory = selectedCategory,
                    searchQuery = searchQuery,
                    activeOrder = activeOrder,
                    onCategorySelect = { menuVm.selectCategory(it) },
                    onSearchQueryChange = { menuVm.setSearchQuery(it) },
                    onItemClick = onItemClick,
                    onOrderClick = onOrderClick
                )
            }
        }
    }
}

@Composable
fun MenuContent(
    response: BrewkeryResponse,
    selectedCategory: String?,
    searchQuery: String,
    activeOrder: OrderInfo?,
    onCategorySelect: (String?) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onItemClick: (MenuItem) -> Unit,
    onOrderClick: () -> Unit
) {
    val filteredItems = response.items.filter { item ->
        val matchesCategory = selectedCategory == null || item.categoryId == selectedCategory
        val matchesSearch = searchQuery.isBlank() ||
                item.name.contains(searchQuery, ignoreCase = true) ||
                item.description.contains(searchQuery, ignoreCase = true) ||
                item.tagline.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Store Announcement Banner
        item {
            StoreBanner(meta = response.meta)
        }

        // Active Order Banner (If Placed)
        activeOrder?.let { order ->
            item {
                ActiveOrderCard(order = order, onClick = onOrderClick)
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search coffee, drinks & bakes...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )
        }

        // Category Filter Row
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { onCategorySelect(null) },
                        label = { Text("All (${response.items.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
                items(response.categories, key = { it.id }) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat.id,
                        onClick = { onCategorySelect(cat.id) },
                        label = { Text("${cat.icon} ${cat.name}") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }
        }

        // Menu Items List
        if (filteredItems.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No items found matching '$searchQuery'",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(filteredItems, key = { it.id }) { item ->
                MenuItemRow(
                    item = item,
                    currencySymbol = response.meta.currencySymbol,
                    onClick = { onItemClick(item) }
                )
            }
        }
    }
}

@Composable
fun StoreBanner(meta: StoreMeta) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "☕", fontSize = 32.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = meta.tagline,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "🛵 Delivery ${meta.estimatedDeliveryTime} • ${meta.deliveryFee.formatPrice(meta.currencySymbol)} Fee",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun ActiveOrderCard(order: OrderInfo, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "⏱️", fontSize = 28.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Active Order ${order.ticketId}",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = GreenBadgeContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = order.status.name,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = GreenBadge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(
                    text = "${order.items.sumOf { it.quantity }} items • Estimated ${order.estimatedDeliveryTime}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                )
            }
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Track Order",
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
fun MenuItemRow(
    item: MenuItem,
    currencySymbol: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.name,
                    modifier = Modifier
                        .size(96.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                item.badge?.let { badgeText ->
                    Surface(
                        color = GreenBadgeContainer,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        Text(
                            text = badgeText,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = GreenBadge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = item.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = item.tagline,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Star,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${item.rating} (${item.reviewCount})",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = item.basePrice.formatPrice(currencySymbol),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun LoadingView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Brewing your menu...",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ErrorView(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "☕ Oops!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Red,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRetry) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Retry Network Call")
        }
    }
}

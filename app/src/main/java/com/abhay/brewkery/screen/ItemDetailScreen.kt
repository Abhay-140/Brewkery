package com.abhay.brewkery.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.abhay.brewkery.data.model.MenuItem
import com.abhay.brewkery.data.model.MilkOption
import com.abhay.brewkery.data.model.SizeOption
import com.abhay.brewkery.data.model.formatPrice
import com.abhay.brewkery.data.repository.UiState
import com.abhay.brewkery.ui.CartViewModel
import com.abhay.brewkery.ui.ItemDetailViewModel
import com.abhay.brewkery.ui.theme.GreenBadge
import com.abhay.brewkery.ui.theme.GreenBadgeContainer
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreen(
    itemId: Int,
    fallbackItem: MenuItem?,
    detailVm: ItemDetailViewModel,
    cartVm: CartViewModel,
    onBackClick: () -> Unit,
    onCartClick: () -> Unit
) {
    val itemState by detailVm.itemState.collectAsStateWithLifecycle()
    val selectedSize by detailVm.selectedSize.collectAsStateWithLifecycle()
    val selectedMilk by detailVm.selectedMilk.collectAsStateWithLifecycle()
    val selectedSugar by detailVm.selectedSugar.collectAsStateWithLifecycle()
    val quantity by detailVm.quantity.collectAsStateWithLifecycle()
    val cartItems by cartVm.cartItems.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(itemId) {
        detailVm.loadItem(itemId, fallbackItem)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Item Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onCartClick) {
                        BadgedBox(
                            badge = {
                                val totalCartCount = cartItems.sumOf { it.quantity }
                                if (totalCartCount > 0) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.secondary,
                                        contentColor = MaterialTheme.colorScheme.onSecondary
                                    ) {
                                        Text("$totalCartCount")
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (val state = itemState) {
                is UiState.Loading -> LoadingView()
                is UiState.Error -> ErrorView(
                    message = state.message,
                    onRetry = { detailVm.loadItem(itemId, fallbackItem) }
                )
                is UiState.Success -> {
                    val item = state.data
                    val totalPrice = detailVm.calculateTotalPrice(item.basePrice)

                    Column(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState())
                        ) {
                            // Hero Image Banner
                            AsyncImage(
                                model = item.imageUrl,
                                contentDescription = item.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(240.dp),
                                contentScale = ContentScale.Crop
                            )

                            Column(modifier = Modifier.padding(16.dp)) {
                                // Badge & Rating Header
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    item.badge?.let { badgeText ->
                                        Surface(
                                            color = GreenBadgeContainer,
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = badgeText,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                style = MaterialTheme.typography.labelMedium,
                                                color = GreenBadge,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    } ?: Spacer(modifier = Modifier.width(1.dp))

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Outlined.Star,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${item.rating} (${item.reviewCount} reviews)",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Title & Price
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.name,
                                            style = MaterialTheme.typography.headlineSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = item.tagline,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = item.basePrice.formatPrice(),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Meta Chips (Prep Time & Calories)
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    MetaChip(text = "⏱️ ${item.prepTime}")
                                    MetaChip(text = "🔥 ${item.calories} kcal")
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Description
                                Text(
                                    text = item.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                // Ingredients Section
                                if (item.ingredients.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(20.dp))
                                    IngredientsSection(ingredients = item.ingredients)
                                }

                                // Customizations Section
                                item.customizations?.let { custom ->
                                    Spacer(modifier = Modifier.height(20.dp))

                                    // Size Options
                                    if (custom.sizes.isNotEmpty()) {
                                        SizeCustomizationSection(
                                            sizes = custom.sizes,
                                            selectedSize = selectedSize,
                                            onSelectSize = { detailVm.selectSize(it) }
                                        )
                                        Spacer(modifier = Modifier.height(20.dp))
                                    }

                                    // Milk Options
                                    if (custom.milkOptions.isNotEmpty()) {
                                        MilkCustomizationSection(
                                            milkOptions = custom.milkOptions,
                                            selectedMilk = selectedMilk,
                                            onSelectMilk = { detailVm.selectMilk(it) }
                                        )
                                        Spacer(modifier = Modifier.height(20.dp))
                                    }

                                    // Sugar Level Options
                                    if (custom.sugarLevels.isNotEmpty()) {
                                        SugarCustomizationSection(
                                            sugarLevels = custom.sugarLevels,
                                            selectedSugar = selectedSugar,
                                            onSelectSugar = { detailVm.selectSugar(it) }
                                        )
                                        Spacer(modifier = Modifier.height(20.dp))
                                    }
                                }
                            }
                        }

                        // Bottom Sticky Action Bar
                        BottomCartActionBar(
                            quantity = quantity,
                            totalPrice = totalPrice,
                            onDecrease = { detailVm.decreaseQuantity() },
                            onIncrease = { detailVm.increaseQuantity() },
                            onAddToCart = {
                                cartVm.addToCart(
                                    menuItem = item,
                                    size = selectedSize,
                                    milk = selectedMilk,
                                    sugar = selectedSugar,
                                    quantity = quantity
                                )
                                scope.launch {
                                    snackbarHostState.showSnackbar("Added ${item.name} to cart 🛒")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MetaChip(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun IngredientsSection(ingredients: List<String>) {
    Column {
        Text(
            text = "Ingredients",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ingredients.forEach { ingredient ->
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "🌿 $ingredient",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
fun SizeCustomizationSection(
    sizes: List<SizeOption>,
    selectedSize: SizeOption?,
    onSelectSize: (SizeOption) -> Unit
) {
    Column {
        Text(
            text = "Select Size",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            sizes.forEach { size ->
                val isSelected = selectedSize?.id == size.id
                Card(
                    onClick = { onSelectSize(size) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                    ),
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = size.label,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (size.extraPrice > 0) "+${size.extraPrice.formatPrice()}" else "Included",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MilkCustomizationSection(
    milkOptions: List<MilkOption>,
    selectedMilk: MilkOption?,
    onSelectMilk: (MilkOption) -> Unit
) {
    Column {
        Text(
            text = "Milk Preference",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            milkOptions.forEach { milk ->
                val isSelected = selectedMilk?.id == milk.id
                Card(
                    onClick = { onSelectMilk(milk) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                    ),
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isSelected) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Text(
                                text = milk.name,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Text(
                            text = if (milk.extraPrice > 0) "+${milk.extraPrice.formatPrice()}" else "Free",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SugarCustomizationSection(
    sugarLevels: List<String>,
    selectedSugar: String?,
    onSelectSugar: (String) -> Unit
) {
    Column {
        Text(
            text = "Sugar / Sweetness Level",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            sugarLevels.forEach { level ->
                val isSelected = selectedSugar == level
                if (isSelected) {
                    Button(
                        onClick = { onSelectSugar(level) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = level,
                            style = MaterialTheme.typography.labelMedium,
                            fontSize = 12.sp
                        )
                    }
                } else {
                    OutlinedButton(
                        onClick = { onSelectSugar(level) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = level,
                            style = MaterialTheme.typography.labelMedium,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BottomCartActionBar(
    quantity: Int,
    totalPrice: Double,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    onAddToCart: () -> Unit
) {
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
            // Quantity Stepper
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                IconButton(onClick = onDecrease, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease")
                }
                Text(
                    text = "$quantity",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = onIncrease, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Add, contentDescription = "Increase")
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Add to Cart Button
            Button(
                onClick = onAddToCart,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Add to Cart • ${totalPrice.formatPrice()}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abhay.brewkery.data.model.OrderInfo
import com.abhay.brewkery.data.model.OrderStatus
import com.abhay.brewkery.data.model.formatPrice
import com.abhay.brewkery.ui.CartViewModel
import com.abhay.brewkery.ui.theme.GreenBadge
import com.abhay.brewkery.ui.theme.GreenBadgeContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    cartVm: CartViewModel,
    onBackToMenuClick: () -> Unit
) {
    val activeOrder by cartVm.activeOrder.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order Status ⏱️", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackToMenuClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Menu")
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
            val order = activeOrder
            if (order == null) {
                NoActiveOrderView(onBackToMenuClick = onBackToMenuClick)
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        OrderHeaderCard(order = order)
                    }

                    item {
                        OrderStatusTimeline(status = order.status)
                    }

                    item {
                        OrderItemsSummaryCard(order = order)
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onBackToMenuClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Back to Home Menu ☕", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = { cartVm.cancelOrder() },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancel / Clear Order Tracking")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderHeaderCard(order: OrderInfo) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = GreenBadge
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Order Placed Successfully!",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Ticket ID: ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = order.ticketId,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Estimated Delivery: ${order.estimatedDeliveryTime}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun OrderStatusTimeline(status: OrderStatus) {
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
                text = "Live Order Progress",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            TimelineStep(
                title = "Order Confirmed",
                subtitle = "Brewkery received your order",
                isDone = true,
                isActive = false
            )

            TimelineDivider(isDone = true)

            TimelineStep(
                title = "Preparing in Kitchen ☕",
                subtitle = "Baristas & bakers are handcrafting your items",
                isDone = status == OrderStatus.PREPARING || status == OrderStatus.OUT_FOR_DELIVERY || status == OrderStatus.DELIVERED,
                isActive = status == OrderStatus.PREPARING
            )

            TimelineDivider(isDone = status == OrderStatus.OUT_FOR_DELIVERY || status == OrderStatus.DELIVERED)

            TimelineStep(
                title = "Out for Delivery 🛵",
                subtitle = "Rider is heading to your location",
                isDone = status == OrderStatus.OUT_FOR_DELIVERY || status == OrderStatus.DELIVERED,
                isActive = status == OrderStatus.OUT_FOR_DELIVERY
            )

            TimelineDivider(isDone = status == OrderStatus.DELIVERED)

            TimelineStep(
                title = "Delivered 🎉",
                subtitle = "Enjoy your fresh Brewkery bakes!",
                isDone = status == OrderStatus.DELIVERED,
                isActive = status == OrderStatus.DELIVERED
            )
        }
    }
}

@Composable
fun TimelineStep(
    title: String,
    subtitle: String,
    isDone: Boolean,
    isActive: Boolean
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(
                    color = when {
                        isActive -> MaterialTheme.colorScheme.secondary
                        isDone -> GreenBadge
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isDone) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.size(18.dp)
                )
            } else if (isActive) {
                Text("⏱️", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                fontWeight = if (isActive || isDone) FontWeight.Bold else FontWeight.Normal,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isActive || isDone) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun TimelineDivider(isDone: Boolean) {
    Box(
        modifier = Modifier
            .padding(start = 13.dp)
            .width(2.dp)
            .height(24.dp)
            .background(if (isDone) GreenBadge else MaterialTheme.colorScheme.surfaceVariant)
    )
}

@Composable
fun OrderItemsSummaryCard(order: OrderInfo) {
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
                text = "Order Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            order.items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${item.quantity}x ${item.menuItem.name}",
                            fontWeight = FontWeight.Medium,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        val opts = listOfNotNull(
                            item.selectedSize?.label,
                            item.selectedMilk?.name,
                            item.selectedSugar
                        )
                        if (opts.isNotEmpty()) {
                            Text(
                                text = opts.joinToString(" • "),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Text(
                        text = item.totalPrice.formatPrice(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            Spacer(modifier = Modifier.height(12.dp))

            SummaryRow(label = "Total Paid", value = order.summary.total.formatPrice(), isBold = true)
        }
    }
}

@Composable
fun NoActiveOrderView(onBackToMenuClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("☕", fontSize = 64.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No Active Order",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "You don't have an active order right now. Explore the menu and order something delicious!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onBackToMenuClick,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Browse Brewkery Menu")
        }
    }
}

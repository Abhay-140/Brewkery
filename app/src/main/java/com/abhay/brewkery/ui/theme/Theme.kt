package com.abhay.brewkery.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = CoffeePrimary,
    onPrimary = CoffeeOnPrimary,
    primaryContainer = CoffeePrimaryContainer,
    onPrimaryContainer = CoffeeOnPrimaryContainer,
    secondary = CaramelSecondary,
    onSecondary = CaramelOnSecondary,
    secondaryContainer = CaramelSecondaryContainer,
    onSecondaryContainer = CaramelOnSecondaryContainer,
    tertiary = CinnamonTertiary,
    background = CoffeeBackground,
    onBackground = CoffeeOnBackground,
    surface = CoffeeSurface,
    onSurface = CoffeeOnSurface,
    surfaceVariant = CoffeeSurfaceVariant,
    onSurfaceVariant = CoffeeOnSurfaceVariant
)

private val DarkColorScheme = darkColorScheme(
    primary = CaramelSecondary,
    onPrimary = CaramelOnSecondary,
    primaryContainer = CoffeePrimaryContainer,
    onPrimaryContainer = CoffeeOnPrimaryContainer,
    secondary = CoffeePrimary,
    onSecondary = CoffeeOnPrimary,
    background = CoffeeOnBackground,
    onBackground = CoffeeBackground,
    surface = CoffeeOnBackground,
    onSurface = CoffeeBackground
)

@Composable
fun BrewkeryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

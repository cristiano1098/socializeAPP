package com.example.cmu_g10.floatingActionButton

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Interface representing a subordinate floating action button (FAB) with icon and background tint colors.
 */
interface FabButtonSub {

    /**
     * The color for tinting the icon of the subordinate FAB.
     */
    val iconTint: Color

    /**
     * The background tint color for the subordinate FAB.
     */
    val backgroundTint: Color
}

/**
 * Implementation of the [FabButtonSub] interface.
 *
 * @property iconTint The color for tinting the icon of the subordinate FAB.
 * @property backgroundTint The background tint color for the subordinate FAB.
 */
private class FabButtonSubImpl(
    override val iconTint: Color,
    override val backgroundTint: Color
) : FabButtonSub

/**
 * Composable function to create an instance of [FabButtonSub].
 *
 * @param backgroundTint The background tint color for the subordinate FAB. Defaults to the primary color from MaterialTheme.
 * @param iconTint The color for tinting the icon of the subordinate FAB. Defaults to white.
 * @return An instance of [FabButtonSub].
 */
@Composable
fun FabButtonSub(
    backgroundTint: Color = MaterialTheme.colorScheme.primary,
    iconTint: Color = Color(0xFFffffff)
): FabButtonSub = FabButtonSubImpl(iconTint, backgroundTint)
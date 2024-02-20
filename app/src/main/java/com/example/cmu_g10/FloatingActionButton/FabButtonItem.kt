package com.example.cmu_g10.floatingActionButton

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Data class representing an item for a floating action button (FAB) menu.
 *
 * @property iconRes The ImageVector representing the icon for the FAB item.
 * @property label The label or text associated with the FAB item.
 */
data class FabButtonItem(
    val iconRes: ImageVector,
    val label: String
)

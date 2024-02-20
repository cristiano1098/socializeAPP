package com.example.cmu_g10.floatingActionButton

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Interface representing the main floating action button (FAB) with an icon and optional rotation.
 */
interface FabButtonMain {

    /**
     * The ImageVector representing the icon for the main FAB.
     */
    val iconRes: ImageVector

    /**
     * The optional rotation angle for the main FAB icon.
     */
    val iconRotate: Float?
}

/**
 * Implementation of the [FabButtonMain] interface.
 *
 * @property iconRes The ImageVector representing the icon for the main FAB.
 * @property iconRotate The optional rotation angle for the main FAB icon.
 */
private class FabButtonMainImpl(
    override val iconRes: ImageVector,
    override val iconRotate: Float?
) : FabButtonMain

/**
 * Factory function to create an instance of [FabButtonMain].
 *
 * @param iconRes The ImageVector representing the icon for the main FAB. Defaults to Icons.Filled.Add.
 * @param iconRotate The optional rotation angle for the main FAB icon. Defaults to 45 degrees.
 * @return An instance of [FabButtonMain].
 */
fun FabButtonMain(iconRes: ImageVector = Icons.Filled.Add, iconRotate: Float = 45f): FabButtonMain =
    FabButtonMainImpl(iconRes, iconRotate)
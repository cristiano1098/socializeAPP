package com.example.cmu_g10.floatingActionButton

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

/**
 * Sealed class representing the state of a multi-option floating action button (FAB).
 */
sealed class FabButtonState {

    /**
     * Represents the collapsed state of the multi-option FAB.
     */
    object Collapsed : FabButtonState()

    /**
     * Represents the expanded state of the multi-option FAB.
     */
    object Expand : FabButtonState()

    /**
     * Checks if the FAB is in the expanded state.
     *
     * @return true if the FAB is expanded, false otherwise.
     */
    fun isExpanded() = this == Expand

    /**
     * Toggles the state of the FAB between expanded and collapsed.
     *
     * @return The updated state after toggling.
     */
    fun toggleValue() = if (isExpanded()) {
        Collapsed
    } else {
        Expand
    }
}

/**
 * Composable function to remember the state of a multi-option floating action button (FAB).
 *
 * @return A [FabButtonState] with an initial value of [FabButtonState.Collapsed].
 */
@Composable
fun rememberMultiFabState() =
    remember { mutableStateOf<FabButtonState>(FabButtonState.Collapsed) }
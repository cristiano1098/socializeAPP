package com.example.cmu_g10.floatingActionButton

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Composable function to display an individual sub-item of the Multi-FloatingActionButton.
 *
 * @param item The FabButtonItem representing the sub-item.
 * @param fabOption The FabButtonSub representing the style options for the sub-item.
 * @param onFabItemClicked Callback to be invoked when the sub-item is clicked.
 */
    @Composable
    fun MiniFabItem(
        item: FabButtonItem,
        fabOption: FabButtonSub,
        onFabItemClicked: (item: FabButtonItem) -> Unit
    ) {
        Row(
            modifier = Modifier
                .wrapContentSize()
                .padding(end = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Text label for the sub-item displayed in a rounded-corner background
            Text(
                text = item.label,
                style = typography.labelSmall,
                color = Color.White,
                modifier = Modifier
                    .clip(RoundedCornerShape(size = 8.dp))
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(all = 8.dp)
            )
            FloatingActionButton(
                onClick = { onFabItemClicked(item) },
                modifier = Modifier.size(40.dp),
                containerColor = fabOption.backgroundTint,
                contentColor = fabOption.iconTint
            ) {

                Icon(
                    imageVector = item.iconRes,
                    contentDescription = "Icon",
                    tint = fabOption.iconTint
                )
            }
        }
    }

/**
 * Composable function to display a Multi-FloatingActionButton with sub-items.
 *
 * @param modifier The modifier to be applied to the Multi-FAB.
 * @param items The list of FabButtonItem representing the sub-items.
 * @param fabState The MutableState representing the state of the Multi-FAB (expanded or collapsed).
 * @param fabIcon The FabButtonMain representing the main FAB icon and rotation options.
 * @param fabOption The FabButtonSub representing the style options for the sub-items.
 * @param onFabItemClicked Callback to be invoked when a sub-item is clicked.
 * @param stateChanged Callback to be invoked when the state of the Multi-FAB changes.
 */
    @Composable
    fun MultiFloatingActionButton(
        modifier: Modifier = Modifier,
        items: List<FabButtonItem>,
        fabState: MutableState<FabButtonState> = rememberMultiFabState(),
        fabIcon: FabButtonMain,
        fabOption: FabButtonSub = FabButtonSub(),
        onFabItemClicked: (fabItem: FabButtonItem) -> Unit,
        stateChanged: (fabState: FabButtonState) -> Unit = {}
    ) {
        // Animation for rotating the main FAB icon based on its state (expanded or collapsed)
        val rotation by animateFloatAsState(
            if (fabState.value == FabButtonState.Expand) {
                fabIcon.iconRotate ?: 0f
            } else {
                0f
            }, label = "Rotation"
        )

        Column(
            modifier = modifier.wrapContentSize(),
            horizontalAlignment = Alignment.End
        ) {
            // AnimatedVisibility to show or hide the sub-items when the Multi-FAB is expanded or collapsed
            AnimatedVisibility(
                visible = fabState.value.isExpanded(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                // LazyColumn to display the sub-items in a vertical list
                LazyColumn(
                    modifier = Modifier.wrapContentSize(),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    items(items.size) { index ->
                        // Composable to display each individual sub-item
                        MiniFabItem(
                            item = items[index],
                            fabOption = fabOption,
                            onFabItemClicked = onFabItemClicked
                        )
                    }
                    item {} // Empty item to provide spacing at the end of the list
                }
            }

            // Main FloatingActionButton representing the Multi-FAB
            FloatingActionButton(
                onClick = {
                    fabState.value = fabState.value.toggleValue()
                    stateChanged(fabState.value)
                },
                containerColor = fabOption.backgroundTint,
                contentColor = fabOption.iconTint
            ) {
                // Icon for the main FAB with optional rotation based on its state (expanded or collapsed)
                Icon(
                    imageVector = fabIcon.iconRes,
                    contentDescription = "Icon",
                    modifier = Modifier.rotate(rotation),
                    tint = fabOption.iconTint
                )
            }
        }
    }
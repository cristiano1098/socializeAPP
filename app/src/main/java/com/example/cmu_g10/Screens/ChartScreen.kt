package com.example.cmu_g10.Screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

/**
 * Composable function representing the "ExpenseChartScreen."
 * Displays an expense chart using the LineChartGraph composable within a Scaffold with a top app bar.
 *
 * @param navController The navigation controller for handling back navigation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ExpenseChartScreen(navController: NavController) {
    // Generate data points for the expense chart
    val expenseData = generateDataPoints()

    // Scaffold is a Material Design component providing basic layout structure
    Scaffold(
        topBar = {
            // TopAppBar displays the title and navigation icon
            TopAppBar(
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
                title = {
                    Text(
                        "Expenses by Month",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                navigationIcon = {
                    // IconButton for navigation back
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        content = {
            // Column is a composable that arranges its children in a vertical sequence
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 100.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                // Display the line chart using the LineChartGraph composable
                LineChartGraph(expenseData)
            }
        }
    )
}

/**
 * Composable function to display a line chart.
 *
 * @param expenseData List of [ExpenseData] representing the data points for the line chart.
 * @param lineColor Color of the line in the chart.
 * @param areaColor Color of the filled area below the line chart.
 */
@Composable
fun LineChartGraph(
    expenseData: List<ExpenseData>,
    lineColor: Color = Color(0xFF5233ff),
    areaColor: Color = Color(0xFF5233ff)
) {
    // Canvas is a composable that allows drawing custom graphics
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp, start = 8.dp, end = 8.dp)
    ) {
        // Calculate scaling factors for x and y axes
        val scaleX = size.width / (expenseData.size - 1).toFloat()
        val scaleY = size.height / (calculateMaxExpense(expenseData) * 2)

        // Draw month labels below the x-axis
        expenseData.forEachIndexed { index, data ->
            val x = index * scaleX
            val y = size.height / 2

            drawIntoCanvas {
                it.nativeCanvas.drawText(
                    data.month,
                    x - (scaleX / 2),
                    y + 24.dp.toPx(), // Adjust the vertical position of the label
                    android.graphics.Paint().apply {
                        color = Color.Black.toArgb()
                        textSize = 12.sp.toPx()
                    }
                )
            }
        }

        // Draw expense labels along the y-axis
        for (i in 0..4) {
            val yLabel = (i * size.height / 4).toFloat()
            val expenseLabel =
                (calculateMaxExpense(expenseData) - (i * calculateMaxExpense(expenseData) / 4)).toInt()

            drawIntoCanvas {
                it.nativeCanvas.drawText(
                    expenseLabel.toString(),
                    -16.dp.toPx(),
                    yLabel + 8.dp.toPx(),
                    android.graphics.Paint().apply {
                        color = Color.Black.toArgb()
                        textSize = 12.sp.toPx()
                    }
                )
            }
        }

        // Draw the horizontal line at the center of the chart
        drawLine(
            start = Offset(0f, size.height / 2f),
            end = Offset(size.width, size.height / 2f),
            color = Color.Black,
            strokeWidth = 2.dp.toPx(),
        )

        // Draw data points as circles and connect them with lines
        expenseData.forEachIndexed { index, data ->
            val x = index * scaleX
            val y = size.height / 2 - data.expense * scaleY

            drawCircle(
                color = Color.Blue,
                center = Offset(x, y),
                radius = 4.dp.toPx(),
            )
        }

        // Draw filled area below the line chart
        drawPath(
            path = Path().apply {
                moveTo(0f, size.height / 2f)
                expenseData.forEachIndexed { index, data ->
                    val x = index * scaleX
                    val y = size.height / 2 - data.expense * scaleY

                    lineTo(x, y)
                }
                lineTo(size.width, size.height / 2f)
                close()
            },
            color = areaColor,
        )

        // Draw the line chart
        drawPath(
            path = Path().apply {
                moveTo(0f, size.height / 2f)
                expenseData.forEachIndexed { index, data ->
                    val x = index * scaleX
                    val y = size.height / 2 - data.expense * scaleY

                    if (index == 0) {
                        moveTo(x, y)
                    } else {
                        lineTo(x, y)
                    }
                }
            },
            color = lineColor,
            style = Stroke(2.dp.toPx())
        )
    }
}

/**
 * Data class representing an expense data point.
 *
 * @property month String representing the month label.
 * @property expense Float representing the expense value for the month.
 */
data class ExpenseData(val month: String, val expense: Float)

/**
 * Function to generate sample expense data.
 *
 * @return List of [ExpenseData] representing sample data points.
 */
@Composable
fun generateDataPoints(): List<ExpenseData> {
    return listOf(
        ExpenseData("Jan", 1000f),
        ExpenseData("Feb", 900f),
        ExpenseData("Mar", 1100f),
        ExpenseData("Apr", 800f),
        ExpenseData("May", 950f),
        ExpenseData("Jun", 750f),
        ExpenseData("Jul", 120f),
        ExpenseData("Aug", 300f),
        ExpenseData("Sep", 600f),
        ExpenseData("Oct", 700f),
        ExpenseData("Nov", 550f),
        ExpenseData("Dec", 1000f),
    )
}

/**
 * Function to calculate the maximum expense in the given data set.
 *
 * @param expenseData List of [ExpenseData] representing the data set.
 * @return Float representing the maximum expense value.
 */
fun calculateMaxExpense(expenseData: List<ExpenseData>): Float {
    return expenseData.maxOf { it.expense }
}

/**
 * Preview composable for the line chart.
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true)
@Composable
fun LineChartPreview() {
    // Generate sample data for the preview
    val expenseData = generateDataPoints()

    // Scaffold for the preview
    Scaffold(
        topBar = {
            // TopAppBar for the preview
            TopAppBar(
                title = { Text("Expenses by Month") },
                navigationIcon = {
                    // IconButton for the navigation back (not functional in preview)
                    IconButton(onClick = { /*Todo*/ }) {
                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Back")
                    }
                }
            )
        },
        content = {
            // Column for the preview content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp, 8.dp, 8.dp, 0.dp)
            ) {
                // Display the line chart using the LineChartGraph composable
                LineChartGraph(expenseData)
            }
        }
    )
}




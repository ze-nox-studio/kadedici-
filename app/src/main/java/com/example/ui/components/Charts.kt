package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SpotifyTextGray
import com.example.ui.theme.SpotifyTextWhite

@Composable
fun SimpleDonutChart(
    data: List<Pair<String, Float>>,
    colors: List<Color>,
    modifier: Modifier = Modifier,
    centerLabel: String = ""
) {
    val total = data.sumOf { it.second.toDouble() }.toFloat()
    
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val sizeMin = size.minDimension
            val strokeWidth = 32.dp.toPx()
            val radius = (sizeMin - strokeWidth) / 2
            val rect = androidx.compose.ui.geometry.Rect(
                size.width / 2 - radius,
                size.height / 2 - radius,
                size.width / 2 + radius,
                size.height / 2 + radius
            )

            var startAngle = -90f
            data.forEachIndexed { index, pair ->
                val sweepAngle = if (total > 0) (pair.second / total) * 360f else 0f
                if (sweepAngle > 0) {
                    drawArc(
                        color = colors.getOrElse(index) { Color.Gray },
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = rect.topLeft,
                        size = rect.size,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
                startAngle += sweepAngle
            }
        }
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = centerLabel,
                color = SpotifyTextWhite,
                fontSize = 20.sp,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Total Sessions",
                color = SpotifyTextGray,
                fontSize = 11.sp,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun SimpleLineChart(
    scores: List<Float>,
    labels: List<String>,
    lineColor: Color,
    modifier: Modifier = Modifier
) {
    if (scores.isEmpty()) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text("No progress data available", color = SpotifyTextGray)
        }
        return
    }

    val maxScore = (scores.maxOrNull() ?: 100f).coerceAtLeast(10f)

    Column(modifier = modifier) {
        Canvas(modifier = Modifier.weight(1f).fillMaxWidth()) {
            val paddingLeft = 40.dp.toPx()
            val paddingBottom = 20.dp.toPx()
            val paddingTop = 10.dp.toPx()
            val paddingRight = 10.dp.toPx()

            val chartWidth = size.width - paddingLeft - paddingRight
            val chartHeight = size.height - paddingBottom - paddingTop

            // Draw Y Grid lines & Labels
            val gridLines = 4
            for (i in 0..gridLines) {
                val ratio = i.toFloat() / gridLines
                val y = paddingTop + chartHeight * (1 - ratio)
                
                // Horizontal line
                drawLine(
                    color = Color.DarkGray.copy(alpha = 0.3f),
                    start = Offset(paddingLeft, y),
                    end = Offset(size.width - paddingRight, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            // Draw data line & filled path
            val points = mutableListOf<Offset>()
            scores.forEachIndexed { index, score ->
                val xRatio = if (scores.size > 1) index.toFloat() / (scores.size - 1) else 0.5f
                val yRatio = score / maxScore
                val x = paddingLeft + chartWidth * xRatio
                val y = paddingTop + chartHeight * (1 - yRatio)
                points.add(Offset(x, y))
            }

            if (points.size > 1) {
                // Background gradient path
                val gradientPath = Path().apply {
                    moveTo(points.first().x, points.first().y)
                    points.forEach { lineTo(it.x, it.y) }
                    lineTo(points.last().x, paddingTop + chartHeight)
                    lineTo(points.first().x, paddingTop + chartHeight)
                    close()
                }

                drawPath(
                    path = gradientPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(lineColor.copy(alpha = 0.4f), Color.Transparent),
                        startY = points.minOf { it.y },
                        endY = paddingTop + chartHeight
                    )
                )

                // Smooth line
                drawPoints(
                    points = points,
                    pointMode = PointMode.Polygon,
                    color = lineColor,
                    strokeWidth = 3.dp.toPx()
                )

                // Draw dots on nodes
                points.forEach { point ->
                    drawCircle(
                        color = lineColor,
                        radius = 5.dp.toPx(),
                        center = point
                    )
                    drawCircle(
                        color = SpotifyTextWhite,
                        radius = 2.dp.toPx(),
                        center = point
                    )
                }
            } else if (points.size == 1) {
                drawCircle(
                    color = lineColor,
                    radius = 6.dp.toPx(),
                    center = points.first()
                )
            }
        }

        // Draw X Axis labels
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 40.dp, end = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            labels.forEach { label ->
                Text(
                    text = label,
                    color = SpotifyTextGray,
                    fontSize = 10.sp,
                    maxLines = 1
                )
            }
        }
    }
}

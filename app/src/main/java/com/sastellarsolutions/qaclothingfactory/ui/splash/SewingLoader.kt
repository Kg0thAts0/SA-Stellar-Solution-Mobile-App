package com.sastellarsolutions.qaclothingfactory.ui.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.sin

@Composable
fun SewingLoader(
    modifier: Modifier = Modifier
) {

    // ---------------------------------------------------------
    // MAIN INFINITE ANIMATION
    // ---------------------------------------------------------

    val transition = rememberInfiniteTransition(
        label = "sewing_animation"
    )


    // ---------------------------------------------------------
    // NEEDLE MOVES ACROSS THE FABRIC
    // ---------------------------------------------------------

    val sewingProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,

        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2200,
                easing = LinearEasing
            ),

            repeatMode = RepeatMode.Restart
        ),

        label = "sewing_progress"
    )


    // ---------------------------------------------------------
    // NEEDLE MOVES UP AND DOWN
    // ---------------------------------------------------------

    val needleMotion by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,

        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 220,
                easing = FastOutSlowInEasing
            ),

            repeatMode = RepeatMode.Reverse
        ),

        label = "needle_motion"
    )


    // ---------------------------------------------------------
    // THREAD MOVEMENT
    // ---------------------------------------------------------

    val threadMotion by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,

        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 600,
                easing = FastOutSlowInEasing
            ),

            repeatMode = RepeatMode.Reverse
        ),

        label = "thread_motion"
    )


    // ---------------------------------------------------------
    // DRAW THE SEWING LOADER
    // ---------------------------------------------------------

    Canvas(
        modifier = modifier
            .width(210.dp)
            .height(60.dp)
    ) {

        val startX = 12.dp.toPx()

        val endX =
            size.width - 12.dp.toPx()

        val fabricY =
            size.height * 0.65f


        // Current horizontal position of the needle.
        val needleX =
            startX +
                    ((endX - startX) * sewingProgress)


        // -----------------------------------------------------
        // FABRIC / BASE LINE
        // -----------------------------------------------------

        drawLine(
            color = Color(0xFFE2E2E2),

            start = Offset(
                x = startX,
                y = fabricY
            ),

            end = Offset(
                x = endX,
                y = fabricY
            ),

            strokeWidth = 1.dp.toPx(),

            cap = StrokeCap.Round
        )


        // -----------------------------------------------------
        // DRAW COMPLETED STITCHES
        // -----------------------------------------------------

        val stitchLength =
            8.dp.toPx()

        val stitchGap =
            5.dp.toPx()

        var stitchX =
            startX


        while (stitchX < needleX) {

            val stitchEnd =
                minOf(
                    stitchX + stitchLength,
                    needleX
                )


            drawLine(
                color = Color(0xFF1B1B1B),

                start = Offset(
                    x = stitchX,
                    y = fabricY
                ),

                end = Offset(
                    x = stitchEnd,
                    y = fabricY
                ),

                strokeWidth = 2.dp.toPx(),

                cap = StrokeCap.Round
            )


            stitchX +=
                stitchLength + stitchGap
        }


        // -----------------------------------------------------
        // NEEDLE VERTICAL MOVEMENT
        // -----------------------------------------------------

        val needleOffset =
            sin(
                needleMotion *
                        Math.PI
            )
                .toFloat() *
                    8.dp.toPx()


        val needleTop =
            fabricY -
                    31.dp.toPx() -
                    needleOffset


        val needleBottom =
            fabricY +
                    5.dp.toPx() -
                    needleOffset


        // -----------------------------------------------------
        // THREAD
        //
        // Slight curved thread leading into the needle.
        // -----------------------------------------------------

        val threadPath =
            Path().apply {

                moveTo(
                    needleX - 20.dp.toPx(),
                    needleTop - 8.dp.toPx()
                )


                quadraticTo(
                    needleX -
                            8.dp.toPx(),

                    needleTop -
                            (2.dp.toPx() +
                                    threadMotion *
                                    4.dp.toPx()),

                    needleX,
                    needleTop +
                            7.dp.toPx()
                )
            }


        drawPath(
            path = threadPath,

            color = Color(0xFF777777),

            style = Stroke(
                width = 1.2.dp.toPx(),
                cap = StrokeCap.Round
            )
        )


        // -----------------------------------------------------
        // NEEDLE
        // -----------------------------------------------------

        drawLine(
            color = Color(0xFF171717),

            start = Offset(
                x = needleX,
                y = needleTop
            ),

            end = Offset(
                x = needleX,
                y = needleBottom
            ),

            strokeWidth = 2.dp.toPx(),

            cap = StrokeCap.Round
        )


        // -----------------------------------------------------
        // NEEDLE TIP
        // -----------------------------------------------------

        drawLine(
            color = Color(0xFF171717),

            start = Offset(
                x = needleX,
                y = needleBottom
            ),

            end = Offset(
                x = needleX - 1.5.dp.toPx(),
                y = needleBottom +
                        4.dp.toPx()
            ),

            strokeWidth = 1.dp.toPx(),

            cap = StrokeCap.Round
        )


        // -----------------------------------------------------
        // NEEDLE EYE
        // -----------------------------------------------------

        drawCircle(
            color = Color.White,

            radius = 2.dp.toPx(),

            center = Offset(
                x = needleX,
                y = needleTop +
                        8.dp.toPx()
            )
        )


        drawCircle(
            color = Color(0xFF333333),

            radius = 2.dp.toPx(),

            center = Offset(
                x = needleX,
                y = needleTop +
                        8.dp.toPx()
            ),

            style = Stroke(
                width = 1.dp.toPx()
            )
        )


        // -----------------------------------------------------
        // SMALL CONTACT POINT
        //
        // Gives a subtle indication of where the needle
        // meets the fabric.
        // -----------------------------------------------------

        if (needleBottom >= fabricY - 3.dp.toPx()) {

            drawCircle(
                color = Color(0x22000000),

                radius = 4.dp.toPx(),

                center = Offset(
                    x = needleX,
                    y = fabricY
                )
            )
        }
    }
}
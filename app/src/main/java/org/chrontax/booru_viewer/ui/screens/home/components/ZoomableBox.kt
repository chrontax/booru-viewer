package org.chrontax.booru_viewer.ui.screens.home.components

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize

@Composable
fun ZoomableBox(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var size by remember { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = modifier
            .onSizeChanged { size = it }
            .graphicsLayer(
                scaleX = scale, scaleY = scale, translationX = offsetX, translationY = offsetY
            )
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown()
                    do {
                        val event = awaitPointerEvent()

                        val oldScale = scale
                        scale *= event.calculateZoom()

                        if (scale <= 1f) {
                            scale = 1f
                            offsetX = 0f
                            offsetY = 0f
                            continue
                        }
                        val scaledWidth = size.width * scale
                        val scaledHeight = size.height * scale

                        val maxOffsetX = (scaledWidth - size.width) / 2f
                        val maxOffsetY = (scaledHeight - size.height) / 2f

                        if (event.changes.size > 1) {
                            val centroid = event.calculateCentroid(useCurrent = true)

                            val contentCenterX = size.width / 2f + offsetX
                            val contentCenterY = size.height / 2f + offsetY

                            val centroidRelativeToContentCenterDescaledX = (centroid.x - contentCenterX) / oldScale
                            val centroidRelativeToContentCenterDescaledY = (centroid.y - contentCenterY) / oldScale

                            offsetX = centroid.x - size.width / 2f - centroidRelativeToContentCenterDescaledX * scale
                            offsetY = centroid.y - size.height / 2f - centroidRelativeToContentCenterDescaledY * scale
                        }

                        val pan = event.calculatePan()

                        offsetX += pan.x * scale
                        offsetY += pan.y * scale

                        offsetX = offsetX.coerceIn(-maxOffsetX, maxOffsetX)
                        offsetY = offsetY.coerceIn(-maxOffsetY, maxOffsetY)

                        if (scale > 1.001f) {
                            event.changes.forEach {
                                if (it.positionChanged()) {
                                    it.consume()
                                }
                            }
                        }
                    } while (event.changes.any { it.pressed })
                }
            }, content = content
    )
}
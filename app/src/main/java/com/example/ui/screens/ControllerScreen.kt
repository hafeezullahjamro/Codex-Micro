package com.example.ui.screens

import android.media.AudioManager
import android.os.Build
import android.view.SoundEffectConstants
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ShortcutMapping
import com.example.ui.CodexViewModel
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.roundToInt
import kotlin.math.sqrt

@Composable
fun ControllerScreen(
    viewModel: CodexViewModel,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val view = LocalView.current
    val mappings by viewModel.mappings.collectAsState()
    val ledStates by viewModel.ledStates.collectAsState()
    val logs by viewModel.transmissionLogs.collectAsState()

    // Helper to play sound & haptics when a button is clicked
    val triggerFeedback = {
        view.playSoundEffect(SoundEffectConstants.CLICK)
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F1113)) // Deep obsidian background
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Screen Header Indicator (gives helpful feedback of last sent command)
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1A1C1E))
                    .border(1.dp, Color(0x0DFFFFFF), RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Column {
                    Text(
                        text = "MICRO CODEX CHASSIS LINK",
                        color = Color(0xFF4CAF50),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = logs.firstOrNull() ?: "Offline Bluetooth keyboard initialized.",
                        color = Color(0x66FFFFFF),
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 1,
                        modifier = Modifier.testTag("terminal_status")
                    )
                }
            }

            // Outer Translucent Glowing Green Case (1:1 physical outline)
            Box(
                modifier = Modifier
                    .size(370.dp)
                    .shadow(
                        elevation = 24.dp,
                        shape = RoundedCornerShape(32.dp),
                        clip = false,
                        ambientColor = Color(0x7F26A69A),
                        spotColor = Color(0xFFD0BCFF)
                    )
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF1A1C1E), Color(0xFF1A1C1E), Color(0xFF1A1C1E)),
                            radius = 600f
                        ),
                        shape = RoundedCornerShape(32.dp)
                    )
                    .border(
                        width = 4.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0x1AFFFFFF), Color(0xFF34D399), Color(0x4400E676))
                        ),
                        shape = RoundedCornerShape(32.dp)
                    )
                    .padding(14.dp)
            ) {
                // Frosted Glass Layer Glow
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x33D0BCFF), RoundedCornerShape(24.dp))
                )

                // The Anodized Silver Top Plate with Hex Bolts
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF1A1C1E), Color(0xFF1A1C1E))
                            )
                        )
                        .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(22.dp))
                        .padding(12.dp)
                ) {
                    // Corner Hex Screws
                    HexScrew(Modifier.align(Alignment.TopStart))
                    HexScrew(Modifier.align(Alignment.TopEnd))
                    HexScrew(Modifier.align(Alignment.BottomStart))
                    HexScrew(Modifier.align(Alignment.BottomEnd))

                    // Engraved vertical text on Left Margin
                    Text(
                        text = "Work Louder | OpenAI 2026",
                        color = Color(0x8F222825),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .rotate(-90f)
                            .offset(y = (-55).dp),
                        textAlign = TextAlign.Center
                    )

                    // Engraved vertical text on Right Margin
                    Text(
                        text = "You can just build things",
                        color = Color(0x8F222825),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .rotate(90f)
                            .offset(y = (-55).dp),
                        textAlign = TextAlign.Center
                    )

                    // Engraved bottom text
                    Text(
                        text = "Let's build",
                        color = Color(0x7F222825),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = (-4).dp)
                    )

                    // Engraved top Arrow
                    Text(
                        text = "↑",
                        color = Color(0x7F222825),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = 2.dp)
                    )

                    // Keyboard Grid (4 rows x 4 columns)
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // ROW 1: Knob, Glow Button, Translucent Button, Joystick
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Knob (C1)
                            SkeuomorphicDial(
                                onClockwise = {
                                    triggerFeedback()
                                    viewModel.transmitKeyPress("knob_cw")
                                },
                                onCounterClockwise = {
                                    triggerFeedback()
                                    viewModel.transmitKeyPress("knob_ccw")
                                },
                                modifier = Modifier.size(54.dp).testTag("rotary_knob")
                            )

                            // Glowing Cyan Neon Key (C2 - Alt + Space)
                            TranslucentKeyCap(
                                keyId = "key_1_2",
                                hasCyanGlow = true,
                                label = "Alt+Spc",
                                onClick = {
                                    triggerFeedback()
                                    viewModel.transmitKeyPress("key_1_2")
                                },
                                modifier = Modifier.size(52.dp).testTag("key_1_2")
                            )

                            // Translucent Key (C3 - Ctrl + Shift + P)
                            TranslucentKeyCap(
                                keyId = "key_1_3",
                                label = "Ctrl+P",
                                onClick = {
                                    triggerFeedback()
                                    viewModel.transmitKeyPress("key_1_3")
                                },
                                modifier = Modifier.size(52.dp).testTag("key_1_3")
                            )

                            // 4-Way Black Joystick (C4)
                            SkeuomorphicJoystick(
                                onDirectionTriggered = { dir ->
                                    triggerFeedback()
                                    val keyId = when (dir) {
                                        JoystickDirection.LEFT -> "joy_left"
                                        JoystickDirection.RIGHT -> "joy_right"
                                        JoystickDirection.UP -> "joy_up"
                                        JoystickDirection.DOWN -> "joy_down"
                                    }
                                    viewModel.transmitKeyPress(keyId)
                                },
                                modifier = Modifier.size(54.dp).testTag("joystick_control")
                            )
                        }

                        // ROW 2: 4 Translucent Keys
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val keys = listOf(
                                "key_2_1" to "Ctrl+/",
                                "key_2_2" to "Ctrl+,",
                                "key_2_3" to "Ctrl+O",
                                "key_2_4" to "Ctrl+N"
                            )
                            keys.forEach { (id, label) ->
                                TranslucentKeyCap(
                                    keyId = id,
                                    label = label,
                                    onClick = {
                                        triggerFeedback()
                                        viewModel.transmitKeyPress(id)
                                    },
                                    modifier = Modifier.size(52.dp).testTag(id)
                                )
                            }
                        }

                        // ROW 3: 4 White Keycaps with crisp symbols
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val solidKeys = listOf(
                                Triple("key_3_1", "⚡", "Quick Chat"),
                                Triple("key_3_2", "✔️", "Search Tasks"),
                                Triple("key_3_3", "⊗", "Find Text"),
                                Triple("key_3_4", "⤏", "Code Review")
                            )
                            solidKeys.forEach { (id, symbol, desc) ->
                                SolidWhiteKeyCap(
                                    keyId = id,
                                    symbol = symbol,
                                    label = desc,
                                    onClick = {
                                        triggerFeedback()
                                        viewModel.transmitKeyPress(id)
                                    },
                                    modifier = Modifier.size(52.dp).testTag(id)
                                )
                            }
                        }

                        // ROW 4: Status/Button, Double-wide spacebar, Brain button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Column 1 Layout: LEDs and Toggle Button
                            Box(
                                modifier = Modifier
                                    .size(width = 54.dp, height = 52.dp)
                                    .padding(vertical = 2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    // Three tiny status LEDs
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(4.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        ledStates.forEachIndexed { i, active ->
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        if (active) {
                                                            when (i) {
                                                                0 -> Color(0xFF34D399) // LED 1 green
                                                                1 -> Color(0xFFFFB300) // LED 2 amber
                                                                else -> Color(0xFF00B0FF) // LED 3 blue
                                                            }
                                                        } else Color(0x3F101A15)
                                                    )
                                            )
                                        }
                                    }

                                    // Black circular button
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.radialGradient(
                                                    colors = listOf(Color(0xFF333333), Color(0xFF151515))
                                                )
                                            )
                                            .border(1.dp, Color(0xFF555555), CircleShape)
                                            .clickable {
                                                triggerFeedback()
                                                viewModel.transmitKeyPress("key_4_1")
                                                viewModel.toggleLed(0)
                                            }
                                            .testTag("key_4_1")
                                    )
                                }
                            }

                            // Double-Wide Spacebar with Mic icon (🎤)
                            DoubleWideMicKeyCap(
                                keyId = "key_4_2",
                                onClick = {
                                    triggerFeedback()
                                    viewModel.transmitKeyPress("key_4_2")
                                    viewModel.toggleLed(1)
                                },
                                modifier = Modifier
                                    .width(114.dp)
                                    .height(52.dp)
                                    .testTag("key_4_2")
                            )

                            // Solid White Brain Key (🧠)
                            SolidWhiteKeyCap(
                                keyId = "key_4_4",
                                symbol = "🧠",
                                label = "New Task Alt",
                                onClick = {
                                    triggerFeedback()
                                    viewModel.transmitKeyPress("key_4_4")
                                    viewModel.toggleLed(2)
                                },
                                modifier = Modifier.size(52.dp).testTag("key_4_4")
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HexScrew(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(4.dp)
            .size(10.dp)
            .clip(CircleShape)
            .background(Color(0xFF1A1C1E))
            .border(1.dp, Color(0x8F000000), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        // Screw inner details
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(RoundedCornerShape(1.dp))
                .background(Color(0xFF1A1C1E))
        )
    }
}

@Composable
fun TranslucentKeyCap(
    keyId: String,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    hasCyanGlow: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(if (isPressed) 0.92f else 1.0f)
    val depthOffset by animateDpAsState(if (isPressed) 1.dp else 4.dp)

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
    ) {
        // Glow effect for Key C2
        if (hasCyanGlow) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp)
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(12.dp),
                        ambientColor = Color(0xFF34D399),
                        spotColor = Color(0xFFD0BCFF)
                    )
                    .background(Color(0x3300FFD8), RoundedCornerShape(12.dp))
                    .border(2.dp, Color(0xFFD0BCFF), RoundedCornerShape(12.dp))
            )
        }

        // Shadow representing physical key thickness
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = depthOffset)
                .background(Color(0x7A1A2420), RoundedCornerShape(12.dp))
        )

        // Frosted Translucent Top
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xE0ECEFEB), Color(0x99DFE4E1))
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                .border(1.dp, Color(0xFFBCC2BF), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            // Blue cross stem under translucent housing
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .drawBehind {
                        // Draw a plus (+) symbol
                        drawLine(
                            color = Color(0xFF3F51B5),
                            start = Offset(0f, size.height / 2),
                            end = Offset(size.width, size.height / 2),
                            strokeWidth = 3.dp.toPx()
                        )
                        drawLine(
                            color = Color(0xFF3F51B5),
                            start = Offset(size.width / 2, 0f),
                            end = Offset(size.width / 2, size.height),
                            strokeWidth = 3.dp.toPx()
                        )
                    }
            )

            // Label text over everything
            Text(
                text = label,
                color = Color(0xFF1E2824),
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 2.dp)
            )
        }
    }
}

@Composable
fun SolidWhiteKeyCap(
    keyId: String,
    symbol: String,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.95f else 1.0f)

    Box(
        modifier = modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .background(Color(0xFFD0BCFF), RoundedCornerShape(24.dp))
            .clickable(interactionSource = interactionSource, indication = null) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(text = symbol, fontSize = 18.sp, color = Color(0xFF381E72))
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = label, fontSize = 10.sp, color = Color(0xFF381E72), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        }
    }
}
@Composable
fun DoubleWideMicKeyCap(
    keyId: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.95f else 1.0f)

    Box(
        modifier = modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .background(Color(0xFF2D2F31), RoundedCornerShape(24.dp))
            .border(1.dp, Color(0x0DFFFFFF), RoundedCornerShape(24.dp))
            .clickable(interactionSource = interactionSource, indication = null) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(text = "🎤", fontSize = 18.sp, color = Color.White)
        }
    }
}
@Composable
fun SkeuomorphicDial(
    onClockwise: () -> Unit,
    onCounterClockwise: () -> Unit,
    modifier: Modifier = Modifier
) {
    var rotationAngle by remember { mutableStateOf(45f) }
    var lastAngle by remember { mutableStateOf(0f) }

    Box(
        modifier = modifier
            .background(Color(0xFF1A1C1E), CircleShape)
            .border(1.dp, Color(0x1AFFFFFF), CircleShape)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val centerX = size.width / 2f
                        val centerY = size.height / 2f
                        lastAngle = Math.toDegrees(
                            atan2((offset.y - centerY).toDouble(), (offset.x - centerX).toDouble())
                        ).toFloat()
                    },
                    onDrag = { change, _ ->
                        val centerX = size.width / 2f
                        val centerY = size.height / 2f
                        val currentOffset = change.position
                        val currentAngle = Math.toDegrees(
                            atan2((currentOffset.y - centerY).toDouble(), (currentOffset.x - centerX).toDouble())
                        ).toFloat()

                        var delta = currentAngle - lastAngle
                        if (delta < -180f) delta += 360f
                        if (delta > 180f) delta -= 360f

                        if (abs(delta) > 20f) {
                            if (delta > 0) onClockwise() else onCounterClockwise()
                            rotationAngle = (rotationAngle + delta) % 360f
                            lastAngle = currentAngle
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(0.6f)
                .rotate(rotationAngle)
                .background(Color(0xFF2D2F31), CircleShape)
                .border(2.dp, Color(0xFF1A1C1E), CircleShape)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight(0.5f)
                    .width(4.dp)
                    .align(Alignment.TopCenter)
                    .background(Color(0xFFD0BCFF), RoundedCornerShape(2.dp))
            )
        }
    }
}
enum class JoystickDirection { LEFT, RIGHT, UP, DOWN }

@Composable
fun SkeuomorphicJoystick(
    onDirectionTriggered: (JoystickDirection) -> Unit,
    modifier: Modifier = Modifier
) {
    val maxRadiusPx = 50f
    val stickOffset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    var triggeredThisDrag = remember { mutableStateOf<JoystickDirection?>(null) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .background(Color(0xFF1A1C1E), CircleShape)
            .border(1.dp, Color(0x1AFFFFFF), CircleShape)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        triggeredThisDrag.value = null
                    },
                    onDragEnd = {
                        scope.launch {
                            stickOffset.animateTo(Offset.Zero, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                        }
                        triggeredThisDrag.value = null
                    },
                    onDragCancel = {
                        scope.launch {
                            stickOffset.animateTo(Offset.Zero, spring())
                        }
                        triggeredThisDrag.value = null
                    },
                    onDrag = { change, dragAmount ->
                        val combined = stickOffset.value + dragAmount
                        val length = sqrt(combined.x * combined.x + combined.y * combined.y)

                        val limited = if (length > maxRadiusPx) {
                            Offset(
                                (combined.x / length) * maxRadiusPx,
                                (combined.y / length) * maxRadiusPx
                            )
                        } else {
                            combined
                        }

                        scope.launch {
                            stickOffset.snapTo(limited)
                        }

                        val threshold = maxRadiusPx * 0.7f
                        if (length > threshold && triggeredThisDrag.value == null) {
                            val angle = Math.toDegrees(atan2(limited.y.toDouble(), limited.x.toDouble()))
                            val direction = when {
                                angle >= -45 && angle < 45 -> JoystickDirection.RIGHT
                                angle >= 45 && angle < 135 -> JoystickDirection.DOWN
                                angle >= -135 && angle < -45 -> JoystickDirection.UP
                                else -> JoystickDirection.LEFT
                            }
                            triggeredThisDrag.value = direction
                            onDirectionTriggered(direction)
                        } else if (length < threshold * 0.3f) {
                            triggeredThisDrag.value = null
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier.fillMaxSize(0.8f).border(1.dp, Color(0x0DFFFFFF), CircleShape))
        
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        stickOffset.value.x.roundToInt(),
                        stickOffset.value.y.roundToInt()
                    )
                }
                .size(64.dp)
                .background(Color(0xFFD0BCFF), CircleShape)
                .border(4.dp, Color(0xFF1A1C1E), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(Color(0xFF381E72), CircleShape)
            )
        }
    }
}

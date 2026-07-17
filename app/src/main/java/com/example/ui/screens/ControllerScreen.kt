package com.example.ui.screens
import androidx.compose.animation.animateColorAsState
import androidx.compose.ui.graphics.drawscope.rotate

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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
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
    val connectedDeviceName by viewModel.bluetoothController.connectedDeviceName.collectAsState()
    val status by viewModel.bluetoothController.status.collectAsState()
    
    val isConnected = connectedDeviceName != null || status.contains("Connected") || status.contains("Simulated")
    val isIdle = !isConnected && (status.contains("Ready") || status.contains("Active"))
    
    val targetGlowColor = when {
        isConnected -> Color(0xFF34D399) // Greenish
        isIdle -> Color(0xFF40C4FF) // Bluish
        else -> Color(0xFFEF5350) // Reddish
    }
    val glowColor by animateColorAsState(targetValue = targetGlowColor, animationSpec = tween(1000))
    
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "glowRotation"
    )

    val triggerFeedback = {
        view.playSoundEffect(SoundEffectConstants.CLICK)
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFE2E8F0)), // Grayish background as requested
        contentAlignment = Alignment.Center
    ) {
        // Outer glowing rim
        Box(
            modifier = Modifier
                .padding(16.dp)
                .aspectRatio(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(48.dp))
                .background(Color(0xAAFFFFFF)) // frosted base
                .drawBehind {
                    rotate(rotation) {
                        drawRect(
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    glowColor.copy(alpha = 0.1f), 
                                    glowColor.copy(alpha = 0.8f), 
                                    glowColor.copy(alpha = 0.1f),
                                    glowColor.copy(alpha = 0.1f)
                                ),
                                center = center
                            ),
                            size = size.times(2f), // Make it larger to cover corners during rotation
                            topLeft = Offset(-size.width * 0.5f, -size.height * 0.5f)
                        )
                    }
                }
                .border(2.dp, Color(0x33FFFFFF), RoundedCornerShape(48.dp))
                .padding(16.dp) // Gap between outer rim and inner chassis
        ) {
            // Inner chassis
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFE2E8F0), RoundedCornerShape(32.dp))
                    .border(2.dp, Color(0xFFCBD5E1), RoundedCornerShape(32.dp))
                    .padding(8.dp)
            ) {
                // Screws
                Screw(Modifier.align(Alignment.TopStart).padding(8.dp))
                Screw(Modifier.align(Alignment.TopEnd).padding(8.dp))
                Screw(Modifier.align(Alignment.BottomStart).padding(8.dp))
                Screw(Modifier.align(Alignment.BottomEnd).padding(8.dp))

                // Text labels
                Text(
                    text = "Work Louder | OpenAI 2026",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .offset(x = (-30).dp, y = 0.dp)
                        .rotate(-90f),
                    fontSize = 10.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = "You can just build things",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .offset(x = 30.dp, y = 0.dp)
                        .rotate(90f),
                    fontSize = 10.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = "↑",
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 10.dp),
                    fontSize = 16.sp,
                    color = Color(0xFF64748B)
                )
                
                Text(
                    text = "Let's build",
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp),
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Medium
                )

                // Layout
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Row 1
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ImageDial()
                        ImageTranslucentKey(isLit = true, label = mappings["key_1"]?.name) { triggerFeedback(); viewModel.transmitKeyPress("key_1") }
                        ImageTranslucentKey(label = mappings["key_2"]?.name) { triggerFeedback(); viewModel.transmitKeyPress("key_2") }
                        ImageJoystick()
                    }
                    
                    // Row 2
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ImageTranslucentKey(label = mappings["key_3"]?.name) { triggerFeedback(); viewModel.transmitKeyPress("key_3") }
                        ImageTranslucentKey(label = mappings["key_4"]?.name) { triggerFeedback(); viewModel.transmitKeyPress("key_4") }
                        ImageTranslucentKey(label = mappings["key_5"]?.name) { triggerFeedback(); viewModel.transmitKeyPress("key_5") }
                        ImageTranslucentKey(label = mappings["key_6"]?.name) { triggerFeedback(); viewModel.transmitKeyPress("key_6") }
                    }
                    
                    // Row 3
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ImageSolidKey(icon = Icons.Default.FlashOn) { triggerFeedback(); viewModel.transmitKeyPress("key_7") }
                        ImageSolidKey(icon = Icons.Default.CheckCircle) { triggerFeedback(); viewModel.transmitKeyPress("key_8") }
                        ImageSolidKey(icon = Icons.Default.Clear) { triggerFeedback(); viewModel.transmitKeyPress("key_9") }
                        ImageSolidKey(icon = Icons.Default.SyncAlt) { triggerFeedback(); viewModel.transmitKeyPress("key_10") }
                    }
                    
                    // Row 4
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Small black button and LEDs
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                LED(Color.White)
                                LED(Color.White)
                                LED(Color.White)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(Color.Black, CircleShape)
                            )
                        }
                        
                        // Double wide mic key
                        ImageDoubleSolidKey(icon = Icons.Default.Mic) { triggerFeedback(); viewModel.transmitKeyPress("key_11") }
                        
                        // Single right key
                        ImageSolidKey(icon = Icons.Default.Cloud) { triggerFeedback(); viewModel.transmitKeyPress("key_12") }
                    }
                }
            }
        }
    }
}

@Composable
fun Screw(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(16.dp)
            .background(Color(0xFF333333), CircleShape)
            .border(1.dp, Color(0xFF111111), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier.size(8.dp).border(1.5.dp, Color(0xFF111111), CircleShape))
    }
}

@Composable
fun LED(color: Color) {
    Box(
        modifier = Modifier
            .size(6.dp, 3.dp)
            .background(color, RoundedCornerShape(1.dp))
            .border(0.5.dp, Color.Black, RoundedCornerShape(1.dp))
    )
}

@Composable
fun ImageTranslucentKey(
    isLit: Boolean = false,
    label: String? = null,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.9f else 1.0f)
    
    val bgColor = if (isLit) Color(0x6634D399) else Color(0x11000000)
    val borderColor = if (isLit) Color(0xAA34D399) else Color(0x11000000)

    Box(
        modifier = Modifier
            .size(56.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .background(bgColor, RoundedCornerShape(14.dp))
            .border(2.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable(interactionSource = interactionSource, indication = null) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(Color(0xDDFFFFFF), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (!label.isNullOrBlank()) {
                Text(label, color = Color(0xFF7986CB), fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1, textAlign = TextAlign.Center)
            } else {
                Box(
                    modifier = Modifier.size(20.dp).border(1.5.dp, Color(0xFF7986CB), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("+", color = Color(0xFF7986CB), fontSize = 16.sp, fontWeight = FontWeight.Medium, modifier = Modifier.offset(y = (-1).dp))
                }
            }
        }
    }
}


@Composable
fun ImageSolidKey(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.9f else 1.0f)

    Box(
        modifier = Modifier
            .size(56.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = Color(0x33000000), ambientColor = Color(0x11000000))
            .background(Color.White, RoundedCornerShape(16.dp))
            .clickable(interactionSource = interactionSource, indication = null) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFF111111), modifier = Modifier.size(24.dp))
    }
}


@Composable
fun ImageDoubleSolidKey(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.9f else 1.0f)

    Box(
        modifier = Modifier
            .height(56.dp)
            .width(128.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = Color(0x33000000), ambientColor = Color(0x11000000))
            .background(Color.White, RoundedCornerShape(16.dp))
            .clickable(interactionSource = interactionSource, indication = null) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFF111111), modifier = Modifier.size(24.dp))
    }
}


@Composable
fun ImageDial(modifier: Modifier = Modifier) {
    var rotationAngle by remember { mutableStateOf(-45f) }
    var lastAngle by remember { mutableStateOf(0f) }

    Box(
        modifier = modifier
            .size(56.dp)
            .shadow(6.dp, CircleShape, spotColor = Color(0x44000000), ambientColor = Color(0x22000000))
            .background(Color.White, CircleShape)
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

                        rotationAngle = (rotationAngle + delta) % 360f
                        lastAngle = currentAngle
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .rotate(rotationAngle)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(12.dp)
                    .align(Alignment.CenterStart)
                    .offset(x = 8.dp)
                    .background(Color(0xFFE2E8F0), RoundedCornerShape(6.dp))
            )
        }
    }
}


enum class JoystickDirection { LEFT, RIGHT, UP, DOWN }

@Composable
fun ImageJoystick(modifier: Modifier = Modifier) {
    val stickOffset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    val scope = rememberCoroutineScope()
    val maxRadiusPx = 20f

    Box(
        modifier = modifier
            .size(56.dp)
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(2.dp, Color(0xFF111111), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        // Striped border pattern
        Canvas(modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(10.dp))) {
            val step = 8f
            for (i in 0..100 step 8) {
                drawLine(
                    color = Color.Black,
                    start = Offset(0f, i.toFloat()),
                    end = Offset(size.width, i.toFloat()),
                    strokeWidth = 2f
                )
                drawLine(
                    color = Color.Black,
                    start = Offset(i.toFloat(), 0f),
                    end = Offset(i.toFloat(), size.height),
                    strokeWidth = 2f
                )
            }
        }
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xDDFFFFFF), RoundedCornerShape(10.dp))
        )
        
        Box(
            modifier = Modifier
                .offset { IntOffset(stickOffset.value.x.roundToInt(), stickOffset.value.y.roundToInt()) }
                .size(46.dp)
                .shadow(8.dp, CircleShape, spotColor = Color.Black)
                .background(Color(0xFF222222), CircleShape)
                .border(2.dp, Color(0xFF111111), CircleShape)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            scope.launch { stickOffset.animateTo(Offset.Zero, spring()) }
                        },
                        onDragCancel = {
                            scope.launch { stickOffset.animateTo(Offset.Zero, spring()) }
                        },
                        onDrag = { _, dragAmount ->
                            val combined = stickOffset.value + dragAmount
                            val length = sqrt(combined.x * combined.x + combined.y * combined.y)
                            val limited = if (length > maxRadiusPx) {
                                Offset((combined.x / length) * maxRadiusPx, (combined.y / length) * maxRadiusPx)
                            } else combined
                            scope.launch { stickOffset.snapTo(limited) }
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            // Little dpad indents on the black stick
            Icon(Icons.Default.KeyboardArrowUp, null, tint = Color(0xFF444444), modifier = Modifier.align(Alignment.TopCenter).size(14.dp).offset(y = 2.dp))
            Icon(Icons.Default.KeyboardArrowDown, null, tint = Color(0xFF444444), modifier = Modifier.align(Alignment.BottomCenter).size(14.dp).offset(y = (-2).dp))
            Icon(Icons.Default.KeyboardArrowLeft, null, tint = Color(0xFF444444), modifier = Modifier.align(Alignment.CenterStart).size(14.dp).offset(x = 2.dp))
            Icon(Icons.Default.KeyboardArrowRight, null, tint = Color(0xFF444444), modifier = Modifier.align(Alignment.CenterEnd).size(14.dp).offset(x = (-2).dp))
        }
    }
}


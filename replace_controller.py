with open('app/src/main/java/com/example/ui/screens/ControllerScreen.kt', 'r') as f:
    content = f.read()

header = content.split('@Composable')[0]

new_code = """@Composable
fun ControllerScreen(
    viewModel: CodexViewModel,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val view = LocalView.current
    val mappings by viewModel.mappings.collectAsState()
    
    val triggerFeedback = {
        view.playSoundEffect(SoundEffectConstants.CLICK)
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFE0F2F1)), // Light glowing background context
        contentAlignment = Alignment.Center
    ) {
        // Outer glowing rim
        Box(
            modifier = Modifier
                .padding(16.dp)
                .aspectRatio(1f)
                .fillMaxWidth()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF90F5CC), Color(0x8890F5CC), Color(0x2290F5CC)),
                        radius = 800f
                    ),
                    shape = RoundedCornerShape(48.dp)
                )
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
                        .padding(horizontal = 38.dp, vertical = 38.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Row 1
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ImageDial()
                        ImageTranslucentKey(isLit = true, label = mappings["key_1"]?.label) { triggerFeedback(); viewModel.executeAction("key_1") }
                        ImageTranslucentKey(label = mappings["key_2"]?.label) { triggerFeedback(); viewModel.executeAction("key_2") }
                        ImageJoystick()
                    }
                    
                    // Row 2
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ImageTranslucentKey(label = mappings["key_3"]?.label) { triggerFeedback(); viewModel.executeAction("key_3") }
                        ImageTranslucentKey(label = mappings["key_4"]?.label) { triggerFeedback(); viewModel.executeAction("key_4") }
                        ImageTranslucentKey(label = mappings["key_5"]?.label) { triggerFeedback(); viewModel.executeAction("key_5") }
                        ImageTranslucentKey(label = mappings["key_6"]?.label) { triggerFeedback(); viewModel.executeAction("key_6") }
                    }
                    
                    // Row 3
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ImageSolidKey(icon = Icons.Default.Bolt) { triggerFeedback(); viewModel.executeAction("key_7") }
                        ImageSolidKey(icon = Icons.Default.CheckCircleOutline) { triggerFeedback(); viewModel.executeAction("key_8") }
                        ImageSolidKey(icon = Icons.Default.Cancel) { triggerFeedback(); viewModel.executeAction("key_9") }
                        ImageSolidKey(icon = Icons.Default.Shuffle) { triggerFeedback(); viewModel.executeAction("key_10") }
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
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .background(Color.Black, CircleShape)
                            )
                        }
                        
                        // Double wide mic key
                        ImageDoubleSolidKey(icon = Icons.Default.Mic) { triggerFeedback(); viewModel.executeAction("key_11") }
                        
                        // Single right key
                        ImageSolidKey(icon = Icons.Default.Cloud) { triggerFeedback(); viewModel.executeAction("key_12") }
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
    
    val bgColor = if (isLit) Color(0x6634D399) else Color(0x22000000)
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
                .background(Color(0xBBFFFFFF), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (!label.isNullOrBlank()) {
                Text(label, color = Color(0xFF6366F1), fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            } else {
                Text("+", color = Color(0xFF6366F1), fontSize = 24.sp, fontWeight = FontWeight.Medium)
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
            .background(Color(0xFFF8FAFC), RoundedCornerShape(14.dp))
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
            .clickable(interactionSource = interactionSource, indication = null) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFF0F172A), modifier = Modifier.size(24.dp))
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
            .width(132.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .background(Color(0xFFF8FAFC), RoundedCornerShape(14.dp))
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
            .clickable(interactionSource = interactionSource, indication = null) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFF0F172A), modifier = Modifier.size(24.dp))
    }
}

@Composable
fun ImageDial(modifier: Modifier = Modifier) {
    var rotationAngle by remember { mutableStateOf(-45f) }
    var lastAngle by remember { mutableStateOf(0f) }

    Box(
        modifier = modifier
            .size(56.dp)
            .background(Color(0xFFF8FAFC), CircleShape)
            .border(1.dp, Color(0xFFE2E8F0), CircleShape)
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
                    .height(8.dp)
                    .align(Alignment.CenterStart)
                    .offset(x = 4.dp)
                    .background(Color(0xFFE2E8F0), RoundedCornerShape(4.dp))
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
            .background(Color(0xFFCBD5E1), RoundedCornerShape(12.dp))
            .border(2.dp, Color(0xFF94A3B8), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .offset { IntOffset(stickOffset.value.x.roundToInt(), stickOffset.value.y.roundToInt()) }
                .size(42.dp)
                .background(Color(0xFF1E293B), CircleShape)
                .border(2.dp, Color(0xFF0F172A), CircleShape)
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
            Icon(Icons.Default.KeyboardArrowUp, null, tint = Color(0xFF475569), modifier = Modifier.align(Alignment.TopCenter).size(14.dp))
            Icon(Icons.Default.KeyboardArrowDown, null, tint = Color(0xFF475569), modifier = Modifier.align(Alignment.BottomCenter).size(14.dp))
            Icon(Icons.Default.KeyboardArrowLeft, null, tint = Color(0xFF475569), modifier = Modifier.align(Alignment.CenterStart).size(14.dp))
            Icon(Icons.Default.KeyboardArrowRight, null, tint = Color(0xFF475569), modifier = Modifier.align(Alignment.CenterEnd).size(14.dp))
        }
    }
}
"""

with open('app/src/main/java/com/example/ui/screens/ControllerScreen.kt', 'w') as f:
    f.write(header + new_code)

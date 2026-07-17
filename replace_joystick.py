import re

with open('app/src/main/java/com/example/ui/screens/ControllerScreen.kt', 'r') as f:
    content = f.read()

rep_dial = """@Composable
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
"""

rep_joystick = """@Composable
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
"""

content = re.sub(r'@Composable\s+fun SkeuomorphicDial.*?(?=enum class JoystickDirection)', rep_dial, content, flags=re.DOTALL)

# Find the start of SkeuomorphicJoystick and replace until end of file (since it's the last function)
content = re.sub(r'@Composable\s+fun SkeuomorphicJoystick.*', rep_joystick, content, flags=re.DOTALL)

with open('app/src/main/java/com/example/ui/screens/ControllerScreen.kt', 'w') as f:
    f.write(content)


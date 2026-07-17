import re

with open('app/src/main/java/com/example/ui/screens/ControllerScreen.kt', 'r') as f:
    content = f.read()

rep_dial = """@Composable
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
}"""

rep_joystick = """@Composable
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
}"""

content = re.sub(r'@Composable\s+fun ImageDial.*?\}\n\}\n', rep_dial + '\n\n', content, flags=re.DOTALL)
content = re.sub(r'@Composable\s+fun ImageJoystick.*?\}\n\}\n', rep_joystick + '\n\n', content, flags=re.DOTALL)

# Adjust padding to make keys tighter
content = content.replace("padding(horizontal = 38.dp, vertical = 38.dp)", "padding(horizontal = 24.dp, vertical = 24.dp)")

# Adjust LEDs and Black button
rep_leds = """                        Row(verticalAlignment = Alignment.CenterVertically) {
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
                        }"""
content = re.sub(r'Row\(verticalAlignment = Alignment\.CenterVertically\) \{\n\s*Column\(verticalArrangement = Arrangement\.spacedBy\(4\.dp\)\) \{\n.*?Box\(\n.*?\n.*?\n.*?\)[\s\n]*\}', rep_leds, content, flags=re.DOTALL)


with open('app/src/main/java/com/example/ui/screens/ControllerScreen.kt', 'w') as f:
    f.write(content)

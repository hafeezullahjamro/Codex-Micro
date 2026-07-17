import re

with open('app/src/main/java/com/example/ui/screens/ControllerScreen.kt', 'r') as f:
    content = f.read()

replacement = """@Composable
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
        ) {"""

# Replace from @Composable fun ControllerScreen to the padding(16.dp) line inside the outer Box
content = re.sub(r'@Composable\s+fun ControllerScreen.*?\.padding\(16\.dp\) // Gap between outer rim and inner chassis\n\s*\) \{', replacement, content, flags=re.DOTALL)

with open('app/src/main/java/com/example/ui/screens/ControllerScreen.kt', 'w') as f:
    f.write(content)

import re

with open('app/src/main/java/com/example/ui/screens/ControllerScreen.kt', 'r') as f:
    content = f.read()

rep_translucent = """@Composable
fun ImageTranslucentKey(
    statusColor: Color? = null,
    label: String? = null,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.94f else 1.0f)
    val elevation by animateFloatAsState(if (isPressed) 1f else 4f)
    
    val baseColor = statusColor ?: Color.Transparent
    val glowColor = if (statusColor != null) statusColor.copy(alpha = 0.6f) else Color(0x11000000)
    val borderColor = if (statusColor != null) statusColor.copy(alpha = 0.8f) else Color(0x22000000)

    Box(
        modifier = Modifier
            .size(56.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .shadow(elevation.dp, RoundedCornerShape(14.dp), spotColor = glowColor, ambientColor = glowColor)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xDDEEFFF0), Color(0xBBFFFFFF))
                ),
                shape = RoundedCornerShape(14.dp)
            )
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable(interactionSource = interactionSource, indication = null) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // Inner frosted glow piece
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            if (statusColor != null) statusColor.copy(alpha = 0.95f) else Color(0x44FFFFFF),
                            if (statusColor != null) statusColor.copy(alpha = 0.3f) else Color(0x11FFFFFF)
                        ),
                        radius = 100f
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
                .border(0.5.dp, Color(0x44FFFFFF), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (!label.isNullOrBlank()) {
                Text(label, color = Color(0xFF334155), fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1, textAlign = TextAlign.Center)
            } else {
                Box(
                    modifier = Modifier.size(18.dp).border(1.5.dp, Color(0xFF334155), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("+", color = Color(0xFF334155), fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.offset(y = (-1).dp))
                }
            }
        }
    }
}"""

content = re.sub(r'@Composable\s+fun ImageTranslucentKey.*?\}\n\}\n', rep_translucent + '\n\n', content, flags=re.DOTALL)

with open('app/src/main/java/com/example/ui/screens/ControllerScreen.kt', 'w') as f:
    f.write(content)

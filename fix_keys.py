import re

with open('app/src/main/java/com/example/ui/screens/ControllerScreen.kt', 'r') as f:
    content = f.read()

rep_translucent = """@Composable
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
}"""

rep_solid = """@Composable
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
}"""

rep_double_solid = """@Composable
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
}"""

content = re.sub(r'@Composable\s+fun ImageTranslucentKey.*?\}\n\}\n', rep_translucent + '\n\n', content, flags=re.DOTALL)
content = re.sub(r'@Composable\s+fun ImageSolidKey.*?\}\n\}\n', rep_solid + '\n\n', content, flags=re.DOTALL)
content = re.sub(r'@Composable\s+fun ImageDoubleSolidKey.*?\}\n\}\n', rep_double_solid + '\n\n', content, flags=re.DOTALL)

with open('app/src/main/java/com/example/ui/screens/ControllerScreen.kt', 'w') as f:
    f.write(content)

import re

with open('app/src/main/java/com/example/ui/screens/ControllerScreen.kt', 'r') as f:
    content = f.read()

# Replace RealisticMechanicalKey
rep_mech = """@Composable
fun RealisticMechanicalKey(
    keyId: String,
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
            .background(Color(0xFF2D2F31), RoundedCornerShape(24.dp))
            .border(1.dp, Color(0x0DFFFFFF), RoundedCornerShape(24.dp))
            .clickable(interactionSource = interactionSource, indication = null) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(text = keyId, fontSize = 10.sp, color = Color(0x80FFFFFF), fontFamily = FontFamily.Monospace)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = label, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        }
    }
}
"""
content = re.sub(r'@Composable\s+fun RealisticMechanicalKey.*?(?=@Composable\s+fun SolidWhiteKeyCap)', rep_mech, content, flags=re.DOTALL)

# Replace SolidWhiteKeyCap
rep_solid = """@Composable
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
"""
content = re.sub(r'@Composable\s+fun SolidWhiteKeyCap.*?(?=@Composable\s+fun DoubleWideMicKeyCap)', rep_solid, content, flags=re.DOTALL)

# Replace DoubleWideMicKeyCap
rep_wide = """@Composable
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
"""
content = re.sub(r'@Composable\s+fun DoubleWideMicKeyCap.*?(?=@Composable\s+fun SkeuomorphicDial)', rep_wide, content, flags=re.DOTALL)

with open('app/src/main/java/com/example/ui/screens/ControllerScreen.kt', 'w') as f:
    f.write(content)

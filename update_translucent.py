import re
with open('app/src/main/java/com/example/ui/screens/ControllerScreen.kt', 'r') as f:
    content = f.read()

# Make the text of assigned shortcut slightly bigger if it is short
content = content.replace("Text(label, color = Color(0xFF6366F1), fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1)",
                          "Text(label, color = Color(0xFF6366F1), fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1, textAlign = TextAlign.Center)")

with open('app/src/main/java/com/example/ui/screens/ControllerScreen.kt', 'w') as f:
    f.write(content)

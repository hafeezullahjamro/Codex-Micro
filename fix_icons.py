import re

with open('app/src/main/java/com/example/ui/screens/ControllerScreen.kt', 'r') as f:
    content = f.read()

# Replace SyncAlt with CallSplit
content = content.replace("Icons.Default.SyncAlt", "Icons.Default.CallSplit")

# Fix background color to be slightly more silver/gray instead of slate
content = content.replace("Color(0xFFE2E8F0)", "Color(0xFFE4E4E5)")

# Make sure CallSplit is imported if not there
if "CallSplit" not in content and "import androidx.compose.material.icons.filled.CallSplit" not in content:
    content = content.replace("import androidx.compose.material.icons.Icons", "import androidx.compose.material.icons.Icons\nimport androidx.compose.material.icons.filled.CallSplit")

with open('app/src/main/java/com/example/ui/screens/ControllerScreen.kt', 'w') as f:
    f.write(content)

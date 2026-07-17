with open('app/src/main/java/com/example/ui/screens/ControllerScreen.kt', 'r') as f:
    content = f.read()

# Fix the import before package
content = content.replace("import androidx.compose.animation.animateColorAsState\npackage com.example.ui.screens", "package com.example.ui.screens\nimport androidx.compose.animation.animateColorAsState\nimport androidx.compose.ui.graphics.drawscope.rotate")

# Check for rotate import
if "import androidx.compose.ui.graphics.drawscope.rotate" not in content:
    content = content.replace("package com.example.ui.screens", "package com.example.ui.screens\nimport androidx.compose.ui.graphics.drawscope.rotate")

with open('app/src/main/java/com/example/ui/screens/ControllerScreen.kt', 'w') as f:
    f.write(content)

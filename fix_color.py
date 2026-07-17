import re

with open('app/src/main/java/com/example/ui/screens/ControllerScreen.kt', 'r') as f:
    content = f.read()

# Make the blue plus inside Agent keys more visible
content = content.replace("Color(0xFF7986CB)", "Color(0xFF5C6BC0)")

with open('app/src/main/java/com/example/ui/screens/ControllerScreen.kt', 'w') as f:
    f.write(content)

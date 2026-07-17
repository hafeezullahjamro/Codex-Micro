with open('app/src/main/java/com/example/ui/screens/ControllerScreen.kt', 'r') as f:
    content = f.read()

content = content.replace("mappings[\"key_1\"]?.label", "mappings[\"key_1\"]?.name")
content = content.replace("mappings[\"key_2\"]?.label", "mappings[\"key_2\"]?.name")
content = content.replace("mappings[\"key_3\"]?.label", "mappings[\"key_3\"]?.name")
content = content.replace("mappings[\"key_4\"]?.label", "mappings[\"key_4\"]?.name")
content = content.replace("mappings[\"key_5\"]?.label", "mappings[\"key_5\"]?.name")
content = content.replace("mappings[\"key_6\"]?.label", "mappings[\"key_6\"]?.name")

content = content.replace("executeAction(", "transmitKeyPress(")

content = content.replace("Icons.Default.Bolt", "Icons.Default.FlashOn")
content = content.replace("Icons.Default.CheckCircleOutline", "Icons.Default.CheckCircle")
content = content.replace("Icons.Default.Cancel", "Icons.Default.Clear")
content = content.replace("Icons.Default.Shuffle", "Icons.Default.SyncAlt")

with open('app/src/main/java/com/example/ui/screens/ControllerScreen.kt', 'w') as f:
    f.write(content)

import re

with open('app/src/main/java/com/example/ui/screens/ControllerScreen.kt', 'r') as f:
    content = f.read()

# Row 1 and 2 keys
rep1 = """                        ImageTranslucentKey(statusColor = Color(0xFF1DE9B6), label = mappings["key_1"]?.name) { triggerFeedback(); viewModel.transmitKeyPress("key_1") }
                        ImageTranslucentKey(statusColor = Color(0xFFFFCA28), label = mappings["key_2"]?.name) { triggerFeedback(); viewModel.transmitKeyPress("key_2") }"""

rep2 = """                        ImageTranslucentKey(statusColor = Color(0xFF40C4FF), label = mappings["key_3"]?.name) { triggerFeedback(); viewModel.transmitKeyPress("key_3") }
                        ImageTranslucentKey(statusColor = Color.White, label = mappings["key_4"]?.name) { triggerFeedback(); viewModel.transmitKeyPress("key_4") }
                        ImageTranslucentKey(statusColor = Color(0xFF40C4FF), label = mappings["key_5"]?.name) { triggerFeedback(); viewModel.transmitKeyPress("key_5") }
                        ImageTranslucentKey(statusColor = Color(0xFFFF5252), label = mappings["key_6"]?.name) { triggerFeedback(); viewModel.transmitKeyPress("key_6") }"""

content = re.sub(r'ImageTranslucentKey\(statusColor = Color\(0xFF6EE7B7\).*?\n\s*ImageTranslucentKey\(statusColor = Color\(0xFFFDE047\).*?\}', rep1, content)
content = re.sub(r'ImageTranslucentKey\(statusColor = Color\(0xFF93C5FD\).*?\n\s*ImageTranslucentKey\(statusColor = Color\.White.*?\n\s*ImageTranslucentKey\(statusColor = Color\(0xFF93C5FD\).*?\n\s*ImageTranslucentKey\(statusColor = Color\(0xFFF9A8D4\).*?\}', rep2, content)

with open('app/src/main/java/com/example/ui/screens/ControllerScreen.kt', 'w') as f:
    f.write(content)

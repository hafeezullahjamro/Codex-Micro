import re

with open('app/src/main/java/com/example/ui/screens/ControllerScreen.kt', 'r') as f:
    content = f.read()

rep = """                        // Small black button and LEDs
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.width(56.dp)) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                LED(Color.White)
                                LED(Color.White)
                                LED(Color.Transparent)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(Color.Black, CircleShape)
                            )
                        }"""

content = re.sub(r'// Small black button and LEDs.*?Row\(verticalAlignment = Alignment\.CenterVertically\) \{\n.*?Column\(verticalArrangement = Arrangement\.spacedBy\(4\.dp\)\) \{\n.*?LED\(Color\.White\)\n.*?LED\(Color\.White\)\n.*?LED\(Color\.White\)\n.*?\}\n.*?Spacer\(modifier = Modifier\.width\(12\.dp\)\)\n.*?Box\(\n.*?modifier = Modifier\n.*?\.size\(24\.dp\)\n.*?\.background\(Color\.Black, CircleShape\)\n.*?\)[\s\n]*\}', rep, content, flags=re.DOTALL)

with open('app/src/main/java/com/example/ui/screens/ControllerScreen.kt', 'w') as f:
    f.write(content)

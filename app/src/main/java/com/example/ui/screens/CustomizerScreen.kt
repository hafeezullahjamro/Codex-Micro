package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ShortcutMapping
import com.example.ui.CodexViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizerScreen(
    viewModel: CodexViewModel,
    modifier: Modifier = Modifier
) {
    val mappings by viewModel.mappings.collectAsState()
    val selectedKeyId by viewModel.selectedKeyForEdit.collectAsState()

    // Key definition maps for rendering wireframe
    val gridLayout = listOf(
        listOf("knob_cw" to "Dial CW", "key_1_2" to "C1-R2", "key_1_3" to "C1-R3", "joy_up" to "Joy Up"),
        listOf("key_2_1" to "C2-R1", "key_2_2" to "C2-R2", "key_2_3" to "C2-R3", "key_2_4" to "C2-R4"),
        listOf("key_3_1" to "⚡", "key_3_2" to "✔️", "key_3_3" to "⊗", "key_3_4" to "⤏"),
        listOf("key_4_1" to "⚙️", "key_4_2" to "🎤 Wide Space", "key_4_2_dup" to "🎤 Wide Space", "key_4_4" to "🧠")
    )

    // Current editing mapping states
    var labelInput by remember { mutableStateOf("") }
    var isCtrlInput by remember { mutableStateOf(false) }
    var isShiftInput by remember { mutableStateOf(false) }
    var isAltInput by remember { mutableStateOf(false) }
    var isGuiInput by remember { mutableStateOf(false) }
    var keyCodeInput by remember { mutableStateOf(4) }
    var descInput by remember { mutableStateOf("") }

    // Update form when a key is selected
    LaunchedEffect(selectedKeyId, mappings) {
        selectedKeyId?.let { keyId ->
            // Skip dup mapping id for wireframe spacebar representation
            val targetKeyId = if (keyId == "key_4_2_dup") "key_4_2" else keyId
            val mapping = mappings[targetKeyId]
            if (mapping != null) {
                labelInput = mapping.name
                isCtrlInput = mapping.isCtrl
                isShiftInput = mapping.isShift
                isAltInput = mapping.isAlt
                isGuiInput = mapping.isGui
                keyCodeInput = mapping.keyCode
                descInput = mapping.description
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F1113))
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Description
            item {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Build, "Build", tint = Color(0xFFD0BCFF))
                        Text(
                            text = "SHORTCUT CUSTOMIZER",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tap any key on the interactive wireframe below to remap its Bluetooth HID command and display settings.",
                        color = Color(0x66FFFFFF),
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Interactive Wireframe Matrix Map
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1C1E)),
                    border = BorderStroke(1.dp, Color(0x0DFFFFFF))
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "INTERACTIVE DEVICE LAYOUT",
                            color = Color(0x4DFFFFFF),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Render 4 rows of wireframe buttons
                        gridLayout.forEach { row ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                row.forEach { (id, wireLabel) ->
                                    val isSelected = selectedKeyId == id || (id == "key_4_2" && selectedKeyId == "key_4_2_dup")
                                    val mapValue = mappings[if (id == "key_4_2_dup") "key_4_2" else id]
                                    val displayLabel = mapValue?.name ?: wireLabel

                                    Box(
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .size(
                                                width = if (id == "key_4_2" || id == "key_4_2_dup") 70.dp else 65.dp,
                                                height = 48.dp
                                            )
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                if (isSelected) Color(0xFFD0BCFF)
                                                else Color(0xFF2D2F31)
                                            )
                                            .border(
                                                width = 1.5.dp,
                                                color = if (isSelected) Color.White else Color(0x1AFFFFFF),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .clickable {
                                                viewModel.selectKeyForEdit(id)
                                            }
                                            .testTag("wireframe_$id"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = displayLabel,
                                            color = if (isSelected) Color.Black else Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                            maxLines = 2
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Editor Panel
            selectedKeyId?.let { keyId ->
                val targetKeyId = if (keyId == "key_4_2_dup") "key_4_2" else keyId
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1C1E)),
                        border = BorderStroke(1.dp, Color(0x0DFFFFFF))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "MAPPING SETTINGS: ${targetKeyId.uppercase()}",
                                color = Color(0xFFD0BCFF),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )

                            // Label input field
                            OutlinedTextField(
                                value = labelInput,
                                onValueChange = { labelInput = it },
                                label = { Text("Display Label (on-screen)", fontFamily = FontFamily.Monospace) },
                                modifier = Modifier.fillMaxWidth().testTag("edit_label"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFD0BCFF),
                                    focusedLabelColor = Color(0xFFD0BCFF),
                                    cursorColor = Color(0xFFD0BCFF)
                                )
                            )

                            // Description input field
                            OutlinedTextField(
                                value = descInput,
                                onValueChange = { descInput = it },
                                label = { Text("Function Description", fontFamily = FontFamily.Monospace) },
                                modifier = Modifier.fillMaxWidth().testTag("edit_description"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFD0BCFF),
                                    focusedLabelColor = Color(0xFFD0BCFF),
                                    cursorColor = Color(0xFFD0BCFF)
                                )
                            )

                            // Modifier Checkboxes
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "USB HID Modifier Keys:",
                                    color = Color(0x66FFFFFF),
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )

                                val modifiers = listOf(
                                    Quad("Ctrl (Control)", isCtrlInput, { v: Boolean -> isCtrlInput = v }, "check_ctrl"),
                                    Quad("Shift", isShiftInput, { v: Boolean -> isShiftInput = v }, "check_shift"),
                                    Quad("Alt (Option)", isAltInput, { v: Boolean -> isAltInput = v }, "check_alt"),
                                    Quad("Meta (Win / Cmd)", isGuiInput, { v: Boolean -> isGuiInput = v }, "check_gui")
                                )

                                modifiers.forEach { (label, checked, onCheckedChange, tag) ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onCheckedChange(!checked) }
                                            .padding(vertical = 4.dp)
                                    ) {
                                        Checkbox(
                                            checked = checked,
                                            onCheckedChange = onCheckedChange,
                                            modifier = Modifier.testTag(tag),
                                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFFD0BCFF))
                                        )
                                        Text(
                                            text = label,
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontFamily = FontFamily.Monospace,
                                            modifier = Modifier.padding(start = 8.dp)
                                        )
                                    }
                                }
                            }

                            // KeyCode selection (Simple Slider/Input for versatility)
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "USB HID Scan Code: $keyCodeInput",
                                        color = Color(0x66FFFFFF),
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = getScanCodeName(keyCodeInput),
                                        color = Color(0xFFD0BCFF),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Slider(
                                    value = keyCodeInput.toFloat(),
                                    onValueChange = { keyCodeInput = it.toInt() },
                                    valueRange = 4f..101f, // Standard USB HID keyboard scan range
                                    colors = SliderDefaults.colors(
                                        thumbColor = Color(0xFFD0BCFF),
                                        activeTrackColor = Color(0xFFD0BCFF)
                                    ),
                                    modifier = Modifier.testTag("scancode_slider")
                                )
                            }

                            // Action buttons: Save, Reset to Default, Cancel
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.updateKeyMapping(
                                            targetKeyId, labelInput, isCtrlInput, isShiftInput, isAltInput, isGuiInput, keyCodeInput, descInput
                                        )
                                        viewModel.selectKeyForEdit(null) // Close form
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF34D399)),
                                    modifier = Modifier.weight(1f).testTag("btn_save_mapping")
                                ) {
                                    Icon(Icons.Default.Check, "Save", modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Save", fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                                }

                                Button(
                                    onClick = {
                                        viewModel.resetKeyToDefault(targetKeyId)
                                        viewModel.selectKeyForEdit(null) // Close form
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                                    modifier = Modifier.weight(1f).testTag("btn_reset_mapping")
                                ) {
                                    Icon(Icons.Default.Refresh, "Reset", modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Default", fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Custom data holder helper
data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

fun getScanCodeName(code: Int): String {
    return when (code) {
        4 -> "Key 'A'"
        5 -> "Key 'B'"
        6 -> "Key 'C'"
        7 -> "Key 'D'"
        8 -> "Key 'E'"
        9 -> "Key 'F'"
        10 -> "Key 'G'"
        11 -> "Key 'H'"
        12 -> "Key 'I'"
        13 -> "Key 'J'"
        14 -> "Key 'K'"
        15 -> "Key 'L'"
        16 -> "Key 'M'"
        17 -> "Key 'N'"
        18 -> "Key 'O'"
        19 -> "Key 'P'"
        20 -> "Key 'Q'"
        21 -> "Key 'R'"
        22 -> "Key 'S'"
        23 -> "Key 'T'"
        24 -> "Key 'U'"
        25 -> "Key 'V'"
        26 -> "Key 'W'"
        27 -> "Key 'X'"
        28 -> "Key 'Y'"
        29 -> "Key 'Z'"
        39 -> "Key '0'"
        40 -> "Key 'Enter'"
        41 -> "Key 'Escape'"
        42 -> "Key 'Backspace'"
        43 -> "Key 'Tab'"
        44 -> "Key 'Spacebar'"
        45 -> "Key 'Minus (-)'"
        46 -> "Key 'Equal (=)'"
        47 -> "Key 'Left Bracket ['"
        48 -> "Key 'Right Bracket ]'"
        49 -> "Key 'Backslash \\'"
        51 -> "Key 'Semicolon ;'"
        52 -> "Key 'Apostrophe ''"
        53 -> "Key 'Grave Accent / Tilde `'"
        54 -> "Key 'Comma ,'"
        55 -> "Key 'Period .'"
        56 -> "Key 'Forward Slash /'"
        57 -> "Key 'Caps Lock'"
        else -> "Key code #$code"
    }
}

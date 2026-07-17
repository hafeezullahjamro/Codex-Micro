package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.CodexViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestureScreen(
    viewModel: CodexViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isFloatingEnabled by viewModel.isFloatingMenuEnabled.collectAsState()

    var showOverlayDialog by remember { mutableStateOf(false) }

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
            // Header Info
            item {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessibilityNew,
                            contentDescription = "Accessibility",
                            tint = Color(0xFFD0BCFF),
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = "ACCESSIBILITY ENGRAVINGS",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Customize touch widgets, gestures, and system overlays to achieve optimal one-handed phone accessibility control.",
                        color = Color(0x66FFFFFF),
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Floating Codex Menu Control Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1C1E)),
                    border = BorderStroke(1.dp, Color(0x0DFFFFFF))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(0.7f)) {
                                Text(
                                    text = "Floating Codex Menu",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Launch a system-wide floating widget representing Micro Codex shortcuts. Access shortcuts while using any application on your device.",
                                    color = Color(0x66FFFFFF),
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Switch(
                                checked = isFloatingEnabled,
                                onCheckedChange = { enable ->
                                    if (enable) {
                                        // Verify system draw overlay permission
                                        if (!Settings.canDrawOverlays(context)) {
                                            showOverlayDialog = true
                                        } else {
                                            viewModel.toggleFloatingMenu(true)
                                        }
                                    } else {
                                        viewModel.toggleFloatingMenu(false)
                                    }
                                },
                                modifier = Modifier.testTag("switch_floating_menu"),
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFFD0BCFF),
                                    checkedTrackColor = Color(0x80D0BCFF)
                                )
                            )
                        }

                        // Status of permission
                        val overlayGranted = Settings.canDrawOverlays(context)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (overlayGranted) Color(0x1334D399) else Color(0x14D32F2F))
                                .border(
                                    1.dp,
                                    if (overlayGranted) Color(0x3D34D399) else Color(0x3DD32F2F),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(10.dp)
                        ) {
                            Text(
                                text = if (overlayGranted) "✓ System Draw Overlays authorized" else "⚠ System Draw Overlays restricted",
                                color = if (overlayGranted) Color(0xFF34D399) else Color(0xFFE53935),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Gesture Configuration Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1C1E)),
                    border = BorderStroke(1.dp, Color(0x0DFFFFFF))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "ACCESSIBILITY GESTURE TRIGGERS",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Map standard swipe gestures from screen edges directly to virtual micro codex shortcuts for seamless navigation.",
                            color = Color(0x66FFFFFF),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )

                        Divider(color = Color(0x1FAEC6B6))

                        GestureSettingRow("Double-tap Dial Knob", "Ctrl + + / Ctrl + - (Zoom Increments)", "knob_cw")
                        GestureSettingRow("Swipe Left on housing", "Page Navigation: Settings Screen", "nav_left")
                        GestureSettingRow("Swipe Right on housing", "Page Navigation: Customizer Screen", "nav_right")
                        GestureSettingRow("Holding Spacebar 1.5s", "Toggle Status LEDs", "led_all")
                    }
                }
            }
        }
    }

    // Permission Dialog
    if (showOverlayDialog) {
        AlertDialog(
            onDismissRequest = { showOverlayDialog = false },
            title = {
                Text(
                    text = "Overlay Permission Required",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "To enable the floating micro codex assistant overlay, you must authorize 'Draw over other apps' in your device settings. This allows the widget to render over other application windows.",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showOverlayDialog = false
                        val intent = Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:${context.packageName}")
                        )
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF34D399))
                ) {
                    Icon(Icons.Default.Settings, "Settings", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Grant Permission", fontFamily = FontFamily.Monospace)
                }
            },
            dismissButton = {
                TextButton(onClick = { showOverlayDialog = false }) {
                    Text("Cancel", fontFamily = FontFamily.Monospace)
                }
            },
            containerColor = Color(0xFF1A1C1E),
            titleContentColor = Color.White,
            textContentColor = Color(0x66FFFFFF)
        )
    }
}

@Composable
fun GestureSettingRow(
    gesture: String,
    action: String,
    tag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF2D2F31))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = gesture,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Action: $action",
                color = Color(0xFFD0BCFF),
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0x33D0BCFF))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = "ACTIVE",
                color = Color(0xFFD0BCFF),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

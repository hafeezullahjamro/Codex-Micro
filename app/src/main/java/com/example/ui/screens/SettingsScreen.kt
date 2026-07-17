package com.example.ui.screens

import android.bluetooth.BluetoothDevice
import android.os.Build
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.CodexViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: CodexViewModel,
    modifier: Modifier = Modifier
) {
    val status by viewModel.bluetoothController.status.collectAsState()
    val pairedDevices by viewModel.bluetoothController.pairedDevices.collectAsState()
    val connectedDeviceName by viewModel.bluetoothController.connectedDeviceName.collectAsState()

    var showGuide by remember { mutableStateOf(false) }

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
            // Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1C1E)),
                    border = BorderStroke(1.dp, Color(0x0DFFFFFF))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bluetooth,
                                contentDescription = "Bluetooth Status",
                                tint = Color(0xFFD0BCFF),
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                text = "CONNECTION MANAGER",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Status Indicator Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x1FD0BCFF))
                                .border(1.dp, Color(0x3DD0BCFF), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(RoundedCornerShape(50))
                                        .background(
                                            if (connectedDeviceName != null) Color(0xFF34D399)
                                            else if (status.contains("Active") || status.contains("Ready")) Color(0xFFFFB300)
                                            else Color(0xFFE53935)
                                        )
                                )
                                Text(
                                    text = status,
                                    color = Color(0x66FFFFFF),
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Trigger Device Loading or Simulation Mode
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.bluetoothController.loadPairedDevices()
                                    viewModel.bluetoothController.connectProfile()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                                modifier = Modifier.weight(1f).testTag("btn_reconnect_profile")
                            ) {
                                Icon(Icons.Default.Refresh, "Refresh", modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Bind Profile", fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                            }

                            Button(
                                onClick = {
                                    viewModel.bluetoothController.simulateConnection()
                                    viewModel.addLog("Simulation Mode Activated.")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF37474F)),
                                modifier = Modifier.weight(1f).testTag("btn_simulate_link")
                            ) {
                                Icon(Icons.Default.PlayArrow, "Simulate", modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Simulate Link", fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
                }
            }

            // Connection Guide Toggle
            item {
                Card(
                    onClick = { showGuide = !showGuide },
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1C1E)),
                    border = BorderStroke(1.dp, Color(0x0DFFFFFF))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Default.Info, "Guide", tint = Color(0xFFFFB300))
                            Text(
                                text = "Pairing Guide (Offline Setup)",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 14.sp
                            )
                        }
                        Icon(
                            imageVector = if (showGuide) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Toggle",
                            tint = Color.White
                        )
                    }
                }
            }

            // Expanded Guide Content
            if (showGuide) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1C1E)),
                        border = BorderStroke(1.dp, Color(0x0DFFFFFF))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "How to connect offline:",
                                color = Color(0xFFD0BCFF),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "1. Open your Computer / Host bluetooth settings.\n" +
                                        "2. Search for new Bluetooth devices.\n" +
                                        "3. Select 'Micro Codex Controller' from your computer's scanner.\n" +
                                        "4. Pair the devices. When prompted, accept all permissions.\n" +
                                        "5. Once paired, it will appear under the 'Paired Devices' list below.\n" +
                                        "6. Click on the device in the list to bind the virtual micro codex link!\n" +
                                        "7. Now, pressing on-screen buttons transmits physical keystrokes instantly over the system-level HID keyboard standard.",
                                color = Color(0x66FFFFFF),
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            // Paired Devices List Section
            item {
                Text(
                    text = "BONDED / PAIRED SYSTEMS",
                    color = Color(0x66FFFFFF),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            if (pairedDevices.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1A1C1E))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Warning, "No paired devices", tint = Color(0x1AFFFFFF), modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No paired Bluetooth systems found.\nVerify Bluetooth is enabled in your Android Quick Settings panel.",
                                color = Color(0x4DFFFFFF),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(pairedDevices) { device ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1A1C1E))
                            .border(
                                1.dp,
                                if (connectedDeviceName == device.name) Color(0xFFD0BCFF) else Color(0x0DFFFFFF),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                    viewModel.bluetoothController.connectToDevice(device)
                                    viewModel.addLog("Connecting to physical device: ${device.name}")
                                } else {
                                    viewModel.addLog("Physical HID connection not supported below Android Pie.")
                                }
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Computer,
                                contentDescription = "Device icon",
                                tint = if (connectedDeviceName == device.name) Color(0xFFD0BCFF) else Color(0x4DFFFFFF)
                            )
                            Column {
                                Text(
                                    text = device.name ?: "Unknown Device",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = device.address,
                                    color = Color(0x4DFFFFFF),
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        if (connectedDeviceName == device.name) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0x3334D399))
                                    .border(1.dp, Color(0xFF34D399), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "LINK ACTIVE",
                                    color = Color(0xFF34D399),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Connect",
                                tint = Color(0x4DFFFFFF)
                            )
                        }
                    }
                }
            }
        }
    }
}

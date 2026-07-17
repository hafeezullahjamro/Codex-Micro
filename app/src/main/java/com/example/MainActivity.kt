package com.example

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bluetooth.FloatingCodexService
import com.example.ui.CodexViewModel
import com.example.ui.screens.ControllerScreen
import com.example.ui.screens.CustomizerScreen
import com.example.ui.screens.GestureScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val allGranted = results.values.all { it }
        if (allGranted) {
            // Re-connect Bluetooth service proxy
            // viewModel or other service can trigger reload
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Launch Bluetooth Permission Request
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                android.Manifest.permission.BLUETOOTH_CONNECT,
                android.Manifest.permission.BLUETOOTH_ADVERTISE,
                android.Manifest.permission.BLUETOOTH_SCAN
            )
        } else {
            arrayOf(
                android.Manifest.permission.BLUETOOTH,
                android.Manifest.permission.BLUETOOTH_ADMIN
            )
        }
        permissionLauncher.launch(permissions)

        setContent {
            MyApplicationTheme {
                val viewModel: CodexViewModel = viewModel()
                MainAppContainer(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainAppContainer(
    viewModel: CodexViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Four-page horizontal pager (Central Controller is Page 1, zero-indexed)
    val pagerState = rememberPagerState(initialPage = 1, pageCount = { 4 })
    val isFloatingEnabled by viewModel.isFloatingMenuEnabled.collectAsState()

    // Control background Service when overlay toggle changes
    LaunchedEffect(isFloatingEnabled) {
        if (Settings.canDrawOverlays(context)) {
            val serviceIntent = Intent(context, FloatingCodexService::class.java)
            if (isFloatingEnabled) {
                context.startService(serviceIntent)
            } else {
                context.stopService(serviceIntent)
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            // Slide transition: Hide Navigation Tab bar when on the Controller Page (Page 1)
            // This maintains a pristine main screen containing ONLY the controller buttons
            AnimatedVisibility(
                visible = pagerState.currentPage != 1,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                NavigationBar(
                    containerColor = Color(0xFF1A1C1E),
                    contentColor = Color(0x66FFFFFF),
                    modifier = Modifier.testTag("app_navigation_bar")
                ) {
                    NavigationBarItem(
                        selected = pagerState.currentPage == 0,
                        onClick = { scope.launch { pagerState.animateScrollToPage(0) } },
                        icon = { Icon(Icons.Default.Power, "Link") },
                        label = { Text("Link", fontFamily = FontFamily.Monospace, fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFD0BCFF),
                            selectedTextColor = Color(0xFFD0BCFF),
                            indicatorColor = Color.Transparent,
                            unselectedIconColor = Color(0x66FFFFFF),
                            unselectedTextColor = Color(0x66FFFFFF)
                        ),
                        modifier = Modifier.testTag("tab_settings")
                    )

                    NavigationBarItem(
                        selected = pagerState.currentPage == 1,
                        onClick = { scope.launch { pagerState.animateScrollToPage(1) } },
                        icon = { Icon(Icons.Default.VideogameAsset, "Controller") },
                        label = { Text("Chassis", fontFamily = FontFamily.Monospace, fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFD0BCFF),
                            selectedTextColor = Color(0xFFD0BCFF),
                            indicatorColor = Color.Transparent,
                            unselectedIconColor = Color(0x66FFFFFF),
                            unselectedTextColor = Color(0x66FFFFFF)
                        ),
                        modifier = Modifier.testTag("tab_controller")
                    )

                    NavigationBarItem(
                        selected = pagerState.currentPage == 2,
                        onClick = { scope.launch { pagerState.animateScrollToPage(2) } },
                        icon = { Icon(Icons.Default.Build, "Custom") },
                        label = { Text("Remap", fontFamily = FontFamily.Monospace, fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFD0BCFF),
                            selectedTextColor = Color(0xFFD0BCFF),
                            indicatorColor = Color.Transparent,
                            unselectedIconColor = Color(0x66FFFFFF),
                            unselectedTextColor = Color(0x66FFFFFF)
                        ),
                        modifier = Modifier.testTag("tab_customizer")
                    )

                    NavigationBarItem(
                        selected = pagerState.currentPage == 3,
                        onClick = { scope.launch { pagerState.animateScrollToPage(3) } },
                        icon = { Icon(Icons.Default.AccessibilityNew, "Assist") },
                        label = { Text("Assist", fontFamily = FontFamily.Monospace, fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFD0BCFF),
                            selectedTextColor = Color(0xFFD0BCFF),
                            indicatorColor = Color.Transparent,
                            unselectedIconColor = Color(0x66FFFFFF),
                            unselectedTextColor = Color(0x66FFFFFF)
                        ),
                        modifier = Modifier.testTag("tab_gestures")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F1113))
                .padding(bottom = if (pagerState.currentPage != 1) innerPadding.calculateBottomPadding() else 0.dp)
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> SettingsScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
                    1 -> ControllerScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
                    2 -> CustomizerScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
                    3 -> GestureScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
                }
            }

            // Floating Minimal Swipe Indicator (Rendered at the top ONLY on Page 1 to let users know how to navigate out)
            if (pagerState.currentPage == 1) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 20.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0x991A1C1E))
                        .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(20.dp))
                        .clickable {
                            scope.launch { pagerState.animateScrollToPage(0) }
                        }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "← Swipe left or right to reconfigure keys →",
                        color = Color(0x66FFFFFF),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

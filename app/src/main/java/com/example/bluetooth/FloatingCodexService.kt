package com.example.bluetooth

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import com.example.bluetooth.BluetoothHidKeyboardController
import kotlin.math.roundToInt

class FloatingCodexService : Service(), LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {

    private lateinit var windowManager: WindowManager
    private var floatingView: View? = null
    private var bluetoothController: BluetoothHidKeyboardController? = null

    // Setup Custom Lifecycle and Saved State Providers for Compose WindowManager compat
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val store = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry
    override val viewModelStore: ViewModelStore
        get() = store
    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        bluetoothController = BluetoothHidKeyboardController(this).apply {
            connectProfile()
        }

        if (Settings.canDrawOverlays(this)) {
            showFloatingBubble()
        }
    }

    private fun showFloatingBubble() {
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100
            y = 200
        }

        val composeView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@FloatingCodexService)
            setViewTreeViewModelStoreOwner(this@FloatingCodexService)
            setViewTreeSavedStateRegistryOwner(this@FloatingCodexService)
            setContent {
                FloatingBubbleUI(
                    onDrag = { dx, dy ->
                        params.x += dx.roundToInt()
                        params.y += dy.roundToInt()
                        windowManager.updateViewLayout(this, params)
                    },
                    onTriggerKeyPress = { modifiers, keycode ->
                        bluetoothController?.sendKeyPress(modifiers, keycode)
                    }
                )
            }
        }

        floatingView = composeView
        windowManager.addView(composeView, params)
    }

    override fun onDestroy() {
        super.onDestroy()
        floatingView?.let {
            windowManager.removeView(it)
        }
        bluetoothController?.disconnectProfile()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }
}

@Composable
fun FloatingBubbleUI(
    onDrag: (Float, Float) -> Unit,
    onTriggerKeyPress: (Byte, Byte) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.wrapContentSize()
    ) {
        if (expanded) {
            // Floating Mini Quick-Access Menu (Aesthetic Translucent Panels)
            Box(
                modifier = Modifier
                    .width(160.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xE60F1210))
                    .border(1.5.dp, Color(0xFFD0BCFF), RoundedCornerShape(16.dp))
                    .padding(8.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "CODEX OVERLAY",
                        color = Color(0xFFD0BCFF),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    val shortcuts = listOf(
                        Triple("Alt + Space", 0x04.toByte(), 44.toByte()),
                        Triple("Ctrl + Shift + P", 0x03.toByte(), 19.toByte()),
                        Triple("Ctrl + O", 0x01.toByte(), 18.toByte()),
                        Triple("Ctrl + N", 0x01.toByte(), 17.toByte()),
                        Triple("Ctrl + Shift + D", 0x03.toByte(), 7.toByte())
                    )

                    shortcuts.forEach { (name, mods, keycode) ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF2D2F31))
                                .clickable {
                                    onTriggerKeyPress(mods, keycode)
                                }
                                .padding(vertical = 6.dp, horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = name,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // Close Menu Option
                    Text(
                        text = "Collapse Menu",
                        color = Color(0xFFE53935),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .clickable { expanded = false }
                            .padding(vertical = 4.dp)
                    )
                }
            }
        } else {
            // Floating Draggy Bubble (The Micro Codex central glowing node)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .shadowBehind()
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFFD0BCFF), Color(0xFF102A20))
                        )
                    )
                    .border(2.dp, Color.White, CircleShape)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            onDrag(dragAmount.x, dragAmount.y)
                        }
                    }
                    .clickable { expanded = true },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⚙",
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Custom simple modifier function to draw standard outer shadows dynamically
fun Modifier.shadowBehind(): Modifier = this.drawBehind {
    drawCircle(
        color = Color(0x3F00FFD8),
        radius = size.minDimension / 2f + 8.dp.toPx()
    )
}

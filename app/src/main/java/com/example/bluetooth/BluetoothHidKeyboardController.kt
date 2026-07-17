package com.example.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.Executors

@SuppressLint("MissingPermission")
class BluetoothHidKeyboardController(private val context: Context) {

    private val TAG = "HidKeyboardController"

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        manager?.adapter ?: BluetoothAdapter.getDefaultAdapter()
    }

    private var hidDevice: BluetoothHidDevice? = null
    private var connectedDevice: BluetoothDevice? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    private val _isSupported = MutableStateFlow(false)
    val isSupported: StateFlow<Boolean> = _isSupported

    private val _status = MutableStateFlow("Initializing...")
    val status: StateFlow<String> = _status

    private val _pairedDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val pairedDevices: StateFlow<List<BluetoothDevice>> = _pairedDevices

    private val _connectedDeviceName = MutableStateFlow<String?>(null)
    val connectedDeviceName: StateFlow<String?> = _connectedDeviceName

    private var isAppRegistered = false

    // Bluetooth HID SDP Descriptor for Keyboard and Mouse (Report ID 1 = Keyboard, Report ID 2 = Mouse)
    private val reportMap = byteArrayOf(
        // Keyboard (Report ID 1)
        0x05.toByte(), 0x01.toByte(),     // Usage Page (Generic Desktop)
        0x09.toByte(), 0x06.toByte(),     // Usage (Keyboard)
        0xa1.toByte(), 0x01.toByte(),     // Collection (Application)
        0x85.toByte(), 0x01.toByte(),     //   Report Id (1)
        0x05.toByte(), 0x07.toByte(),     //   Usage Page (Keyboard/Keypad)
        0x19.toByte(), 0xe0.toByte(),     //   Usage Minimum (Keyboard LeftControl)
        0x29.toByte(), 0xe7.toByte(),     //   Usage Maximum (Keyboard Right GUI)
        0x15.toByte(), 0x00.toByte(),     //   Logical Minimum (0)
        0x25.toByte(), 0x01.toByte(),     //   Logical Maximum (1)
        0x75.toByte(), 0x01.toByte(),     //   Report Size (1)
        0x95.toByte(), 0x08.toByte(),     //   Report Count (8)
        0x81.toByte(), 0x02.toByte(),     //   Input (Data, Variable, Absolute) - Modifier byte
        0x95.toByte(), 0x01.toByte(),     //   Report Count (1)
        0x75.toByte(), 0x08.toByte(),     //   Report Size (8)
        0x81.toByte(), 0x01.toByte(),     //   Input (Constant) - Reserved byte
        0x95.toByte(), 0x05.toByte(),     //   Report Count (5)
        0x75.toByte(), 0x01.toByte(),     //   Report Size (1)
        0x05.toByte(), 0x08.toByte(),     //   Usage Page (LEDs)
        0x19.toByte(), 0x01.toByte(),     //   Usage Minimum (Num Lock)
        0x29.toByte(), 0x05.toByte(),     //   Usage Maximum (Kana)
        0x91.toByte(), 0x02.toByte(),     //   Output (Data, Variable, Absolute) - LED Report
        0x95.toByte(), 0x01.toByte(),     //   Report Count (1)
        0x75.toByte(), 0x03.toByte(),     //   Report Size (3)
        0x91.toByte(), 0x01.toByte(),     //   Output (Constant) - LED Report padding
        0x95.toByte(), 0x06.toByte(),     //   Report Count (6)
        0x75.toByte(), 0x08.toByte(),     //   Report Size (8)
        0x15.toByte(), 0x00.toByte(),     //   Logical Minimum (0)
        0x25.toByte(), 0x65.toByte(),     //   Logical Maximum (101)
        0x05.toByte(), 0x07.toByte(),     //   Usage Page (Keyboard/Keypad)
        0x19.toByte(), 0x00.toByte(),     //   Usage Minimum (Reserved)
        0x29.toByte(), 0x65.toByte(),     //   Usage Maximum (Keyboard Application)
        0x81.toByte(), 0x00.toByte(),     //   Input (Data, Array, Absolute) - 6 Key bytes
        0xc0.toByte(),                    // End Collection

        // Mouse (Report ID 2)
        0x05.toByte(), 0x01.toByte(),     // Usage Page (Generic Desktop)
        0x09.toByte(), 0x02.toByte(),     // Usage (Mouse)
        0xa1.toByte(), 0x01.toByte(),     // Collection (Application)
        0x09.toByte(), 0x01.toByte(),     //   Usage (Pointer)
        0xa1.toByte(), 0x00.toByte(),     //   Collection (Physical)
        0x85.toByte(), 0x02.toByte(),     //     Report ID (2)
        0x05.toByte(), 0x09.toByte(),     //     Usage Page (Buttons)
        0x19.toByte(), 0x01.toByte(),     //     Usage Minimum (Button 1)
        0x29.toByte(), 0x03.toByte(),     //     Usage Maximum (Button 3)
        0x15.toByte(), 0x00.toByte(),     //     Logical Minimum (0)
        0x25.toByte(), 0x01.toByte(),     //     Logical Maximum (1)
        0x75.toByte(), 0x01.toByte(),     //     Report Size (1)
        0x95.toByte(), 0x03.toByte(),     //     Report Count (3)
        0x81.toByte(), 0x02.toByte(),     //     Input (Data, Variable, Absolute) - Buttons
        0x95.toByte(), 0x01.toByte(),     //     Report Count (1)
        0x75.toByte(), 0x05.toByte(),     //     Report Size (5)
        0x81.toByte(), 0x01.toByte(),     //     Input (Constant) - Button padding
        0x05.toByte(), 0x01.toByte(),     //     Usage Page (Generic Desktop)
        0x09.toByte(), 0x30.toByte(),     //     Usage (X)
        0x09.toByte(), 0x31.toByte(),     //     Usage (Y)
        0x15.toByte(), 0x81.toByte(),     //     Logical Minimum (-127)
        0x25.toByte(), 0x7f.toByte(),     //     Logical Maximum (127)
        0x75.toByte(), 0x08.toByte(),     //     Report Size (8)
        0x95.toByte(), 0x02.toByte(),     //     Report Count (2)
        0x81.toByte(), 0x06.toByte(),     //     Input (Data, Variable, Relative) - Coordinates
        0x09.toByte(), 0x38.toByte(),     //     Usage (Wheel)
        0x15.toByte(), 0x81.toByte(),     //     Logical Minimum (-127)
        0x25.toByte(), 0x7f.toByte(),     //     Logical Maximum (127)
        0x75.toByte(), 0x08.toByte(),     //     Report Size (8)
        0x95.toByte(), 0x01.toByte(),     //     Report Count (1)
        0x81.toByte(), 0x06.toByte(),     //     Input (Data, Variable, Relative) - Scroll wheel
        0xc0.toByte(),                    //   End Collection
        0xc0.toByte()                     // End Collection
    )

    init {
        checkSupport()
    }

    private fun checkSupport() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            _isSupported.value = false
            _status.value = "Requires Android 9+ (API 28+)"
            return
        }
        if (bluetoothAdapter == null) {
            _isSupported.value = false
            _status.value = "Bluetooth is not supported"
            return
        }
        _isSupported.value = true
        _status.value = "Ready to connect profile"
        loadPairedDevices()
    }

    fun loadPairedDevices() {
        try {
            val paired = bluetoothAdapter?.bondedDevices?.toList() ?: emptyList()
            _pairedDevices.value = paired
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load paired devices", e)
        }
    }

    @SuppressLint("NewApi")
    fun connectProfile() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P || bluetoothAdapter == null) {
            _status.value = "Simulation Link (Bluetooth HID Offline)"
            return
        }

        try {
            _status.value = "Binding Bluetooth HID Profile..."
            val success = bluetoothAdapter?.getProfileProxy(
                context,
                profileListener,
                BluetoothProfile.HID_DEVICE
            ) ?: false

            if (!success) {
                _status.value = "Skeuomorphic Simulation Mode (Hardware restricted)"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error binding profile proxy", e)
            _status.value = "Simulation Link (Exception: ${e.localizedMessage})"
        }
    }

    fun disconnectProfile() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && bluetoothAdapter != null && hidDevice != null) {
            try {
                unregisterApp()
                bluetoothAdapter?.closeProfileProxy(BluetoothProfile.HID_DEVICE, hidDevice)
                hidDevice = null
                _status.value = "Disconnected"
            } catch (e: Exception) {
                Log.e(TAG, "Error closing proxy", e)
            }
        }
    }

    private val profileListener = object : BluetoothProfile.ServiceListener {
        @RequiresApi(Build.VERSION_CODES.P)
        override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
            if (profile == BluetoothProfile.HID_DEVICE) {
                Log.d(TAG, "HID Device service connected")
                hidDevice = proxy as BluetoothHidDevice
                registerApp()
            }
        }

        override fun onServiceDisconnected(profile: Int) {
            if (profile == BluetoothProfile.HID_DEVICE) {
                Log.d(TAG, "HID Device service disconnected")
                hidDevice = null
                isAppRegistered = false
                _status.value = "Service disconnected"
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun registerApp() {
        val hid = hidDevice ?: return
        val sdp = BluetoothHidDeviceAppSdpSettings(
            "Micro Codex Controller",
            "Virtual Custom Macropad Keyboard",
            "Work Louder Virtual Controller",
            BluetoothHidDevice.SUBCLASS1_COMBO,
            reportMap
        )

        try {
            hid.registerApp(sdp, null, null, Executors.newSingleThreadExecutor(), callback)
        } catch (e: Exception) {
            Log.e(TAG, "registerApp failed", e)
            _status.value = "SDP registration failed"
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun unregisterApp() {
        if (isAppRegistered) {
            try {
                hidDevice?.unregisterApp()
                isAppRegistered = false
            } catch (e: Exception) {
                Log.e(TAG, "unregisterApp failed", e)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private val callback = object : BluetoothHidDevice.Callback() {
        override fun onAppStatusChanged(pluggedDevice: BluetoothDevice?, registered: Boolean) {
            Log.d(TAG, "onAppStatusChanged: registered = $registered")
            isAppRegistered = registered
            mainHandler.post {
                if (registered) {
                    _status.value = "Bluetooth HID Active. Ready to Pair!"
                } else {
                    _status.value = "HID Service un-registered"
                }
            }
        }

        override fun onConnectionStateChanged(device: BluetoothDevice?, state: Int) {
            Log.d(TAG, "onConnectionStateChanged: state = $state")
            mainHandler.post {
                when (state) {
                    BluetoothProfile.STATE_CONNECTED -> {
                        connectedDevice = device
                        _connectedDeviceName.value = device?.name ?: "Unknown Device"
                        _status.value = "Connected to computer: ${device?.name}"
                    }
                    BluetoothProfile.STATE_CONNECTING -> {
                        _status.value = "Connecting to computer..."
                    }
                    BluetoothProfile.STATE_DISCONNECTING -> {
                        _status.value = "Disconnecting..."
                    }
                    BluetoothProfile.STATE_DISCONNECTED -> {
                        connectedDevice = null
                        _connectedDeviceName.value = null
                        _status.value = "Bluetooth HID Active (Ready to connect)"
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun connectToDevice(device: BluetoothDevice) {
        val hid = hidDevice ?: return
        try {
            _status.value = "Connecting to ${device.name}..."
            hid.connect(device)
        } catch (e: Exception) {
            Log.e(TAG, "connect failed", e)
            _status.value = "Connection failed"
        }
    }

    fun simulateConnection() {
        _connectedDeviceName.value = "My Computer (Simulated)"
        _status.value = "Connected (Simulated Over-the-air Link)"
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun disconnectDevice() {
        val hid = hidDevice ?: return
        val device = connectedDevice ?: return
        try {
            hid.disconnect(device)
        } catch (e: Exception) {
            Log.e(TAG, "disconnect failed", e)
        }
    }

    // Sends a keypress: Modifier + Scan code, wait 30ms, then release.
    fun sendKeyPress(modifiers: Byte, keyCode: Byte) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val hid = hidDevice
            val device = connectedDevice
            if (hid != null && device != null) {
                Executors.newSingleThreadExecutor().execute {
                    try {
                        // Keydown Report ID 1
                        val reportDown = ByteArray(8)
                        reportDown[0] = modifiers
                        reportDown[2] = keyCode
                        hid.sendReport(device, 1, reportDown)

                        Thread.sleep(30)

                        // Keyup
                        val reportUp = ByteArray(8)
                        hid.sendReport(device, 1, reportUp)
                    } catch (e: Exception) {
                        Log.e(TAG, "sendKeyPress failed", e)
                    }
                }
                return
            }
        }
        Log.d(TAG, "[Simulated Keypress] Modifiers: $modifiers, KeyCode: $keyCode")
    }

    // Mouse control: scroll (-127 to 127)
    fun sendMouseScroll(scroll: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val hid = hidDevice
            val device = connectedDevice
            if (hid != null && device != null) {
                Executors.newSingleThreadExecutor().execute {
                    try {
                        val report = ByteArray(5)
                        report[4] = scroll.coerceIn(-127, 127).toByte()
                        hid.sendReport(device, 2, report) // Report ID 2
                    } catch (e: Exception) {
                        Log.e(TAG, "sendMouseScroll failed", e)
                    }
                }
                return
            }
        }
        Log.d(TAG, "[Simulated Scroll] Scroll: $scroll")
    }

    // Mouse movement
    fun sendMouseMove(dx: Int, dy: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val hid = hidDevice
            val device = connectedDevice
            if (hid != null && device != null) {
                Executors.newSingleThreadExecutor().execute {
                    try {
                        val report = ByteArray(5)
                        report[1] = dx.coerceIn(-127, 127).toByte()
                        report[2] = dy.coerceIn(-127, 127).toByte()
                        hid.sendReport(device, 2, report)
                    } catch (e: Exception) {
                        Log.e(TAG, "sendMouseMove failed", e)
                    }
                }
                return
            }
        }
        Log.d(TAG, "[Simulated Mouse Move] dx: $dx, dy: $dy")
    }

    // Mouse button click
    fun sendMouseClick(buttonBit: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val hid = hidDevice
            val device = connectedDevice
            if (hid != null && device != null) {
                Executors.newSingleThreadExecutor().execute {
                    try {
                        // Press down
                        val reportDown = ByteArray(5)
                        reportDown[0] = buttonBit.toByte()
                        hid.sendReport(device, 2, reportDown)

                        Thread.sleep(30)

                        // Release
                        val reportUp = ByteArray(5)
                        hid.sendReport(device, 2, reportUp)
                    } catch (e: Exception) {
                        Log.e(TAG, "sendMouseClick failed", e)
                    }
                }
                return
            }
        }
        Log.d(TAG, "[Simulated Mouse Click] Button: $buttonBit")
    }
}

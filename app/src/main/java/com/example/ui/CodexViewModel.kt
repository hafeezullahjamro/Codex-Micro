package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bluetooth.BluetoothHidKeyboardController
import com.example.data.AppDatabase
import com.example.data.ShortcutMapping
import com.example.data.ShortcutRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CodexViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ShortcutRepository
    val bluetoothController: BluetoothHidKeyboardController

    init {
        val db = AppDatabase.getDatabase(application)
        repository = ShortcutRepository(db.shortcutDao())
        bluetoothController = BluetoothHidKeyboardController(application)
        bluetoothController.connectProfile()
    }

    // Default mappings for the Micro Codex macropad matching the user table.
    private val defaultMappings = mapOf(
        "key_1_2" to ShortcutMapping("key_1_2", "Alt + Space", false, false, true, false, 44, "Open ChatGPT Companion Window"),
        "key_1_3" to ShortcutMapping("key_1_3", "Ctrl + Shift + P", true, true, false, false, 19, "Open the Command Menu"),
        
        "key_2_1" to ShortcutMapping("key_2_1", "Ctrl + Shift + /", true, true, false, false, 56, "Display All Shortcuts"),
        "key_2_2" to ShortcutMapping("key_2_2", "Ctrl + ,", true, false, false, false, 54, "Open Settings"),
        "key_2_3" to ShortcutMapping("key_2_3", "Ctrl + O", true, false, false, false, 18, "Open Project Folder"),
        "key_2_4" to ShortcutMapping("key_2_4", "Ctrl + N", true, false, false, false, 17, "Start New Task/Chat"),
        
        "key_3_1" to ShortcutMapping("key_3_1", "Ctrl + Alt + N", true, false, true, false, 17, "Open Quick Chat"),
        "key_3_2" to ShortcutMapping("key_3_2", "Ctrl + G", true, false, false, false, 10, "Search Previous Tasks"),
        "key_3_3" to ShortcutMapping("key_3_3", "Ctrl + F", true, false, false, false, 9, "Find Text in Task"),
        "key_3_4" to ShortcutMapping("key_3_4", "Ctrl + Shift + G", true, true, false, false, 10, "Open Code-Review Tab"),
        
        "key_4_1" to ShortcutMapping("key_4_1", "Ctrl + B", true, false, false, false, 5, "Toggle Sidebar / Bottom Panel"),
        "key_4_2" to ShortcutMapping("key_4_2", "Ctrl + Shift + D", true, true, false, false, 7, "Start Voice Dictation"),
        "key_4_4" to ShortcutMapping("key_4_4", "Ctrl + Shift + O", true, true, false, false, 18, "Alt New Task Shortcut"),
        
        // Knob Rotations
        "knob_cw" to ShortcutMapping("knob_cw", "Ctrl + +", true, true, false, false, 46, "Zoom In / Increase Font"),
        "knob_ccw" to ShortcutMapping("knob_ccw", "Ctrl + -", true, false, false, false, 45, "Zoom Out / Decrease Font"),
        
        // Joystick directions
        "joy_left" to ShortcutMapping("joy_left", "Ctrl + [", true, false, false, false, 47, "Navigate Backward"),
        "joy_right" to ShortcutMapping("joy_right", "Ctrl + ]", true, false, false, false, 48, "Navigate Forward"),
        "joy_up" to ShortcutMapping("joy_up", "Ctrl + Shift + [", true, true, false, false, 47, "Open Previous Task"),
        "joy_down" to ShortcutMapping("joy_down", "Ctrl + Shift + ]", true, true, false, false, 48, "Open Next Task")
    )

    // Observable stream of custom mappings from the database
    val customMappings: StateFlow<Map<String, ShortcutMapping>> = repository.allMappings
        .map { list -> list.associateBy { it.keyId } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    // Combined mappings showing custom if saved, otherwise default
    val mappings: StateFlow<Map<String, ShortcutMapping>> = customMappings.map { customMap ->
        defaultMappings.mapValues { (key, defaultVal) ->
            customMap[key] ?: defaultVal
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = defaultMappings
    )

    // UI state for tracking transmission actions and logs
    private val _transmissionLogs = MutableStateFlow<List<String>>(emptyList())
    val transmissionLogs: StateFlow<List<String>> = _transmissionLogs

    private val _isFloatingMenuEnabled = MutableStateFlow(false)
    val isFloatingMenuEnabled: StateFlow<Boolean> = _isFloatingMenuEnabled

    private val _selectedKeyForEdit = MutableStateFlow<String?>(null)
    val selectedKeyForEdit: StateFlow<String?> = _selectedKeyForEdit

    // LED status indicators mapping (corresponding to the three small status LEDs on the bottom left)
    private val _ledStates = MutableStateFlow(booleanArrayOf(true, false, true))
    val ledStates: StateFlow<BooleanArray> = _ledStates

    fun toggleLed(index: Int) {
        val current = _ledStates.value.clone()
        if (index in current.indices) {
            current[index] = !current[index]
            _ledStates.value = current
        }
    }

    fun selectKeyForEdit(keyId: String?) {
        _selectedKeyForEdit.value = keyId
    }

    fun updateKeyMapping(
        keyId: String,
        name: String,
        isCtrl: Boolean,
        isShift: Boolean,
        isAlt: Boolean,
        isGui: Boolean,
        keyCode: Int,
        description: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val mapping = ShortcutMapping(keyId, name, isCtrl, isShift, isAlt, isGui, keyCode, description)
            repository.insert(mapping)
            addLog("Remapped '$keyId' to '$name'")
        }
    }

    fun resetKeyToDefault(keyId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteById(keyId)
            addLog("Reset '$keyId' to hardware default")
        }
    }

    fun resetAllToDefaults() {
        viewModelScope.launch(Dispatchers.IO) {
            defaultMappings.keys.forEach { keyId ->
                repository.deleteById(keyId)
            }
            addLog("Restored all keys to hardware defaults")
        }
    }

    fun toggleFloatingMenu(enabled: Boolean) {
        _isFloatingMenuEnabled.value = enabled
        addLog("Floating Accessibility Menu: ${if (enabled) "Enabled" else "Disabled"}")
    }

    fun transmitKeyPress(keyId: String) {
        val mapping = mappings.value[keyId] ?: defaultMappings[keyId] ?: return
        
        // Translate mapping to modifier byte and keyCode
        val modifiers = mapping.getModifierByte()
        val keyCode = mapping.keyCode.toByte()

        // Send over Bluetooth controller
        bluetoothController.sendKeyPress(modifiers, keyCode)

        // Log the event
        addLog("Transmitted shortcut: ${mapping.name} (${mapping.description})")

        // Blit status LEDs dynamically when keys are pressed!
        viewModelScope.launch {
            val original = _ledStates.value.clone()
            _ledStates.value = booleanArrayOf(true, true, true) // Blink all LEDs
            kotlinx.coroutines.delay(120)
            _ledStates.value = original
        }
    }

    fun addLog(message: String) {
        val current = _transmissionLogs.value.toMutableList()
        current.add(0, "[${nowTimestamp()}] $message")
        if (current.size > 20) {
            current.removeAt(current.size - 1)
        }
        _transmissionLogs.value = current
    }

    private fun nowTimestamp(): String {
        val now = java.time.LocalTime.now()
        return String.format("%02d:%02d:%02d", now.hour, now.minute, now.second)
    }

    override fun onCleared() {
        super.onCleared()
        bluetoothController.disconnectProfile()
    }
}

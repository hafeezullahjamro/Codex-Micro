package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shortcut_mappings")
data class ShortcutMapping(
    @PrimaryKey val keyId: String,
    val name: String,
    val isCtrl: Boolean,
    val isShift: Boolean,
    val isAlt: Boolean,
    val isGui: Boolean,
    val keyCode: Int,
    val description: String = ""
) {
    // Utility to represent the modifiers as a mask byte for USB HID Keyboard Report
    fun getModifierByte(): Byte {
        var mask = 0
        if (isCtrl) mask = mask or 0x01
        if (isShift) mask = mask or 0x02
        if (isAlt) mask = mask or 0x04
        if (isGui) mask = mask or 0x08
        return mask.toByte()
    }
}

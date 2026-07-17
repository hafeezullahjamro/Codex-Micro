package com.example.data

import kotlinx.coroutines.flow.Flow

class ShortcutRepository(private val shortcutDao: ShortcutDao) {
    val allMappings: Flow<List<ShortcutMapping>> = shortcutDao.getAllMappings()

    suspend fun getMappingById(keyId: String): ShortcutMapping? {
        return shortcutDao.getMappingById(keyId)
    }

    suspend fun insert(mapping: ShortcutMapping) {
        shortcutDao.insertMapping(mapping)
    }

    suspend fun deleteById(keyId: String) {
        shortcutDao.deleteMappingById(keyId)
    }
}

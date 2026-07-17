package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ShortcutDao {
    @Query("SELECT * FROM shortcut_mappings")
    fun getAllMappings(): Flow<List<ShortcutMapping>>

    @Query("SELECT * FROM shortcut_mappings WHERE keyId = :keyId")
    suspend fun getMappingById(keyId: String): ShortcutMapping?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMapping(mapping: ShortcutMapping)

    @Query("DELETE FROM shortcut_mappings WHERE keyId = :keyId")
    suspend fun deleteMappingById(keyId: String)
}

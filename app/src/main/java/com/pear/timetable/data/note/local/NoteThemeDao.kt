package com.pear.timetable.data.note.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteThemeDao {
    @Query("SELECT * FROM note_themes ORDER BY isBuiltIn DESC, updatedAt DESC")
    fun observeThemes(): Flow<List<NoteThemeEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBuiltInThemes(themes: List<NoteThemeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(theme: NoteThemeEntity)
}

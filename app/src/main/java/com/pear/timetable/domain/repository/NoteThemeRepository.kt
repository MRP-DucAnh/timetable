package com.pear.timetable.domain.repository

import com.pear.timetable.domain.model.NoteTheme
import kotlinx.coroutines.flow.Flow

interface NoteThemeRepository {
    fun observeThemes(): Flow<List<NoteTheme>>
    suspend fun ensureBuiltInThemes()
    suspend fun saveCustomTheme(name: String, appearance: com.pear.timetable.domain.model.NoteAppearance)
}

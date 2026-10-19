package com.pear.timetable.data.note.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_themes")
data class NoteThemeEntity(
    @PrimaryKey val id: String,
    val name: String,
    val appearanceJson: String,
    val isBuiltIn: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)

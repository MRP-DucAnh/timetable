package com.pear.timetable.domain.repository

import com.pear.timetable.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun observeNotes(): Flow<List<Note>>
    fun observeNote(noteId: Long): Flow<Note?>
    suspend fun saveNote(note: Note): Long
}

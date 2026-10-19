package com.pear.timetable.data.note.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Transaction
    @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
    fun observeNotes(): Flow<List<NoteWithBlocks>>

    @Transaction
    @Query("SELECT * FROM notes WHERE id = :noteId")
    fun observeNote(noteId: Long): Flow<NoteWithBlocks?>

    @Insert
    suspend fun insertNote(note: NoteEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlocks(blocks: List<NoteBlockEntity>)

    @Query("DELETE FROM note_blocks WHERE noteId = :noteId")
    suspend fun deleteBlocksForNote(noteId: Long)

    @Query("UPDATE notes SET title = :title, updatedAt = :updatedAt WHERE id = :noteId")
    suspend fun updateNote(noteId: Long, title: String, updatedAt: Long)

    @Transaction
    suspend fun save(note: NoteEntity, blocks: List<NoteBlockEntity>): Long {
        val noteId = if (note.id == 0L) insertNote(note) else {
            updateNote(note.id, note.title, note.updatedAt)
            note.id
        }
        deleteBlocksForNote(noteId)
        insertBlocks(blocks.map { it.copy(noteId = noteId) })
        return noteId
    }
}

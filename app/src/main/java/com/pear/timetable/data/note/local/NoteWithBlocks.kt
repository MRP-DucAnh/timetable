package com.pear.timetable.data.note.local

import androidx.room.Embedded
import androidx.room.Relation

data class NoteWithBlocks(
    @Embedded val note: NoteEntity,
    @Relation(parentColumn = "id", entityColumn = "noteId")
    val blocks: List<NoteBlockEntity>
)

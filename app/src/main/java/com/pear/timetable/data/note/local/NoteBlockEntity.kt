package com.pear.timetable.data.note.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "note_blocks",
    foreignKeys = [
        ForeignKey(
            entity = NoteEntity::class,
            parentColumns = ["id"],
            childColumns = ["noteId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("noteId")]
)
data class NoteBlockEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val noteId: Long,
    val blockType: String,
    val content: String,
    val fontFamily: String? = null,
    val textAlignment: String? = null,
    val isHighlighted: Boolean = false,
    val sortOrder: Int,
    val metadataJson: String? = null
)

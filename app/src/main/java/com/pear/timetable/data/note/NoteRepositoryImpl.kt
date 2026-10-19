package com.pear.timetable.data.note

import com.pear.timetable.data.note.local.NoteBlockEntity
import com.pear.timetable.data.note.local.NoteDao
import com.pear.timetable.data.note.local.NoteEntity
import com.pear.timetable.data.note.local.NoteWithBlocks
import com.pear.timetable.domain.model.Note
import com.pear.timetable.domain.model.NoteBlock
import com.pear.timetable.domain.model.NoteBlockType
import com.pear.timetable.domain.model.NoteFont
import com.pear.timetable.domain.model.TextAlignment
import com.pear.timetable.domain.model.TextStyle
import com.pear.timetable.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao
) : NoteRepository {
    override fun observeNotes(): Flow<List<Note>> = noteDao.observeNotes().map { notes ->
        notes.map(NoteWithBlocks::toDomain)
    }

    override fun observeNote(noteId: Long): Flow<Note?> = noteDao.observeNote(noteId).map { it?.toDomain() }

    override suspend fun saveNote(note: Note): Long = noteDao.save(
        note = NoteEntity(note.id, note.title, note.createdAt, note.updatedAt, NoteAppearanceCodec.encode(note.appearance)),
        blocks = note.blocks.map { block ->
            NoteBlockEntity(
                id = block.id,
                noteId = note.id,
                blockType = block.type.name,
                content = block.content,
                fontFamily = block.style.fontFamily.name,
                textAlignment = block.style.alignment.name,
                isHighlighted = block.style.isHighlighted,
                sortOrder = block.order,
                metadataJson = block.metadataJson
            )
        }
    )
}

private fun NoteWithBlocks.toDomain() = Note(
    id = note.id,
    title = note.title,
    appearance = NoteAppearanceCodec.decode(note.appearanceJson),
    blocks = blocks.sortedBy { it.sortOrder }.map { block ->
        NoteBlock(
            id = block.id,
            type = runCatching { NoteBlockType.valueOf(block.blockType) }.getOrDefault(NoteBlockType.TEXT),
            content = block.content,
            style = TextStyle(
                fontFamily = block.fontFamily?.let { runCatching { NoteFont.valueOf(it) }.getOrNull() }
                    ?: NoteFont.DEFAULT,
                alignment = block.textAlignment?.let { runCatching { TextAlignment.valueOf(it) }.getOrNull() }
                    ?: TextAlignment.START,
                isHighlighted = block.isHighlighted
            ),
            order = block.sortOrder,
            metadataJson = block.metadataJson
        )
    },
    createdAt = note.createdAt,
    updatedAt = note.updatedAt
)

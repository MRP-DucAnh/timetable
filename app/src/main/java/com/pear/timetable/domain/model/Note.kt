package com.pear.timetable.domain.model

data class Note(
    val id: Long = 0,
    val title: String,
    val blocks: List<NoteBlock>,
    val appearance: NoteAppearance = NoteAppearance(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class NoteAppearance(
    val backgroundColor: Long = 0xFFFFFBFE,
    val showLines: Boolean = false,
    val lineColor: Long = 0xFFCAD7E5,
    val titleColor: Long = 0xFF1C1B1F,
    val bodyTextColor: Long = 0xFF49454F,
    // An asset key, not a device file path. Future template packs can resolve this key safely.
    val backgroundTemplateKey: String? = null
)

data class NoteTheme(
    val id: String,
    val name: String,
    val appearance: NoteAppearance,
    val isBuiltIn: Boolean
)

data class NoteBlock(
    val id: Long = 0,
    val type: NoteBlockType,
    val content: String,
    val style: TextStyle = TextStyle(),
    val order: Int,
    // Reserved for future block-specific data such as image URL, link target or checklist items.
    val metadataJson: String? = null
)

enum class NoteBlockType { TEXT, IMAGE, LINK, CHECKLIST }

data class TextStyle(
    val fontFamily: NoteFont = NoteFont.DEFAULT,
    val alignment: TextAlignment = TextAlignment.START,
    val isHighlighted: Boolean = false
)

enum class NoteFont { DEFAULT, SERIF, MONOSPACE }
enum class TextAlignment { START, CENTER, END }

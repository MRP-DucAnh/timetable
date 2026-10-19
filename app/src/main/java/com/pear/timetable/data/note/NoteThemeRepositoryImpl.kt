package com.pear.timetable.data.note

import com.pear.timetable.data.note.local.NoteThemeDao
import com.pear.timetable.data.note.local.NoteThemeEntity
import com.pear.timetable.domain.model.NoteAppearance
import com.pear.timetable.domain.model.NoteTheme
import com.pear.timetable.domain.repository.NoteThemeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class NoteThemeRepositoryImpl @Inject constructor(private val dao: NoteThemeDao) : NoteThemeRepository {
    override fun observeThemes(): Flow<List<NoteTheme>> = dao.observeThemes().map { themes ->
        themes.map { NoteTheme(it.id, it.name, NoteAppearanceCodec.decode(it.appearanceJson), it.isBuiltIn) }
    }

    override suspend fun ensureBuiltInThemes() = dao.insertBuiltInThemes(BUILT_IN_THEMES)

    override suspend fun saveCustomTheme(name: String, appearance: NoteAppearance) {
        val now = System.currentTimeMillis()
        dao.upsert(NoteThemeEntity(UUID.randomUUID().toString(), name, NoteAppearanceCodec.encode(appearance), false, now, now))
    }

    private companion object {
        val BUILT_IN_THEMES = listOf(
            theme("default", "Mặc định", NoteAppearance()),
            theme("letter_warm", "Thư ấm áp", NoteAppearance(backgroundColor = 0xFFFFF7EC, showLines = true, lineColor = 0xFFE7CCB2, backgroundTemplateKey = "ruled")),
            theme("letter_blue", "Thư xanh dịu", NoteAppearance(backgroundColor = 0xFFF1F7FF, showLines = true, lineColor = 0xFFB7CEE7, backgroundTemplateKey = "ruled")),
            theme("letter_garden", "Thư vườn xanh", NoteAppearance(backgroundColor = 0xFFF0FAF2, showLines = true, lineColor = 0xFFB7D9BE, backgroundTemplateKey = "grid"))
        )
        fun theme(id: String, name: String, appearance: NoteAppearance) = NoteThemeEntity(
            id, name, NoteAppearanceCodec.encode(appearance), true, 0, 0
        )
    }
}

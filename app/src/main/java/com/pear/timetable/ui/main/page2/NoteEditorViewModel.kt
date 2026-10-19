package com.pear.timetable.ui.main.page2

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.input.TextFieldValue
import com.pear.timetable.domain.model.Note
import com.pear.timetable.domain.model.NoteBlock
import com.pear.timetable.domain.model.NoteBlockType
import com.pear.timetable.domain.model.NoteFont
import com.pear.timetable.domain.model.NoteAppearance
import com.pear.timetable.domain.model.TextAlignment
import com.pear.timetable.domain.model.TextStyle
import com.pear.timetable.domain.repository.NoteRepository
import com.pear.timetable.domain.repository.NoteThemeRepository
import com.pear.timetable.domain.model.NoteTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NoteEditorUiState(
    val noteId: Long = 0,
    val title: String = "",
    val body: TextFieldValue = TextFieldValue(""),
    val font: NoteFont = NoteFont.DEFAULT,
    val alignment: TextAlignment = TextAlignment.START,
    val appearance: NoteAppearance = NoteAppearance(),
    val bodyBlockId: Long = 0,
    val bodyBlockOrder: Int = 0,
    val preservedBlocks: List<NoteBlock> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false
)

@HiltViewModel
class NoteEditorViewModel @Inject constructor(
    private val repository: NoteRepository,
    private val themeRepository: NoteThemeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val requestedNoteId: Long = savedStateHandle["noteId"] ?: 0L
    private val _uiState = MutableStateFlow(NoteEditorUiState(noteId = requestedNoteId, isLoading = requestedNoteId != 0L))
    val uiState: StateFlow<NoteEditorUiState> = _uiState
    val themes: StateFlow<List<NoteTheme>> = themeRepository.observeThemes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch { themeRepository.ensureBuiltInThemes() }
        if (requestedNoteId != 0L) viewModelScope.launch {
            repository.observeNote(requestedNoteId).filterNotNull().first().also(::load)
        }
    }

    fun updateTitle(value: String) = _uiState.update { it.copy(title = value) }
    fun updateBody(value: TextFieldValue) = _uiState.update { it.copy(body = value) }
    fun updateFont(value: NoteFont) = _uiState.update { it.copy(font = value) }
    fun updateAlignment(value: TextAlignment) = _uiState.update { it.copy(alignment = value) }
    fun applyTextColor(color: Color) = applyStyle(SpanStyle(color = color))
    fun applyBackgroundColor(color: Color) = applyStyle(SpanStyle(background = color))

    fun highlightSelection() = applyStyle(SpanStyle(background = Color(0xFFFFE082)))
    fun updatePaperColor(color: Color) = _uiState.update {
        it.copy(appearance = it.appearance.copy(backgroundColor = color.toArgb().toLong() and 0xFFFF_FFFFL))
    }
    fun updateLineColor(color: Color) = _uiState.update {
        it.copy(appearance = it.appearance.copy(lineColor = color.toArgb().toLong() and 0xFFFF_FFFFL))
    }
    fun updateTitleColor(color: Color) = _uiState.update {
        it.copy(appearance = it.appearance.copy(titleColor = color.toArgb().toLong() and 0xFFFF_FFFFL))
    }
    fun updateBodyTextColor(color: Color) = _uiState.update {
        it.copy(appearance = it.appearance.copy(bodyTextColor = color.toArgb().toLong() and 0xFFFF_FFFFL))
    }
    fun toggleLines() = _uiState.update { it.copy(appearance = it.appearance.copy(showLines = !it.appearance.showLines)) }
    fun updateTemplate(templateKey: String?) = _uiState.update { it.copy(appearance = it.appearance.copy(backgroundTemplateKey = templateKey)) }
    fun updateAppearance(appearance: NoteAppearance) = _uiState.update { it.copy(appearance = appearance) }
    fun saveCurrentAsTheme(name: String) = viewModelScope.launch {
        if (name.isNotBlank()) themeRepository.saveCustomTheme(name.trim(), _uiState.value.appearance)
    }
    fun saveTheme(name: String, appearance: NoteAppearance) = viewModelScope.launch {
        if (name.isNotBlank()) themeRepository.saveCustomTheme(name.trim(), appearance)
    }

    fun save(onSaved: () -> Unit) = viewModelScope.launch {
        val state = _uiState.value
        if (state.title.isBlank() && state.body.text.isBlank()) return@launch
        _uiState.update { it.copy(isSaving = true) }
        val now = System.currentTimeMillis()
        repository.saveNote(
            Note(
                id = state.noteId,
                title = state.title.ifBlank { state.body.text.lineSequence().firstOrNull().orEmpty() },
                createdAt = if (state.noteId == 0L) now else originalCreatedAt,
                updatedAt = now,
                // Keep blocks that this editor does not yet render (image/link/checklist).
                // Adding editor features later will therefore not destroy existing note content.
                blocks = state.preservedBlocks + NoteBlock(
                        id = state.bodyBlockId,
                        type = NoteBlockType.TEXT,
                        content = state.body.text,
                        style = TextStyle(state.font, state.alignment),
                        order = state.bodyBlockOrder,
                        metadataJson = RichTextCodec.encode(state.body.annotatedString)
                    ),
                appearance = state.appearance
            )
        )
        _uiState.update { it.copy(isSaving = false) }
        onSaved()
    }

    private var originalCreatedAt = System.currentTimeMillis()

    private fun load(note: Note) {
        val block = note.blocks.firstOrNull { it.type == NoteBlockType.TEXT }
        originalCreatedAt = note.createdAt
        _uiState.value = NoteEditorUiState(
            noteId = note.id,
            title = note.title,
            body = TextFieldValue(RichTextCodec.decode(block?.content.orEmpty(), block?.metadataJson)),
            font = block?.style?.fontFamily ?: NoteFont.DEFAULT,
            alignment = block?.style?.alignment ?: TextAlignment.START,
            appearance = note.appearance,
            bodyBlockId = block?.id ?: 0,
            bodyBlockOrder = block?.order ?: 0,
            preservedBlocks = note.blocks.filter { it.type != NoteBlockType.TEXT }
        )
    }

    private fun applyStyle(style: SpanStyle) = _uiState.update { state ->
        val value = state.body
        val range = value.selection.let { selection ->
            if (!selection.collapsed) minOf(selection.start, selection.end)..maxOf(selection.start, selection.end)
            else value.text.currentLineRange(selection.start)
        }
        if (range.first == range.last) state else {
            val styledText = AnnotatedString.Builder(value.annotatedString).apply {
                addStyle(style, range.first, range.last)
            }.toAnnotatedString()
            state.copy(body = value.copy(annotatedString = styledText))
        }
    }

    private fun String.currentLineRange(cursor: Int): IntRange {
        val safeCursor = cursor.coerceIn(0, length)
        val start = lastIndexOf('\n', startIndex = (safeCursor - 1).coerceAtLeast(0)).let { if (it == -1) 0 else it + 1 }
        val end = indexOf('\n', startIndex = safeCursor).let { if (it == -1) length else it }
        return start..end
    }
}

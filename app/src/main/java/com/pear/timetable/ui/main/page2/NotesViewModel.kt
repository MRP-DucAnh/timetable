package com.pear.timetable.ui.main.page2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pear.timetable.domain.model.Note
import com.pear.timetable.domain.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class NotesUiState(val notes: List<Note> = emptyList())

@HiltViewModel
class NotesViewModel @Inject constructor(repository: NoteRepository) : ViewModel() {
    val uiState: StateFlow<NotesUiState> = repository.observeNotes()
        .map(::NotesUiState)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), NotesUiState())
}

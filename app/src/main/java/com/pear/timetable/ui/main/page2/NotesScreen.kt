package com.pear.timetable.ui.main.page2

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.FormatAlignLeft
import androidx.compose.material.icons.automirrored.filled.FormatAlignRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FormatAlignCenter
import androidx.compose.material.icons.filled.Highlight
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Button
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pear.timetable.R
import com.pear.timetable.domain.model.Note
import com.pear.timetable.domain.model.NoteFont
import com.pear.timetable.domain.model.TextAlignment
import com.pear.timetable.domain.model.NoteAppearance
import com.pear.timetable.domain.model.NoteTheme

private const val NOTES_LIST_ROUTE = "notes_list"
private const val NOTE_EDITOR_ROUTE = "note/{noteId}"

private data class ColorOption(val labelRes: Int, val color: Color)
private data class TemplateOption(val labelRes: Int, val key: String?)

private val textColorOptions = listOf(
    ColorOption(R.string.note_color_default, Color.Unspecified),
    ColorOption(R.string.note_color_ink, Color(0xFF1D4ED8)),
    ColorOption(R.string.note_color_forest, Color(0xFF006C4C)),
    ColorOption(R.string.note_color_rose, Color(0xFF9D174D))
)
private val backgroundColorOptions = listOf(
    ColorOption(R.string.note_color_default, Color.Unspecified),
    ColorOption(R.string.note_color_yellow, Color(0xFFFFE082)),
    ColorOption(R.string.note_color_mint, Color(0xFFC8F1DF)),
    ColorOption(R.string.note_color_lavender, Color(0xFFE2D9FF))
)
private val paperColorOptions = listOf(
    ColorOption(R.string.note_color_default, Color(0xFFFFFBFE)),
    ColorOption(R.string.note_color_yellow, Color(0xFFFFF8D8)),
    ColorOption(R.string.note_color_mint, Color(0xFFE5F8EF)),
    ColorOption(R.string.note_color_lavender, Color(0xFFF1EDFF))
)
private val lineColorOptions = listOf(
    ColorOption(R.string.note_color_default, Color(0xFFCAD7E5)),
    ColorOption(R.string.note_color_ink, Color(0xFF9CB8DF)),
    ColorOption(R.string.note_color_forest, Color(0xFFA8D5C1)),
    ColorOption(R.string.note_color_rose, Color(0xFFE6B7C8))
)
private val paperTemplateOptions = listOf(
    TemplateOption(R.string.note_template_plain, null),
    TemplateOption(R.string.note_template_ruled, "ruled"),
    TemplateOption(R.string.note_template_grid, "grid")
)

@Composable
fun NotesScreen() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = NOTES_LIST_ROUTE) {
        composable(NOTES_LIST_ROUTE) {
            NotesListRoute(
                onCreateNote = { navController.navigate("note/0") },
                onOpenNote = { navController.navigate("note/$it") }
            )
        }
        composable(NOTE_EDITOR_ROUTE, listOf(navArgument("noteId") { type = NavType.LongType })) {
            NoteEditorRoute(navController::popBackStack)
        }
    }
}

@Composable
private fun NotesListRoute(onCreateNote: () -> Unit, onOpenNote: (Long) -> Unit, viewModel: NotesViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    NotesListScreen(uiState.notes, onCreateNote, onOpenNote)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotesListScreen(notes: List<Note>, onCreateNote: () -> Unit, onOpenNote: (Long) -> Unit) {
    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text(stringResource(R.string.notes_title)) }) },
        floatingActionButton = {
            if (notes.isNotEmpty()) ExtendedFloatingActionButton(
                onClick = onCreateNote,
                icon = { Icon(Icons.Default.Add, null) },
                text = { Text(stringResource(R.string.notes_add)) }
            )
        }
    ) { padding ->
        if (notes.isEmpty()) EmptyNotesView(Modifier.padding(padding), onCreateNote)
        else LazyColumn(
            Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) { items(notes, key = Note::id) { NoteCard(it, onOpenNote) } }
    }
}

@Composable
private fun EmptyNotesView(modifier: Modifier, onCreateNote: () -> Unit) {
    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(Modifier.size(88.dp), RoundedCornerShape(28.dp), color = MaterialTheme.colorScheme.secondaryContainer) {
            Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.NoteAlt, null, Modifier.size(42.dp), MaterialTheme.colorScheme.onSecondaryContainer) }
        }
        Spacer(Modifier.height(24.dp))
        Text(stringResource(R.string.notes_empty_title), style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        Text(stringResource(R.string.notes_empty_description), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        Spacer(Modifier.height(24.dp))
        ExtendedFloatingActionButton(
            onClick = onCreateNote,
            icon = { Icon(Icons.Default.Add, null) },
            text = { Text(stringResource(R.string.notes_add_first)) }
        )
    }
}

@Composable
private fun NoteCard(note: Note, onOpenNote: (Long) -> Unit) {
    val textBlock = note.blocks.firstOrNull { it.content.isNotBlank() }
    Card(
        Modifier.fillMaxWidth().clickable { onOpenNote(note.id) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(Modifier.padding(18.dp)) {
            Text(note.title, style = MaterialTheme.typography.titleMedium)
            if (!textBlock?.content.isNullOrBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(textBlock!!.content, maxLines = 3, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(14.dp))
            Text(stringResource(R.string.notes_recently_edited), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.tertiary)
        }
    }
}

@Composable
private fun NoteEditorRoute(onBack: () -> Unit, viewModel: NoteEditorViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val themes by viewModel.themes.collectAsState()
    Dialog(
        onDismissRequest = onBack,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            NoteEditorScreen(
                state = state,
                themes = themes,
                onBack = onBack,
                onTitleChange = viewModel::updateTitle,
                onBodyChange = { value -> viewModel.updateBody(value) },
                onFontChange = viewModel::updateFont,
                onAlignmentChange = viewModel::updateAlignment,
                onHighlight = viewModel::highlightSelection,
                onTextColor = viewModel::applyTextColor,
                onBackgroundColor = viewModel::applyBackgroundColor,
                onPaperColor = viewModel::updatePaperColor,
                onLineColor = viewModel::updateLineColor,
                onTitleColor = viewModel::updateTitleColor,
                onBodyTextColor = viewModel::updateBodyTextColor,
                onToggleLines = viewModel::toggleLines,
                onTemplateChange = viewModel::updateTemplate,
                onThemeChange = viewModel::updateAppearance,
                onSaveCurrentTheme = viewModel::saveCurrentAsTheme,
                onSaveTheme = viewModel::saveTheme,
                onSave = { viewModel.save(onBack) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun NoteEditorScreen(
    state: NoteEditorUiState, themes: List<NoteTheme>, onBack: () -> Unit, onTitleChange: (String) -> Unit, onBodyChange: (TextFieldValue) -> Unit,
    onFontChange: (NoteFont) -> Unit, onAlignmentChange: (TextAlignment) -> Unit,
    onHighlight: () -> Unit, onTextColor: (Color) -> Unit, onBackgroundColor: (Color) -> Unit,
    onPaperColor: (Color) -> Unit, onLineColor: (Color) -> Unit, onToggleLines: () -> Unit,
    onTitleColor: (Color) -> Unit, onBodyTextColor: (Color) -> Unit,
    onTemplateChange: (String?) -> Unit, onThemeChange: (NoteAppearance) -> Unit, onSaveCurrentTheme: (String) -> Unit,
    onSaveTheme: (String, NoteAppearance) -> Unit, onSave: () -> Unit
) {
    // Keep the writing baseline comfortably above the ruled line. This is also the
    // line spacing used by the page canvas below, so a larger text style can never
    // be crossed by a rule.
    val noteLineHeight = 32.sp
    val textStyle = MaterialTheme.typography.bodyLarge.copy(
        fontFamily = state.font.toFontFamily(),
        textAlign = state.alignment.toTextAlign(),
        color = Color(state.appearance.bodyTextColor.toInt()),
        lineHeight = noteLineHeight
    )
    val paperColor = Color(state.appearance.backgroundColor.toInt())
    var showThemePicker by remember { mutableStateOf(false) }
    Scaffold(containerColor = MaterialTheme.colorScheme.surfaceContainerLow, topBar = {
        CenterAlignedTopAppBar(
            title = { Text(stringResource(if (state.noteId == 0L) R.string.note_editor_new_title else R.string.note_editor_edit_title)) },
            navigationIcon = { IconButton(onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.note_back)) } },
            actions = { IconButton(onSave, enabled = !state.isSaving) { Icon(Icons.Default.Check, stringResource(R.string.note_save)) } }
        )
    }, bottomBar = {
        BottomAppBar {
            if (!state.body.selection.collapsed) {
                IconButton(onClick = onHighlight) { Icon(Icons.Default.Highlight, stringResource(R.string.note_highlight)) }
                AlignmentButton(TextAlignment.START, state.alignment, onAlignmentChange, Icons.AutoMirrored.Filled.FormatAlignLeft)
                AlignmentButton(TextAlignment.CENTER, state.alignment, onAlignmentChange, Icons.Default.FormatAlignCenter)
                AlignmentButton(TextAlignment.END, state.alignment, onAlignmentChange, Icons.AutoMirrored.Filled.FormatAlignRight)
            }
            Spacer(Modifier.weight(1f))
            AssistChip(onClick = { showThemePicker = true }, label = { Text(stringResource(R.string.note_choose_theme)) })
        }
    }) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .background(paperColor, RoundedCornerShape(16.dp))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
                .paperCanvas(state.appearance),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { TextField(state.title, onTitleChange, Modifier.fillMaxWidth(), placeholder = { Text(stringResource(R.string.note_title_hint)) }, textStyle = MaterialTheme.typography.headlineSmall.copy(color = Color(state.appearance.titleColor.toInt())), colors = editorColors()) }
            item {
                TextField(
                    value = state.body,
                    onValueChange = onBodyChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .background(Color.Transparent, RoundedCornerShape(16.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
                    placeholder = { Text(stringResource(R.string.note_body_hint)) },
                    textStyle = textStyle,
                    colors = editorColors()
                )
            }
            item { AssistChip(onClick = {}, label = { Text(stringResource(R.string.note_add_content_hint)) }) }
        }
    }
    if (showThemePicker) {
        NoteThemePicker(
            themes = themes,
            selectedAppearance = state.appearance,
            onDismiss = { showThemePicker = false },
            onThemeSelected = { appearance -> onThemeChange(appearance); showThemePicker = false },
            onSaveCurrentTheme = onSaveCurrentTheme,
            onSaveTheme = onSaveTheme
        )
    }
}

@Composable
private fun editorColors() = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)

@Composable
private fun AlignmentButton(value: TextAlignment, selected: TextAlignment, onChange: (TextAlignment) -> Unit, icon: ImageVector) {
    IconButton({ onChange(value) }) { Icon(icon, stringResource(value.labelRes()), tint = if (value == selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColorDropdown(labelRes: Int, options: List<ColorOption>, onSelect: (Color) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(options.first()) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = stringResource(selected.labelRes),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(labelRes)) },
            leadingIcon = {
                Surface(Modifier.size(18.dp), RoundedCornerShape(9.dp), color = selected.color.takeOrSurface()) {}
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(stringResource(option.labelRes)) },
                    leadingIcon = { Surface(Modifier.size(18.dp), RoundedCornerShape(9.dp), color = option.color.takeOrSurface()) {} },
                    onClick = { selected = option; expanded = false; onSelect(option.color) }
                )
            }
        }
    }
}

@Composable
private fun Color.takeOrSurface(): Color = if (this == Color.Unspecified) MaterialTheme.colorScheme.surfaceVariant else this

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NoteThemePicker(
    themes: List<NoteTheme>,
    selectedAppearance: NoteAppearance,
    onDismiss: () -> Unit,
    onThemeSelected: (NoteAppearance) -> Unit,
    onSaveCurrentTheme: (String) -> Unit,
    onSaveTheme: (String, NoteAppearance) -> Unit
) {
    var customThemeName by remember { mutableStateOf("") }
    var showCreator by remember { mutableStateOf(false) }
    ModalBottomSheet(onDismissRequest = onDismiss) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
                    item { Text(stringResource(R.string.note_choose_theme), style = MaterialTheme.typography.titleLarge) }
                    item {
                        OutlinedTextField(
                            value = customThemeName,
                            onValueChange = { customThemeName = it },
                            label = { Text(stringResource(R.string.note_custom_theme_name)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = { onSaveCurrentTheme(customThemeName); customThemeName = "" },
                            enabled = customThemeName.isNotBlank()
                        ) { Text(stringResource(R.string.note_save_current_theme)) }
                        Spacer(Modifier.height(8.dp))
                        OutlinedButton(onClick = { showCreator = true }, modifier = Modifier.fillMaxWidth()) {
                            Text(stringResource(R.string.note_create_theme))
                        }
                    }
                    items(themes, key = NoteTheme::id) { theme ->
                        NoteThemeCard(theme, theme.appearance == selectedAppearance) { onThemeSelected(theme.appearance) }
                    }
    }
    }
    if (showCreator) NoteThemeCreator(
        initialAppearance = selectedAppearance,
        onDismiss = { showCreator = false },
        onSave = { name, appearance -> onSaveTheme(name, appearance); showCreator = false }
    )
}

@Composable
private fun NoteThemeCard(theme: NoteTheme, selected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color(theme.appearance.backgroundColor.toInt())),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Surface(
                modifier = Modifier.fillMaxWidth().height(104.dp).paperCanvas(theme.appearance),
                color = Color(theme.appearance.backgroundColor.toInt()),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(R.string.note_theme_preview_greeting), style = MaterialTheme.typography.titleSmall, color = Color(theme.appearance.titleColor.toInt()))
                    Text(stringResource(R.string.note_theme_preview_body), style = MaterialTheme.typography.bodyMedium, color = Color(theme.appearance.bodyTextColor.toInt()))
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(theme.name, style = MaterialTheme.typography.titleMedium)
            if (theme.isBuiltIn) Text(stringResource(R.string.note_theme_preview_body), color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
            if (selected) {
                Spacer(Modifier.height(8.dp))
                Text(stringResource(R.string.note_theme_selected), color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NoteThemeCreator(
    initialAppearance: NoteAppearance,
    onDismiss: () -> Unit,
    onSave: (String, NoteAppearance) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var appearance by remember { mutableStateOf(initialAppearance) }
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)) {
        Surface(Modifier.fillMaxSize(), color = Color(appearance.backgroundColor.toInt())) {
            Scaffold(
                containerColor = Color(appearance.backgroundColor.toInt()),
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text(stringResource(R.string.note_create_theme_title)) },
                        navigationIcon = { IconButton(onDismiss) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.note_back)) } },
                        actions = { IconButton({ onSave(name, appearance) }, enabled = name.isNotBlank()) { Icon(Icons.Default.Check, stringResource(R.string.note_save)) } }
                    )
                }
            ) { padding ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding).paperCanvas(appearance),
                    contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item { OutlinedTextField(name, { name = it }, Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.note_custom_theme_name)) }) }
                    item {
                        Surface(Modifier.fillMaxWidth().height(150.dp).paperCanvas(appearance), color = Color(appearance.backgroundColor.toInt()), shape = RoundedCornerShape(16.dp)) {
                            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(stringResource(R.string.note_theme_preview_greeting), style = MaterialTheme.typography.titleLarge, color = Color(appearance.titleColor.toInt()))
                                Text(stringResource(R.string.note_theme_preview_body), style = MaterialTheme.typography.bodyLarge, color = Color(appearance.bodyTextColor.toInt()))
                            }
                        }
                    }
                    item { ColorDropdown(R.string.note_paper_color, paperColorOptions) { appearance = appearance.copy(backgroundColor = it.toArgb().toLong() and 0xFFFF_FFFFL) } }
                    item { ColorDropdown(R.string.note_title_color, textColorOptions) { appearance = appearance.copy(titleColor = it.toArgb().toLong() and 0xFFFF_FFFFL) } }
                    item { ColorDropdown(R.string.note_body_text_color, textColorOptions) { appearance = appearance.copy(bodyTextColor = it.toArgb().toLong() and 0xFFFF_FFFFL) } }
                    item {
                        androidx.compose.foundation.layout.Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(stringResource(R.string.note_show_lines))
                            Switch(appearance.showLines, { appearance = appearance.copy(showLines = it) })
                        }
                    }
                    if (appearance.showLines) item { ColorDropdown(R.string.note_line_color, lineColorOptions) { appearance = appearance.copy(lineColor = it.toArgb().toLong() and 0xFFFF_FFFFL) } }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TemplateDropdown(selectedKey: String?, onSelect: (String?) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val selected = paperTemplateOptions.first { it.key == selectedKey }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = stringResource(selected.labelRes), onValueChange = {}, readOnly = true,
            label = { Text(stringResource(R.string.note_paper_template)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true).fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            paperTemplateOptions.forEach { option ->
                DropdownMenuItem(text = { Text(stringResource(option.labelRes)) }, onClick = { expanded = false; onSelect(option.key) })
            }
        }
    }
}

private fun Modifier.paperCanvas(appearance: NoteAppearance): Modifier = drawBehind {
    if (!appearance.showLines) return@drawBehind
    val lineColor = Color(appearance.lineColor.toInt())
    val spacing = 32.dp.toPx()
    var y = spacing
    while (y < size.height) {
        drawLine(lineColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1.dp.toPx())
        y += spacing
    }
    if (appearance.backgroundTemplateKey == "grid") {
        var x = spacing
        while (x < size.width) {
            drawLine(lineColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1.dp.toPx())
            x += spacing
        }
    }
}

private fun NoteFont.labelRes() = when (this) { NoteFont.DEFAULT -> R.string.note_font_default; NoteFont.SERIF -> R.string.note_font_serif; NoteFont.MONOSPACE -> R.string.note_font_monospace }
private fun TextAlignment.labelRes() = when (this) { TextAlignment.START -> R.string.note_align_start; TextAlignment.CENTER -> R.string.note_align_center; TextAlignment.END -> R.string.note_align_end }
private fun NoteFont.toFontFamily() = when (this) { NoteFont.DEFAULT -> FontFamily.Default; NoteFont.SERIF -> FontFamily.Serif; NoteFont.MONOSPACE -> FontFamily.Monospace }
private fun TextAlignment.toTextAlign() = when (this) { TextAlignment.START -> TextAlign.Start; TextAlignment.CENTER -> TextAlign.Center; TextAlignment.END -> TextAlign.End }

package com.pear.timetable.data.note

import com.pear.timetable.domain.model.NoteAppearance
import org.json.JSONObject

internal object NoteAppearanceCodec {
    fun encode(value: NoteAppearance): String = JSONObject().apply {
        put("backgroundColor", value.backgroundColor.toULong().toString())
        put("showLines", value.showLines)
        put("lineColor", value.lineColor.toULong().toString())
        put("titleColor", value.titleColor.toULong().toString())
        put("bodyTextColor", value.bodyTextColor.toULong().toString())
        value.backgroundTemplateKey?.let { put("template", it) }
    }.toString()

    fun decode(raw: String): NoteAppearance = runCatching {
        if (raw.isBlank()) return NoteAppearance()
        JSONObject(raw).let { json ->
            NoteAppearance(
                backgroundColor = json.optString("backgroundColor").toULongOrNull()?.toLong() ?: NoteAppearance().backgroundColor,
                showLines = json.optBoolean("showLines", false),
                lineColor = json.optString("lineColor").toULongOrNull()?.toLong() ?: NoteAppearance().lineColor,
                titleColor = json.optString("titleColor").toULongOrNull()?.toLong() ?: NoteAppearance().titleColor,
                bodyTextColor = json.optString("bodyTextColor").toULongOrNull()?.toLong() ?: NoteAppearance().bodyTextColor,
                backgroundTemplateKey = json.optString("template").takeIf { it.isNotBlank() }
            )
        }
    }.getOrDefault(NoteAppearance())
}

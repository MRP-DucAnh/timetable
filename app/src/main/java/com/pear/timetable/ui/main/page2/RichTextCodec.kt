package com.pear.timetable.ui.main.page2

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import org.json.JSONArray
import org.json.JSONObject

/**
 * Persists only the inline style ranges of a text block. The text itself remains in
 * [NoteBlock.content], while this JSON can grow with new rich-text attributes later.
 */
object RichTextCodec {
    fun encode(text: AnnotatedString): String = JSONArray().apply {
        text.spanStyles.forEach { range ->
            val style = range.item
            if (style.color != Color.Unspecified || style.background != Color.Unspecified) {
                put(JSONObject().apply {
                    put("start", range.start)
                    put("end", range.end)
                    if (style.color != Color.Unspecified) put("textColor", style.color.toArgb().toLong() and 0xFFFF_FFFFL)
                    if (style.background != Color.Unspecified) put("backgroundColor", style.background.toArgb().toLong() and 0xFFFF_FFFFL)
                })
            }
        }
    }.toString()

    fun decode(content: String, rawJson: String?): AnnotatedString {
        if (rawJson.isNullOrBlank()) return AnnotatedString(content)
        return runCatching {
            val ranges = JSONArray(rawJson)
            AnnotatedString.Builder(content).apply {
                for (index in 0 until ranges.length()) {
                    val item = ranges.getJSONObject(index)
                    val start = item.getInt("start").coerceIn(0, content.length)
                    val end = item.getInt("end").coerceIn(start, content.length)
                    if (start == end) continue
                    addStyle(
                        SpanStyle(
                            color = item.colorOrUnspecified("textColor"),
                            background = item.colorOrUnspecified("backgroundColor")
                        ),
                        start,
                        end
                    )
                }
            }.toAnnotatedString()
        }.getOrElse { AnnotatedString(content) }
    }

    private fun JSONObject.colorOrUnspecified(key: String): Color =
        if (has(key)) Color(getString(key).toLong().toInt()) else Color.Unspecified
}

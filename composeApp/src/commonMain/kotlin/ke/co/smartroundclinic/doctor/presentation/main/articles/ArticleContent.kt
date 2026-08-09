package ke.co.smartroundclinic.doctor.presentation.main.articles

/**
 * The API stores article bodies as HTML and the reader renders them as HTML, but the amended Figma
 * spec drops the rich-text toolbar in favour of a plain text area. These two helpers bridge the
 * gap: what a doctor types is wrapped back into paragraphs on save, and an existing article is
 * flattened to text when it is opened for editing.
 */

/** Wraps typed plain text into the `<p>`/`<br>` HTML the article API and reader expect. */
internal fun plainTextToHtml(text: String): String {
    val normalised = text.replace("\r\n", "\n").trim()
    if (normalised.isEmpty()) return ""

    return normalised
        .split(Regex("\n{2,}"))
        .filter { it.isNotBlank() }
        .joinToString("") { paragraph ->
            val escaped = paragraph
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\n", "<br>")
            "<p>$escaped</p>"
        }
}

/** Flattens stored article HTML back to editable text, keeping paragraph breaks intact. */
internal fun htmlToPlainText(html: String): String {
    if (html.isBlank()) return ""

    return html
        .replace(Regex("<br\\s*/?>", RegexOption.IGNORE_CASE), "\n")
        .replace(Regex("</(p|div|h[1-6]|li)>", RegexOption.IGNORE_CASE), "\n\n")
        .replace(Regex("<li[^>]*>", RegexOption.IGNORE_CASE), "• ")
        .replace(Regex("<[^>]*>"), "")
        .replace("&nbsp;", " ")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&quot;", "\"")
        .replace("&#39;", "'")
        .replace("&apos;", "'")
        .replace("&amp;", "&")
        .replace(Regex("[ \t]+"), " ")
        .replace(Regex("\n{3,}"), "\n\n")
        .trim()
}

/** Rough reading time shown on cards and the detail byline — the design always reads "N min read". */
internal fun readMinutes(contentHtml: String): Int {
    val words = contentHtml
        .replace(Regex("<[^>]+>"), " ")
        .trim()
        .split(Regex("\\s+"))
        .count { it.isNotBlank() }
    return maxOf(1, words / 200)
}

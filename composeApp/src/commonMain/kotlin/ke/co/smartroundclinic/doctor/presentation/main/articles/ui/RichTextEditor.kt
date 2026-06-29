package ke.co.smartroundclinic.doctor.presentation.main.articles.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor as LibRichTextEditor
import ke.co.smartroundclinic.doctor.presentation.theme.Primary40
import ke.co.smartroundclinic.doctor.presentation.theme.Primary90
import ke.co.smartroundclinic.doctor.presentation.theme.ShapeInput

@Composable
internal fun RichTextEditor(
    state: RichTextState,
    modifier: Modifier = Modifier,
    placeholder: String = "Write your article content here…",
) {
    var showLinkDialog by remember { mutableStateOf(false) }
    var pendingLinkText by remember { mutableStateOf("") }
    var pendingLinkUrl by remember { mutableStateOf("") }

    if (showLinkDialog) {
        AlertDialog(
            onDismissRequest = { showLinkDialog = false; pendingLinkText = ""; pendingLinkUrl = "" },
            title = { Text("Insert Link") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = pendingLinkText,
                        onValueChange = { pendingLinkText = it },
                        label = { Text("Link text") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = pendingLinkUrl,
                        onValueChange = { pendingLinkUrl = it },
                        label = { Text("URL") },
                        placeholder = { Text("https://") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (pendingLinkUrl.isNotBlank()) {
                        state.addLink(pendingLinkText.ifBlank { pendingLinkUrl }, pendingLinkUrl)
                    }
                    showLinkDialog = false; pendingLinkText = ""; pendingLinkUrl = ""
                }) { Text("Insert") }
            },
            dismissButton = {
                TextButton(onClick = { showLinkDialog = false; pendingLinkText = ""; pendingLinkUrl = "" }) { Text("Cancel") }
            },
        )
    }

    val isBold = state.currentSpanStyle.fontWeight == FontWeight.Bold
    val isItalic = state.currentSpanStyle.fontStyle == FontStyle.Italic
    val isUnderline = state.currentSpanStyle.textDecoration?.contains(TextDecoration.Underline) == true

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 4.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            EditorIconBtn(Icons.Filled.FormatBold, "Bold", active = isBold) {
                state.toggleSpanStyle(SpanStyle(fontWeight = FontWeight.Bold))
            }
            EditorIconBtn(Icons.Filled.FormatItalic, "Italic", active = isItalic) {
                state.toggleSpanStyle(SpanStyle(fontStyle = FontStyle.Italic))
            }
            EditorIconBtn(Icons.Filled.FormatUnderlined, "Underline", active = isUnderline) {
                state.toggleSpanStyle(SpanStyle(textDecoration = TextDecoration.Underline))
            }

            EditorDivider()

            EditorIconBtn(Icons.Filled.FormatListBulleted, "Bullet list", active = state.isUnorderedList) {
                state.toggleUnorderedList()
            }
            EditorIconBtn(Icons.Filled.FormatListNumbered, "Numbered list", active = state.isOrderedList) {
                state.toggleOrderedList()
            }

            EditorDivider()

            EditorIconBtn(Icons.Filled.Link, "Link") { showLinkDialog = true }
        }

        LibRichTextEditor(
            state = state,
            modifier = Modifier.fillMaxWidth().weight(1f),
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
            ),
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
        )
    }
}

@Composable
private fun EditorIconBtn(
    icon: ImageVector,
    label: String,
    active: Boolean = false,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = if (active) Modifier.background(Primary90, ShapeInput) else Modifier,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (active) Primary40 else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun EditorTextBtn(text: String, active: Boolean = false, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = if (active) Modifier.background(Primary90, ShapeInput) else Modifier,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = if (active) Primary40 else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun EditorDivider() {
    Box(modifier = Modifier.width(1.dp).height(28.dp).background(MaterialTheme.colorScheme.outlineVariant))
}

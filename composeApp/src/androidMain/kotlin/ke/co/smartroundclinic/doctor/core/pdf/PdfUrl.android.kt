package ke.co.smartroundclinic.doctor.core.pdf

import android.net.Uri

// Android WebView does not render PDFs inline — wrap in Google Docs viewer
actual fun buildPdfViewerUrl(originalUrl: String): String =
    "https://docs.google.com/gview?embedded=true&url=${Uri.encode(originalUrl)}"

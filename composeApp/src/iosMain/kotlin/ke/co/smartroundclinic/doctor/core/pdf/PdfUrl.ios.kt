package ke.co.smartroundclinic.doctor.core.pdf

import platform.Foundation.NSURLComponents
import platform.Foundation.NSURLQueryItem

actual fun buildPdfViewerUrl(originalUrl: String): String {
    val components = NSURLComponents()
    components.scheme = "https"
    components.host = "docs.google.com"
    components.path = "/gview"
    components.queryItems = listOf(
        NSURLQueryItem(name = "embedded", value = "true"),
        NSURLQueryItem(name = "url", value = originalUrl),
    )
    return components.URL?.absoluteString
        ?: "https://docs.google.com/gview?embedded=true&url=$originalUrl"
}

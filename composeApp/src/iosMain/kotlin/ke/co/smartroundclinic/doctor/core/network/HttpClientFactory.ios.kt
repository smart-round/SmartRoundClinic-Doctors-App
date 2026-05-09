package ke.co.smartroundclinic.doctor.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin

actual fun createHttpClient(tokenProvider: () -> String?): HttpClient =
    buildHttpClient(Darwin.create(), tokenProvider)

package ke.co.smartroundclinic.doctor.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin

actual fun createHttpClient(): HttpClient = buildHttpClient(Darwin.create())

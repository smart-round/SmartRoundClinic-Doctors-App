package ke.co.smartroundclinic.doctor.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.response.GetAllMyLicencesRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.GetMyLicenceByIdRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.SuccessRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.UploadLicenceRes
import ke.co.smartroundclinic.doctor.domain.repository.LicenceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class LicenceRepositoryImpl(private val client: HttpClient) : LicenceRepository {

    override suspend fun uploadLicence(licenceName: String, licenceFileName: String, licence: ByteArray): Resource<UploadLicenceRes> = withContext(Dispatchers.IO) {
        val mimeType = mimeTypeFromFileName(licenceFileName)
        try {
            Resource.Success(
                client.post("doctor/licence") {
                    setBody(
                        MultiPartFormDataContent(
                            formData {
                                append("licenceName", licenceName)
                                append(
                                    key = "licenceFile",
                                    value = licence,
                                    headers = Headers.build {
                                        append(
                                            HttpHeaders.ContentDisposition,
                                            "form-data; name=\"licenceFile\"; filename=\"$licenceFileName\"",
                                        )
                                        append(HttpHeaders.ContentType, mimeType)
                                    },
                                )
                            }
                        )
                    )
                }.body<UploadLicenceRes>()
            )
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to upload licence")
        }
    }

    private fun mimeTypeFromFileName(fileName: String): String {
        return when (fileName.substringAfterLast('.', "").lowercase()) {
            "pdf"  -> "application/pdf"
            "jpg", "jpeg" -> "image/jpeg"
            "png"  -> "image/png"
            "webp" -> "image/webp"
            "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            "doc"  -> "application/msword"
            else   -> "application/octet-stream"
        }
    }

    override suspend fun getAllMyLicences(): Resource<GetAllMyLicencesRes> = withContext(Dispatchers.IO) {
        try {
            Resource.Success(client.get("doctor/licence/all").body<GetAllMyLicencesRes>())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to load licences")
        }
    }

    override suspend fun getMyLicenceById(id: String): Resource<GetMyLicenceByIdRes> = withContext(Dispatchers.IO) {
        try {
            Resource.Success(client.get("doctor/licence") { parameter("id", id) }.body<GetMyLicenceByIdRes>())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to load licence")
        }
    }

    override suspend fun deleteMyLicence(idString: String): Resource<SuccessRes> = withContext(Dispatchers.IO) {
        try {
            Resource.Success(client.delete("doctor/licence") { parameter("id", idString) }.body<SuccessRes>())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete licence")
        }
    }
}

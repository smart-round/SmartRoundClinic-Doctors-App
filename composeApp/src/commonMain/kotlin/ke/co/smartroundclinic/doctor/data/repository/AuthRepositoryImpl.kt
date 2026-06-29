package ke.co.smartroundclinic.doctor.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.request.RequestRefreshTokenReq
import ke.co.smartroundclinic.doctor.data.remote.dto.request.SignInReq
import ke.co.smartroundclinic.doctor.data.remote.dto.request.UpdatePasswordReq
import ke.co.smartroundclinic.doctor.data.remote.dto.request.UpdatePersonalInformationReq
import ke.co.smartroundclinic.doctor.data.remote.dto.response.DeleteProfilePictureRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.GetComplianceStatusRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.GetUserRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.SignInRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.SuccessRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.UpdatePersonalInformationRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.UploadProfilePictureRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.refreshToken.RefreshTokenRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.toDomain
import ke.co.smartroundclinic.doctor.domain.model.AuthTokens
import ke.co.smartroundclinic.doctor.domain.model.DoctorSignUpData
import ke.co.smartroundclinic.doctor.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class AuthRepositoryImpl(private val client: HttpClient) : AuthRepository {

    override suspend fun signUp(data: DoctorSignUpData): Resource<SuccessRes> =
        withContext(Dispatchers.IO) {
            try {
                val response = client.post("auth/doctors/sign-up") {
                    setBody(
                        MultiPartFormDataContent(
                            formData {
                                append("fullName", data.fullName)
                                append("email", data.email)
                                append("password", data.password)
                                append("gender", data.gender)
                                data.dateOfBirth?.let { append("dateOfBirth", it) }
                                append("phoneNumber", data.phoneNumber)
                                append("kraPin", data.kraPin)
                                append("specializationId", data.specializationId)
                                append("licenceName", data.licenceName)
                                append("bankName", data.bankName)
                                append("bankCode", data.bankCode)
                                append("branchCode", data.branchCode)
                                append("branchName", data.branchName)
                                append("accountName", data.accountName)
                                append("accountNumber", data.accountNumber)
                                data.kmpdcRegNumber?.let { append("kmpdcRegNumber", it) }
                                data.title?.let { append("title", it) }
                                data.bio?.let { append("bio", it) }
                                data.yearsOfExperience?.let { append("yearsOfExperience", it.toString()) }
                                if (data.languages.isNotEmpty()) append("languages", data.languages.joinToString(","))
                                data.facilityName?.let { append("facilityName", it) }
                                append(
                                    key = "licenceFile",
                                    value = data.licenceFile,
                                    headers = Headers.build {
                                        append(
                                            HttpHeaders.ContentDisposition,
                                            "form-data; name=\"licenceFile\"; filename=\"${data.licenceFileName}\""
                                        )
                                        append(
                                            HttpHeaders.ContentType,
                                            data.licenceFileMimeType
                                        )
                                    },
                                )
                                data.profilePicture?.let { photo ->
                                    append(
                                        key = "profilePicture",
                                        value = photo,
                                        headers = Headers.build {
                                            append(
                                                HttpHeaders.ContentDisposition,
                                                "form-data; name=\"profilePicture\"; filename=\"profile.jpg\""
                                            )
                                            append(HttpHeaders.ContentType, "image/jpeg")
                                        },
                                    )
                                }
                            }
                        )
                    )
                }.body<SuccessRes>()
                if (response.status) Resource.Success(data = response, message = response.message)
                else Resource.Error(message = response.message)
            } catch (e: Exception) {
                Resource.Error(e.message ?: "An unknown error occurred")
            }
        }

    override suspend fun verifyAccount(
        email: String,
        otpCode: String
    ): Resource<SuccessRes> = withContext(Dispatchers.IO) {
        try {
            val response = client.get("/auth/user/account-verification") {
                parameter(key = "email", email)
                parameter(key = "otpCode", otpCode)
            }.body<SuccessRes>()

            if (response.status) Resource.Success(data = response, message = response.message)
            else Resource.Error(message = response.message)

        } catch (e: Exception) {
            Resource.Error("An unknown error occurred")
        }
    }

    override suspend fun resendAccount(email: String): Resource<SuccessRes> = withContext(
        Dispatchers.IO
    ) {
        try {
            val response = client.get("/auth/user/account-verification/resend-otp") {
                parameter(key = "email", email)
            }.body<SuccessRes>()

            if (response.status) Resource.Success(data = response, message = response.message)
            else Resource.Error(message = response.message)

        } catch (e: Exception) {
            Resource.Error("An unknown error occurred")
        }
    }

    override suspend fun signIn(
        email: String,
        password: String,
    ): Resource<AuthTokens> = withContext(Dispatchers.IO) {
        try {
            val response = client.post("/auth/user/sign-in") {
                setBody(SignInReq(email = email, password = password))
            }.body<SignInRes>()
            if (!response.status) return@withContext Resource.Error(message = response.message, data = null)
            Resource.Success(data = response.toDomain(), message = response.message)
        } catch (e: Exception) {
            Resource.Error("An unknown error occurred")
        }
    }

    override suspend fun requestPasswordReset(email: String): Resource<SuccessRes> =
        withContext(Dispatchers.IO) {
        try {
            val response = client.post("/auth/user/password-reset/request") {
                parameter(key = "email", email)
            }.body<SuccessRes>()
            if (response.status) Resource.Success(data = null, message = response.message)
            else Resource.Error(message = response.message)
        } catch (e: Exception) {
            Resource.Error("An unknown error occurred")
        }
    }

    override suspend fun resendPasswordResetOtp(email: String): Resource<SuccessRes> = withContext(
        Dispatchers.IO) {
        try {
            val response = client.get("/auth/user/password-reset/resend-otp") {
                parameter(key = "email", email)
            }.body<SuccessRes>()
            if (response.status) Resource.Success(data = null, message = response.message)
            else Resource.Error(message = response.message)
        } catch (e: Exception) {
            Resource.Error("An unknown error occurred")
        }
    }

    override suspend fun updatePassword(body: UpdatePasswordReq): Resource<SuccessRes> =
        withContext(Dispatchers.IO) {
            try {
                val response = client.post("/auth/user/password-reset") {
                    setBody(body)
                }.body<SuccessRes>()
                if (response.status) Resource.Success(data = null, message = response.message)
                else Resource.Error(message = response.message)
            } catch (e: Exception) {
                Resource.Error("An unknown error occurred")
            }
        }

    override suspend fun requestRefreshToken(refreshToken: String): Resource<RefreshTokenRes>  = withContext(
        Dispatchers.IO){
        try {
            val response = client.post("/auth/user/token/refresh") {
                setBody(RequestRefreshTokenReq(refreshToken))
            }.body<RefreshTokenRes>()
            if (response.status) Resource.Success(data = response, message = response.message)
            else Resource.Error(message = response.message)
        } catch (e: Exception) {
            Resource.Error("An unknown error occurred")
        }
    }

    override suspend fun getUser(): Resource<GetUserRes> = withContext(Dispatchers.IO){
        try {
            val response = client.get("/auth/user") {
            }.body<GetUserRes>()
            if (response.status) Resource.Success(data = response, message = response.message)
            else Resource.Error(message = response.message)
        } catch (e: Exception) {
            Resource.Error("An unknown error occurred")
        }
    }

    override suspend fun uploadProfilePicture(profilePicture: ByteArray): Resource<UploadProfilePictureRes> = withContext(
        Dispatchers.IO) {
        try {
            val response = client.post("/auth/user/profile-picture") {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append(
                                key = "profilePicture",
                                value = profilePicture,
                                headers = Headers.build {
                                    append(
                                        HttpHeaders.ContentDisposition,
                                        "form-data; name=\"profilePicture\"; filename=\"profile.jpg\""
                                    )
                                    append(HttpHeaders.ContentType, "image/jpeg")
                                },
                            )
                        }
                    )
                )
            }.body<UploadProfilePictureRes>()
            if (response.status) Resource.Success(data = response, message = response.message)
            else Resource.Error(message = response.message)
        } catch (e: Exception) {
            Resource.Error("An unknown error occurred")
        }
    }

    override suspend fun deleteProfilePicture(): Resource<DeleteProfilePictureRes> = withContext(
        Dispatchers.IO) {
        try {
            val response = client.delete  ("/auth/user/profile-picture") {
            }.body<DeleteProfilePictureRes>()
            if (response.status) Resource.Success(data = response, message = response.message)
            else Resource.Error(message = response.message)
        } catch (e: Exception) {
            Resource.Error("An unknown error occurred")
        }
    }

    override suspend fun updatePersonalInfo(body: UpdatePersonalInformationReq): Resource<UpdatePersonalInformationRes> = withContext(
        Dispatchers.IO) {
        try {
            val response = client.put("/auth/user") {
                setBody(body)
            }.body<UpdatePersonalInformationRes>()
            if (response.status) Resource.Success(data = response, message = response.message)
            else Resource.Error(message = response.message)
        } catch (e: Exception) {
            Resource.Error("An unknown error occurred")
        }
    }

    override suspend fun getComplianceStatus(): Resource<GetComplianceStatusRes>  = withContext(
        Dispatchers.IO){
        try {
            val response = client.get("/doctor/compliance/status") {
            }.body<GetComplianceStatusRes>()
            if (response.status) Resource.Success(data = response, message = response.message)
            else Resource.Error(message = response.message)
        } catch (e: Exception) {
            Resource.Error("An unknown error occurred")
        }
    }
}

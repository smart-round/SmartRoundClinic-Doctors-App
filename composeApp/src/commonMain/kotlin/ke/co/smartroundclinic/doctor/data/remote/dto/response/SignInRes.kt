package ke.co.smartroundclinic.doctor.data.remote.dto.response

import ke.co.smartroundclinic.doctor.domain.model.AuthTokens
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class SignInRes(
    val data: Data? = null,
    val httpStatusCode: Int, // 200
    val message: String, // Login successful
    val status: Boolean // true
)

@Serializable
data class Data(
    val accessToken: String? = null,
    val accountStatus: String,
    val permissions: List<JsonElement>,
    val policyGroupIds: List<JsonElement>,
    val refreshToken: String? = null,
    val verificationStatus: String
)

fun SignInRes.toDomain() = AuthTokens(
    accessToken = data?.accessToken,
    refreshToken = data?.refreshToken,
    accountStatus = data?.accountStatus,
    verificationStatus = data?.verificationStatus,
)
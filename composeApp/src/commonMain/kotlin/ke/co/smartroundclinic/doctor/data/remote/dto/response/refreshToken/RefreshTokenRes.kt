package ke.co.smartroundclinic.doctor.data.remote.dto.response.refreshToken


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class RefreshTokenRes(
    val data: Data,
    val httpStatusCode: Int, // 200
    val message: String, // Token refreshed successfully
    val status: Boolean // true
)

@Serializable
data class Data(
    val accessToken: String, // eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJzbWFydHJvdW5kY2xpbmljIiwiaXNzIjoic21hcnRyb3VuZGNsaW5pYy5jby5rZSIsInVzZXJJZCI6IjY5Zjg4NDZjMzE5ZDU5ZTE1NGZkYWIzYyIsInJvbGUiOiJET0NUT1IiLCJwZXJtaXNzaW9ucyI6IiIsImV4cCI6MTc3ODQzNDIzMH0.T9ybpfaZ6Xbe3R4VvvoL9n2xWrvOwnmNWOu6XXt4RDE
    val accountStatus: String, // INACTIVE
    val permissions: List<JsonElement>,
    val policyGroupIds: List<JsonElement>,
    val refreshToken: String, // eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJzbWFydHJvdW5kY2xpbmljIiwiaXNzIjoic21hcnRyb3VuZGNsaW5pYy5jby5rZSIsInVzZXJJZCI6IjY5Zjg4NDZjMzE5ZDU5ZTE1NGZkYWIzYyIsImV4cCI6MTc4MDkzOTgzMH0.aN0GfV6pJdBJsBqLcUlwwv0GJZw4wETwH8aG-06D87g
    val verificationStatus: String // PENDING_APPROVAL
)
package ke.co.smartroundclinic.doctor.domain.model

data class AuthTokens(
    val accessToken: String?,
    val refreshToken: String?,
    val accountStatus: String,
    val verificationStatus: String,
)

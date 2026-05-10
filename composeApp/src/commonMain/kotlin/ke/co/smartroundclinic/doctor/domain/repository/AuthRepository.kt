package ke.co.smartroundclinic.doctor.domain.repository

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.request.UpdatePasswordReq
import ke.co.smartroundclinic.doctor.data.remote.dto.request.UpdatePersonalInformationReq
import ke.co.smartroundclinic.doctor.data.remote.dto.response.DeleteProfilePictureRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.GetUserRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.SuccessRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.UpdatePersonalInformationRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.UploadProfilePictureRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.refreshToken.RefreshTokenRes
import ke.co.smartroundclinic.doctor.domain.model.AuthTokens
import ke.co.smartroundclinic.doctor.domain.model.DoctorSignUpData

interface AuthRepository {
    suspend fun signUp(data: DoctorSignUpData): Resource<SuccessRes>
    suspend fun verifyAccount(email: String, otpCode: String): Resource<SuccessRes>
    suspend fun resendAccount(email: String): Resource<SuccessRes>
    suspend fun signIn(email: String, password: String): Resource<AuthTokens>
    suspend fun requestPasswordReset(email: String): Resource<SuccessRes>
    suspend fun resendPasswordResetOtp(email: String): Resource<SuccessRes>
    suspend fun updatePassword(body: UpdatePasswordReq): Resource<SuccessRes>
    suspend fun requestRefreshToken(refreshToken: String): Resource<RefreshTokenRes>
    suspend fun getUser(): Resource<GetUserRes>
    suspend fun uploadProfilePicture(profilePicture: ByteArray): Resource<UploadProfilePictureRes>
    suspend fun deleteProfilePicture(): Resource<DeleteProfilePictureRes>
    suspend fun updatePersonalInfo(body: UpdatePersonalInformationReq): Resource<UpdatePersonalInformationRes>
}

package ke.co.smartroundclinic.doctor.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.request.CreateReferralReq
import ke.co.smartroundclinic.doctor.data.remote.dto.response.ReferralEligibilityData
import ke.co.smartroundclinic.doctor.data.remote.dto.response.ReferralEligibilityRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.ReferralData
import ke.co.smartroundclinic.doctor.data.remote.dto.response.ReferralListRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.ReferralRes
import ke.co.smartroundclinic.doctor.domain.repository.ReferralRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class ReferralRepositoryImpl(private val client: HttpClient) : ReferralRepository {

    override suspend fun checkEligibility(appointmentId: String): Resource<ReferralEligibilityData> =
        withContext(Dispatchers.IO) {
            try {
                val res = client.get("referral/eligibility") { parameter("appointmentId", appointmentId) }.body<ReferralEligibilityRes>()
                if (res.status && res.data != null) Resource.Success(res.data)
                else Resource.Error(res.message.ifBlank { "Failed to check referral eligibility" })
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Failed to check referral eligibility")
            }
        }

    override suspend fun createReferral(req: CreateReferralReq): Resource<ReferralData> =
        withContext(Dispatchers.IO) {
            try {
                val res = client.post("referral") { setBody(req) }.body<ReferralRes>()
                if (res.status && res.data != null) Resource.Success(res.data, res.message)
                else Resource.Error(res.message.ifBlank { "Failed to create referral" })
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Failed to create referral")
            }
        }

    override suspend fun getMyReferrals(): Resource<List<ReferralData>> =
        withContext(Dispatchers.IO) {
            try {
                val res = client.get("referral/mine").body<ReferralListRes>()
                if (res.status) Resource.Success(res.data ?: emptyList())
                else Resource.Error(res.message.ifBlank { "Failed to load sent referrals" })
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Failed to load sent referrals")
            }
        }

    override suspend fun getReceivedReferrals(): Resource<List<ReferralData>> =
        withContext(Dispatchers.IO) {
            try {
                val res = client.get("referral/received").body<ReferralListRes>()
                if (res.status) Resource.Success(res.data ?: emptyList())
                else Resource.Error(res.message.ifBlank { "Failed to load received referrals" })
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Failed to load received referrals")
            }
        }
}

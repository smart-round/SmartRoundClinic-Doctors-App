package ke.co.smartroundclinic.doctor.domain.repository

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.request.CreateReferralReq
import ke.co.smartroundclinic.doctor.data.remote.dto.response.ReferralEligibilityData
import ke.co.smartroundclinic.doctor.data.remote.dto.response.ReferralData

interface ReferralRepository {
    suspend fun checkEligibility(appointmentId: String): Resource<ReferralEligibilityData>
    suspend fun createReferral(req: CreateReferralReq): Resource<ReferralData>
}

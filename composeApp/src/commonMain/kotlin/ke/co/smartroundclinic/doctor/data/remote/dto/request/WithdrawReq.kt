package ke.co.smartroundclinic.doctor.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class WithdrawReq(
    val idNumber: String,
    val amount: Double,
)

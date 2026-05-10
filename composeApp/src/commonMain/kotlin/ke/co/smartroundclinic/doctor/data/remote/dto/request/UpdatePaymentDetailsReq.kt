package ke.co.smartroundclinic.doctor.data.remote.dto.request


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdatePaymentDetailsReq(
    val accountName: String? = null, // Lazarus Pasaka Mutuku
    val accountNumber: String?  = null, // 123456789
    val bankCode: String?  = null, // 01
    val bankName: String?  = null, // Kenya Commercial Bank Limited
    val branchCode: String?  = null, // 091
    val branchName: String?  = null // Eastleigh
)
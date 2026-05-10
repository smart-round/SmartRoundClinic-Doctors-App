package ke.co.smartroundclinic.doctor.data.remote.dto.request


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddPaymentDetailsReq(
    val accountName: String, // Lazarus Pasaka Mutuku
    val accountNumber: String, // 123456789
    val bankCode: String, // 01
    val bankName: String, // Kenya Commercial Bank Limited
    val branchCode: String, // 091
    val branchName: String // Eastleigh
)
package ke.co.smartroundclinic.doctor.data.remote.dto.response

import ke.co.smartroundclinic.doctor.domain.model.PaymentDetails
import kotlinx.serialization.Serializable

@Serializable
data class GetPaymentDetailsRes(
    val `data`: PaymentDetailsData? = null,
    val httpStatusCode: Int, // 200
    val message: String, // Success
    val status: Boolean // true
)

@Serializable
data class PaymentDetailsData(
    val accountName: String, // Mutuku kyalo
    val accountNumber: String, // 123456789
    val bankCode: String, // 03
    val bankName: String, // Absa Bank Kenya Plc
    val branchCode: String, // 049
    val branchName: String, // Lavington Branch
    val createdAt: String, // 2026-05-04T11:35:08.258198501Z
    val doctorId: String, // 69f8846c319d59e154fdab3c
    val id: String // 69f8846c319d59e154fdab46
)

fun PaymentDetailsData.toDomain() = PaymentDetails(
    id = id,
    doctorId = doctorId,
    accountName = accountName,
    accountNumber = accountNumber,
    bankCode = bankCode,
    bankName = bankName,
    branchCode = branchCode,
    branchName = branchName,
    createdAt = createdAt,
)

package ke.co.smartroundclinic.doctor.data.remote.dto.response

import ke.co.smartroundclinic.doctor.domain.model.PaymentDetails
import kotlinx.serialization.Serializable

@Serializable
data class UpdatePaymentDetailsRes(
    val data: UpdatePaymentDetailsData? = null,
    val httpStatusCode: Int, // 200
    val message: String, // Success
    val status: Boolean // true
)

@Serializable
data class UpdatePaymentDetailsData(
    val accountName: String, // Lazarus Pasaka Mutuku
    val accountNumber: String, // 123456789
    val bankCode: String, // 01
    val bankName: String, // Kenya Commercial Bank Limited
    val branchCode: String, // 091
    val branchName: String, // Eastleigh
    val createdAt: String, // 2026-05-10T11:04:01.586904Z
    val doctorId: String, // 69f8846c319d59e154fdab3c
    val id: String, // 6a0066214be969008b8125fa
    val updatedAt: String // 1778411150485
)

fun UpdatePaymentDetailsData.toDomain() = PaymentDetails(
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

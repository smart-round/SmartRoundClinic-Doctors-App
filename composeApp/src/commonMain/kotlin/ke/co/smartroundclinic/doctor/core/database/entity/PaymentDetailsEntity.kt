package ke.co.smartroundclinic.doctor.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ke.co.smartroundclinic.doctor.domain.model.PaymentDetails

@Entity(tableName = "payment_details")
data class PaymentDetailsEntity(
    @PrimaryKey val id: String,
    val doctorId: String,
    val accountName: String,
    val accountNumber: String,
    val bankCode: String,
    val bankName: String,
    val branchCode: String,
    val branchName: String,
    val createdAt: String,
)

fun PaymentDetailsEntity.toDomain() = PaymentDetails(
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

fun PaymentDetails.toEntity() = PaymentDetailsEntity(
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

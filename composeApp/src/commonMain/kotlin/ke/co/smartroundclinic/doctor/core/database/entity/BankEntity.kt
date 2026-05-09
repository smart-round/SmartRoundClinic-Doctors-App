package ke.co.smartroundclinic.doctor.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ke.co.smartroundclinic.doctor.domain.model.Bank
import ke.co.smartroundclinic.doctor.domain.model.BankBranch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Entity(tableName = "banks")
data class BankEntity(
    @PrimaryKey val id: String,
    val bankCode: String,
    val bankName: String,
    val branchesJson: String
)

@Serializable
private data class BranchJson(val branchCode: String, val branchName: String)

private val bankEntityJson = Json { ignoreUnknownKeys = true }

fun BankEntity.toDomain(): Bank = Bank(
    id = id,
    bankCode = bankCode,
    bankName = bankName,
    branches = bankEntityJson.decodeFromString<List<BranchJson>>(branchesJson)
        .map { BankBranch(branchCode = it.branchCode, branchName = it.branchName) }
)

fun Bank.toEntity(): BankEntity = BankEntity(
    id = id,
    bankCode = bankCode,
    bankName = bankName,
    branchesJson = bankEntityJson.encodeToString(
        branches.map { BranchJson(branchCode = it.branchCode, branchName = it.branchName) }
    )
)

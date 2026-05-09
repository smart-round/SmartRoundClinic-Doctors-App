package ke.co.smartroundclinic.doctor.data.remote.dto.response.localBanks

import ke.co.smartroundclinic.doctor.domain.model.Bank
import ke.co.smartroundclinic.doctor.domain.model.BankBranch
import kotlinx.serialization.Serializable

@Serializable
data class GetLocalBanksRes(
    val data: List<Item>,
    val httpStatusCode: Int,
    val message: String,
    val status: Boolean
)

@Serializable
data class Branches(
    val branchCode: String,
    val branchName: String
)

@Serializable
data class Item(
    val bankCode: String,
    val bankName: String,
    val branches: List<Branches>,
    val id: String
)

fun GetLocalBanksRes.toModel(): List<Bank> = data.map { it.toDomain() }

fun Item.toDomain(): Bank = Bank(
    id = id,
    bankCode = bankCode,
    bankName = bankName,
    branches = branches.map { BankBranch(branchCode = it.branchCode, branchName = it.branchName) }
)

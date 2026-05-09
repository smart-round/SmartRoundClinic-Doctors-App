package ke.co.smartroundclinic.doctor.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.response.localBanks.GetLocalBanksRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.localBanks.toModel
import ke.co.smartroundclinic.doctor.domain.model.Bank
import ke.co.smartroundclinic.doctor.domain.repository.BankRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class BankRepositoryImpl(private val client: HttpClient) : BankRepository {

    override suspend fun getLocalBanks(): Resource<List<Bank>> = withContext(Dispatchers.IO) {
        try {
            Resource.Success(client.get("banks").body<GetLocalBanksRes>().toModel())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }
}

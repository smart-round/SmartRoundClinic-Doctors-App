package ke.co.smartroundclinic.doctor.domain.usecase.datastore

import ke.co.smartroundclinic.doctor.domain.repository.DatastoreRepository

class GetKeyUseCase(private val repository: DatastoreRepository) {
    suspend operator fun invoke(key: String): String? = repository.getKey(key)
}
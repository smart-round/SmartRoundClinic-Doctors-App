package ke.co.smartroundclinic.doctor.domain.usecase.datastore

import ke.co.smartroundclinic.doctor.domain.repository.DatastoreRepository

class SetKeyUseCase(private val repository: DatastoreRepository) {
    suspend operator fun invoke(key: String, value: String?): Boolean = repository.setKey(key, value)
}

package ke.co.smartroundclinic.doctor.domain.usecase.datastore

import ke.co.smartroundclinic.doctor.domain.repository.DatastoreRepository
import kotlinx.coroutines.flow.Flow

class ObserveKeyUseCase(private val repository: DatastoreRepository) {
    operator fun invoke(key: String): Flow<String?> = repository.observe(key)
}
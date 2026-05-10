package ke.co.smartroundclinic.doctor.domain.repository

import ke.co.smartroundclinic.doctor.domain.model.DoctorSpecialization
import kotlinx.coroutines.flow.Flow

interface DoctorSpecializationLocalRepository {
    fun observeSpecialization(): Flow<DoctorSpecialization?>
    suspend fun saveSpecialization(specialization: DoctorSpecialization)
    suspend fun clearSpecializations()
}

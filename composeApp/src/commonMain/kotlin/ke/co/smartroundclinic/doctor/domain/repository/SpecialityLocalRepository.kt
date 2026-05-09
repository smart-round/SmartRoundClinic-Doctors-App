package ke.co.smartroundclinic.doctor.domain.repository

import ke.co.smartroundclinic.doctor.domain.model.Speciality

interface SpecialityLocalRepository {
    suspend fun getSpecialities(): List<Speciality>
    suspend fun saveSpecialities(specialities: List<Speciality>)
    suspend fun hasSpecialities(): Boolean
}

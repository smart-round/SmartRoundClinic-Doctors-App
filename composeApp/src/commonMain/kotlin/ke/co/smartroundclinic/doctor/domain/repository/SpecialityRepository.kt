package ke.co.smartroundclinic.doctor.domain.repository

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.Speciality

interface SpecialityRepository {
    suspend fun getAllSpecialities(): Resource<List<Speciality>>
}

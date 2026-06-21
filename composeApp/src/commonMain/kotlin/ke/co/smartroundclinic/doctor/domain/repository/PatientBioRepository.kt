package ke.co.smartroundclinic.doctor.domain.repository

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.response.PatientBioResponse

interface PatientBioRepository {
    suspend fun getPatientBio(patientId: String): Resource<PatientBioResponse>
}

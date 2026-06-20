package ke.co.smartroundclinic.doctor.domain.repository

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.request.SaveMedicalRecordReq
import ke.co.smartroundclinic.doctor.domain.model.MedicalRecord

interface MedicalRecordRepository {
    suspend fun save(req: SaveMedicalRecordReq): Resource<MedicalRecord?>
    suspend fun getByAppointmentId(appointmentId: String): Resource<MedicalRecord?>
    suspend fun getPatientHistory(patientId: String): Resource<List<MedicalRecord>>
}

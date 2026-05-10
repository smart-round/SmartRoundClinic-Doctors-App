package ke.co.smartroundclinic.doctor.domain.repository

import ke.co.smartroundclinic.doctor.domain.model.Appointment
import kotlinx.coroutines.flow.Flow

interface AppointmentLocalRepository {
    fun observeAppointments(): Flow<List<Appointment>>
    suspend fun upsertAll(appointments: List<Appointment>)
    suspend fun upsertAppointment(appointment: Appointment)
    suspend fun clearAppointments()
}

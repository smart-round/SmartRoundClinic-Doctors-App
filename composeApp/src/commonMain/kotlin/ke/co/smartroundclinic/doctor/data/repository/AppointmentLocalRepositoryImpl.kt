package ke.co.smartroundclinic.doctor.data.repository

import ke.co.smartroundclinic.doctor.core.database.dao.AppointmentDao
import ke.co.smartroundclinic.doctor.core.database.entity.toDomain
import ke.co.smartroundclinic.doctor.core.database.entity.toEntity
import ke.co.smartroundclinic.doctor.domain.model.Appointment
import ke.co.smartroundclinic.doctor.domain.repository.AppointmentLocalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppointmentLocalRepositoryImpl(private val dao: AppointmentDao) : AppointmentLocalRepository {
    override fun observeAppointments(): Flow<List<Appointment>> =
        dao.observeAppointments().map { list -> list.map { it.toDomain() } }

    override suspend fun upsertAll(appointments: List<Appointment>) =
        dao.upsertAppointments(appointments.map { it.toEntity() })

    override suspend fun upsertAppointment(appointment: Appointment) =
        dao.upsertAppointment(appointment.toEntity())

    override suspend fun clearAppointments() = dao.clearAppointments()
}

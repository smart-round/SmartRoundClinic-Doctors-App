package ke.co.smartroundclinic.doctor.data.repository

import ke.co.smartroundclinic.doctor.core.database.dao.PaymentDetailsDao
import ke.co.smartroundclinic.doctor.core.database.entity.toDomain
import ke.co.smartroundclinic.doctor.core.database.entity.toEntity
import ke.co.smartroundclinic.doctor.domain.model.PaymentDetails
import ke.co.smartroundclinic.doctor.domain.repository.PaymentDetailsLocalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PaymentDetailsLocalRepositoryImpl(private val dao: PaymentDetailsDao) : PaymentDetailsLocalRepository {

    override fun observePaymentDetails(): Flow<PaymentDetails?> =
        dao.observePaymentDetails().map { it?.toDomain() }

    override suspend fun savePaymentDetails(details: PaymentDetails) =
        dao.upsertPaymentDetails(details.toEntity())

    override suspend fun clearPaymentDetails() = dao.clearPaymentDetails()
}

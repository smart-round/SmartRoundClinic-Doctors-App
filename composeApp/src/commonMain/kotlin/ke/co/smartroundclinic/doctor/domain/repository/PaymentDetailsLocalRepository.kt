package ke.co.smartroundclinic.doctor.domain.repository

import ke.co.smartroundclinic.doctor.domain.model.PaymentDetails
import kotlinx.coroutines.flow.Flow

interface PaymentDetailsLocalRepository {
    fun observePaymentDetails(): Flow<PaymentDetails?>
    suspend fun savePaymentDetails(details: PaymentDetails)
    suspend fun clearPaymentDetails()
}

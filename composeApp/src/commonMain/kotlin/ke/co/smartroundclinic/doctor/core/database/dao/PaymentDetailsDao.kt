package ke.co.smartroundclinic.doctor.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import ke.co.smartroundclinic.doctor.core.database.entity.PaymentDetailsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDetailsDao {
    @Query("SELECT * FROM payment_details LIMIT 1")
    fun observePaymentDetails(): Flow<PaymentDetailsEntity?>

    @Upsert
    suspend fun upsertPaymentDetails(details: PaymentDetailsEntity)

    @Query("DELETE FROM payment_details")
    suspend fun clearPaymentDetails()
}

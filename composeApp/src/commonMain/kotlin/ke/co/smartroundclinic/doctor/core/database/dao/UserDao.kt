package ke.co.smartroundclinic.doctor.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import ke.co.smartroundclinic.doctor.core.database.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users LIMIT 1")
    fun observeUser(): Flow<UserEntity?>

    @Upsert
    suspend fun upsertUser(user: UserEntity)

    /**
     * The table is only ever meant to hold the signed-in doctor. Upserting alone keys on the user
     * id, so signing in as a different account left the previous row behind — and since
     * [observeUser] takes an unordered `LIMIT 1`, the app could read back the stale row and show
     * its long-expired presigned profile-picture URL. Replacing wholesale keeps it to one row.
     */
    @Transaction
    suspend fun replaceUser(user: UserEntity) {
        clearUser()
        upsertUser(user)
    }

    @Query("DELETE FROM users")
    suspend fun clearUser()
}

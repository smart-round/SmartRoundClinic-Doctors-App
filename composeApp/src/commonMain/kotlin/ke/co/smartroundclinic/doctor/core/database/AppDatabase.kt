package ke.co.smartroundclinic.doctor.core.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import ke.co.smartroundclinic.doctor.core.database.dao.AppointmentDao
import ke.co.smartroundclinic.doctor.core.database.dao.ArticleCategoryDao
import ke.co.smartroundclinic.doctor.core.database.dao.ArticleDao
import ke.co.smartroundclinic.doctor.core.database.dao.BankDao
import ke.co.smartroundclinic.doctor.core.database.dao.DoctorAvailabilityDao
import ke.co.smartroundclinic.doctor.core.database.dao.DoctorSpecializationDao
import ke.co.smartroundclinic.doctor.core.database.dao.PaymentDetailsDao
import ke.co.smartroundclinic.doctor.core.database.dao.SpecialityDao
import ke.co.smartroundclinic.doctor.core.database.dao.UserDao
import ke.co.smartroundclinic.doctor.core.database.entity.AppointmentEntity
import ke.co.smartroundclinic.doctor.core.database.entity.ArticleCategoryEntity
import ke.co.smartroundclinic.doctor.core.database.entity.ArticleEntity
import ke.co.smartroundclinic.doctor.core.database.entity.BankEntity
import ke.co.smartroundclinic.doctor.core.database.entity.DoctorAvailabilityEntity
import ke.co.smartroundclinic.doctor.core.database.entity.DoctorSpecializationEntity
import ke.co.smartroundclinic.doctor.core.database.entity.PaymentDetailsEntity
import ke.co.smartroundclinic.doctor.core.database.entity.SpecialityEntity
import ke.co.smartroundclinic.doctor.core.database.entity.UserEntity

@Database(
    entities = [
        BankEntity::class,
        SpecialityEntity::class,
        UserEntity::class,
        PaymentDetailsEntity::class,
        DoctorSpecializationEntity::class,
        DoctorAvailabilityEntity::class,
        AppointmentEntity::class,
        ArticleEntity::class,
        ArticleCategoryEntity::class,
    ],
    version = 8,
    exportSchema = false
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val bankDao: BankDao
    abstract val specialityDao: SpecialityDao
    abstract val userDao: UserDao
    abstract val paymentDetailsDao: PaymentDetailsDao
    abstract val doctorSpecializationDao: DoctorSpecializationDao
    abstract val doctorAvailabilityDao: DoctorAvailabilityDao
    abstract val appointmentDao: AppointmentDao
    abstract val articleDao: ArticleDao
    abstract val articleCategoryDao: ArticleCategoryDao
}

// Room KSP generates the actual implementations per platform.
// -Xexpect-actual-classes + @Suppress allow this pattern to compile cleanly.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

package ke.co.smartroundclinic.doctor.core.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import ke.co.smartroundclinic.doctor.core.database.dao.BankDao
import ke.co.smartroundclinic.doctor.core.database.dao.SpecialityDao
import ke.co.smartroundclinic.doctor.core.database.entity.BankEntity
import ke.co.smartroundclinic.doctor.core.database.entity.SpecialityEntity

@Database(
    entities = [BankEntity::class, SpecialityEntity::class],
    version = 1,
    exportSchema = false
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val bankDao: BankDao
    abstract val specialityDao: SpecialityDao
}

// Room KSP generates the actual implementations per platform.
// -Xexpect-actual-classes + @Suppress allow this pattern to compile cleanly.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

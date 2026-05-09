package ke.co.smartroundclinic.doctor.koin

import ke.co.smartroundclinic.doctor.core.database.AppDatabase
import ke.co.smartroundclinic.doctor.core.database.createDatabase
import ke.co.smartroundclinic.doctor.core.datastore.createDataStore
import ke.co.smartroundclinic.doctor.core.network.createHttpClient
import ke.co.smartroundclinic.doctor.core.snackbar.SnackbarController
import ke.co.smartroundclinic.doctor.core.storage.createKVault
import org.koin.dsl.module

val coreModule = module {
    single<AppDatabase> { createDatabase() }
    single { createHttpClient() }
    single { createDataStore() }
    single { createKVault() }
    single { SnackbarController() }
}

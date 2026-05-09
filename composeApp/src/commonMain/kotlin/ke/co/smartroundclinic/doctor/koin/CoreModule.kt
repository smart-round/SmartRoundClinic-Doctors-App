package ke.co.smartroundclinic.doctor.koin

import ke.co.smartroundclinic.doctor.core.database.AppDatabase
import ke.co.smartroundclinic.doctor.core.database.createDatabase
import ke.co.smartroundclinic.doctor.core.datastore.createDataStore
import com.liftric.kvault.KVault
import ke.co.smartroundclinic.doctor.core.network.createHttpClient
import ke.co.smartroundclinic.doctor.core.snackbar.SnackbarController
import ke.co.smartroundclinic.doctor.core.storage.createKVault
import ke.co.smartroundclinic.doctor.domain.usecase.auth.KEY_ACCESS_TOKEN
import org.koin.dsl.module

val coreModule = module {
    single<AppDatabase> { createDatabase() }
    single { createKVault() }
    single { createDataStore() }
    single { SnackbarController() }
    single {
        val kvault = get<KVault>()
        createHttpClient { kvault.string(KEY_ACCESS_TOKEN) }
    }
}

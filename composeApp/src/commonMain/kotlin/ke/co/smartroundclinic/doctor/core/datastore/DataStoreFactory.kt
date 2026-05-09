package ke.co.smartroundclinic.doctor.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

internal const val PREFERENCES_FILE = "app_prefs.preferences_pb"

expect fun createDataStore(): DataStore<Preferences>

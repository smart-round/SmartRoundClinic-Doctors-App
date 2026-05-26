package ke.co.smartroundclinic.doctor.core.storage

import android.content.Context
import com.liftric.kvault.KVault
import org.koin.core.context.GlobalContext
import androidx.core.content.edit

actual fun createKVault(): KVault {
    val context = GlobalContext.get().get<Context>()
    return try {
        KVault(context = context, fileName = null)
    } catch (_: Exception) {
        // Keystore key invalidated (reinstall, signing change, etc.) — wipe corrupted prefs and start fresh
        // KVault uses context.packageName as the prefs file name when fileName = null
        context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            .edit(commit = true) { clear() }
        KVault(context = context, fileName = null)
    }
}

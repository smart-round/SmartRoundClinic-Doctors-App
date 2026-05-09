package ke.co.smartroundclinic.doctor.core.storage

import com.liftric.kvault.KVault

actual fun createKVault(): KVault = KVault(serviceName = "ke.co.smartroundclinic.doctor", accessGroup = null)

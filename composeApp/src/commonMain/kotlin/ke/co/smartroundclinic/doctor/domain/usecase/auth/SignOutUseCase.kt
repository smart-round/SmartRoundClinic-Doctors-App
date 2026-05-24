package ke.co.smartroundclinic.doctor.domain.usecase.auth

import com.liftric.kvault.KVault
import ke.co.smartroundclinic.doctor.common.Constants.KEY_ACCESS_TOKEN
import ke.co.smartroundclinic.doctor.common.Constants.KEY_COMPLIANCE_IS_APPROVED
import ke.co.smartroundclinic.doctor.common.Constants.KEY_COMPLIANCE_STATUS
import ke.co.smartroundclinic.doctor.common.Constants.KEY_REFRESH_TOKEN
import ke.co.smartroundclinic.doctor.common.Constants.ONBOARDING_KEY
import ke.co.smartroundclinic.doctor.domain.repository.AppointmentLocalRepository
import ke.co.smartroundclinic.doctor.domain.repository.ArticleCategoryLocalRepository
import ke.co.smartroundclinic.doctor.domain.repository.ArticleLocalRepository
import ke.co.smartroundclinic.doctor.domain.repository.BankLocalRepository
import ke.co.smartroundclinic.doctor.domain.repository.DatastoreRepository
import ke.co.smartroundclinic.doctor.domain.repository.DoctorSpecializationLocalRepository
import ke.co.smartroundclinic.doctor.domain.repository.PaymentDetailsLocalRepository
import ke.co.smartroundclinic.doctor.domain.repository.ScheduleLocalRepository
import ke.co.smartroundclinic.doctor.domain.repository.SpecialityLocalRepository
import ke.co.smartroundclinic.doctor.domain.repository.UserLocalRepository

class SignOutUseCase(
    private val secureStorage: KVault,
    private val userLocalRepository: UserLocalRepository,
    private val paymentDetailsLocalRepository: PaymentDetailsLocalRepository,
    private val doctorSpecializationLocalRepository: DoctorSpecializationLocalRepository,
    private val scheduleLocalRepository: ScheduleLocalRepository,
    private val appointmentLocalRepository: AppointmentLocalRepository,
    private val articleLocalRepository: ArticleLocalRepository,
    private val articleCategoryLocalRepository: ArticleCategoryLocalRepository,
    private val bankLocalRepository: BankLocalRepository,
    private val specialityLocalRepository: SpecialityLocalRepository,
    private val datastoreRepository: DatastoreRepository,
) {
    suspend operator fun invoke() {
        // KVault — tokens + compliance cache
        secureStorage.deleteObject(KEY_ACCESS_TOKEN)
        secureStorage.deleteObject(KEY_REFRESH_TOKEN)
        secureStorage.deleteObject(KEY_COMPLIANCE_STATUS)
        secureStorage.deleteObject(KEY_COMPLIANCE_IS_APPROVED)

        // Room — all local caches
        userLocalRepository.clearUser()
        paymentDetailsLocalRepository.clearPaymentDetails()
        doctorSpecializationLocalRepository.clearSpecializations()
        scheduleLocalRepository.clearSchedule()
        appointmentLocalRepository.clearAppointments()
        articleLocalRepository.clearAll()
        articleCategoryLocalRepository.clearAll()
        bankLocalRepository.clearAll()
        specialityLocalRepository.clearAll()

        // DataStore — clear everything, then re-persist the device-level onboarding flag
        // so the user doesn't see onboarding screens again after re-login
        datastoreRepository.clearAll()
        datastoreRepository.setKey(ONBOARDING_KEY, "true")
    }
}

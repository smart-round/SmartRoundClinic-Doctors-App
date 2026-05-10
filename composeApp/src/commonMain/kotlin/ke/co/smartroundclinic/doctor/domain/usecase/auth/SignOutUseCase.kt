package ke.co.smartroundclinic.doctor.domain.usecase.auth

import com.liftric.kvault.KVault
import ke.co.smartroundclinic.doctor.common.Constants.KEY_ACCESS_TOKEN
import ke.co.smartroundclinic.doctor.common.Constants.KEY_REFRESH_TOKEN
import ke.co.smartroundclinic.doctor.domain.repository.AppointmentLocalRepository
import ke.co.smartroundclinic.doctor.domain.repository.DoctorSpecializationLocalRepository
import ke.co.smartroundclinic.doctor.domain.repository.PaymentDetailsLocalRepository
import ke.co.smartroundclinic.doctor.domain.repository.ScheduleLocalRepository
import ke.co.smartroundclinic.doctor.domain.repository.UserLocalRepository

class SignOutUseCase(
    private val secureStorage: KVault,
    private val userLocalRepository: UserLocalRepository,
    private val paymentDetailsLocalRepository: PaymentDetailsLocalRepository,
    private val doctorSpecializationLocalRepository: DoctorSpecializationLocalRepository,
    private val scheduleLocalRepository: ScheduleLocalRepository,
    private val appointmentLocalRepository: AppointmentLocalRepository,
) {
    suspend operator fun invoke() {
        secureStorage.deleteObject(KEY_ACCESS_TOKEN)
        secureStorage.deleteObject(KEY_REFRESH_TOKEN)
        userLocalRepository.clearUser()
        paymentDetailsLocalRepository.clearPaymentDetails()
        doctorSpecializationLocalRepository.clearSpecializations()
        scheduleLocalRepository.clearSchedule()
        appointmentLocalRepository.clearAppointments()
    }
}

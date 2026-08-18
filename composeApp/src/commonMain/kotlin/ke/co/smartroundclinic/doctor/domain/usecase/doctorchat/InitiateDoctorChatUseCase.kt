package ke.co.smartroundclinic.doctor.domain.usecase.doctorchat

import ke.co.smartroundclinic.doctor.domain.repository.DoctorChatRepository

/** "Connect"/"Refer to this doctor"-adjacent action — finds or creates the thread with another doctor. Used by the Services tab and the Other Doctors search. */
class InitiateDoctorChatUseCase(private val repository: DoctorChatRepository) {
    suspend operator fun invoke(otherDoctorId: String) = repository.initiateChat(otherDoctorId)
}

package ke.co.smartroundclinic.doctor.domain.model

data class CallJoinInfo(
    val meetingId: String,
    val participantId: String,
    val authToken: String,
    val presetName: String,
)

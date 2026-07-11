package ke.co.smartroundclinic.doctor.domain.model

/** One page of merged conversation history. */
data class MergedHistoryPage(
    val items: List<ConsultationMessage>,
    val nextCursor: String?,
)

package ke.co.smartroundclinic.doctor.domain.model

data class RatingPage(
    val items: List<Rating>,
    val total: Int,
    val page: Int,
    val size: Int,
)

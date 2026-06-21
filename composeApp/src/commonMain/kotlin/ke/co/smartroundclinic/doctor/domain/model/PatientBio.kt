package ke.co.smartroundclinic.doctor.domain.model

data class PatientBio(
    val weight: Double?,
    val weightIn: String?,
    val height: Double?,
    val heightIn: String?,
    val bloodGroup: String?,
    val maritalStatus: String?,
    val allergies: List<String>,
    val chronicConditions: List<String>,
    val currentMedications: List<String>,
)

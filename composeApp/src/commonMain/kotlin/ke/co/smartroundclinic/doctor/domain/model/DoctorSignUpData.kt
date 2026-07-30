package ke.co.smartroundclinic.doctor.domain.model

data class DoctorSignUpData(
    val fullName: String,
    val email: String,
    val password: String,
    val specializationId: String,
    val licenceName: String,
    val licenceFileName: String,
    val licenceFileMimeType: String,
    val licenceFile: ByteArray,
    val bankName: String,
    val bankCode: String,
    val branchCode: String,
    val branchName: String,
    val accountName: String,
    val accountNumber: String,
    val profilePicture: ByteArray,
    val kmpdcRegNumber: String? = null,
    val title: String? = null,
    val bio: String? = null,
    val yearsOfExperience: Int? = null,
    val languages: List<String> = emptyList(),
    val facilityName: String? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as DoctorSignUpData
        return fullName == other.fullName &&
            email == other.email &&
            password == other.password &&
            specializationId == other.specializationId &&
            licenceName == other.licenceName &&
            licenceFileName == other.licenceFileName &&
            licenceFileMimeType == other.licenceFileMimeType &&
            licenceFile.contentEquals(other.licenceFile) &&
            bankName == other.bankName &&
            bankCode == other.bankCode &&
            branchCode == other.branchCode &&
            branchName == other.branchName &&
            accountName == other.accountName &&
            accountNumber == other.accountNumber &&
            profilePicture.contentEquals(other.profilePicture) &&
            kmpdcRegNumber == other.kmpdcRegNumber &&
            title == other.title &&
            bio == other.bio &&
            yearsOfExperience == other.yearsOfExperience &&
            languages == other.languages &&
            facilityName == other.facilityName
    }

    override fun hashCode(): Int {
        var result = fullName.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + password.hashCode()
        result = 31 * result + specializationId.hashCode()
        result = 31 * result + licenceName.hashCode()
        result = 31 * result + licenceFileName.hashCode()
        result = 31 * result + licenceFileMimeType.hashCode()
        result = 31 * result + licenceFile.contentHashCode()
        result = 31 * result + bankName.hashCode()
        result = 31 * result + bankCode.hashCode()
        result = 31 * result + branchCode.hashCode()
        result = 31 * result + branchName.hashCode()
        result = 31 * result + accountName.hashCode()
        result = 31 * result + accountNumber.hashCode()
        result = 31 * result + profilePicture.contentHashCode()
        result = 31 * result + (kmpdcRegNumber?.hashCode() ?: 0)
        result = 31 * result + (title?.hashCode() ?: 0)
        result = 31 * result + (bio?.hashCode() ?: 0)
        result = 31 * result + (yearsOfExperience ?: 0)
        result = 31 * result + languages.hashCode()
        result = 31 * result + (facilityName?.hashCode() ?: 0)
        return result
    }
}

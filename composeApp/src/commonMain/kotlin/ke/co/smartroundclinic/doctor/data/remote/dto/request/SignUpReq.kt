package ke.co.smartroundclinic.doctor.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class SignUpReq(
    val fullName:String,
    val email: String,
    val password: String,
    val specializationId : String,
    val licenceName: String,
    val licenceFile: ByteArray,
    val bankName: String,
    val branchCode: String,
    val accountName: String,
    val accountNumber: String,
    val gender:String,
    val kraPin: String,
    val profilePicture: ByteArray? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as SignUpReq

        if (fullName != other.fullName) return false
        if (email != other.email) return false
        if (password != other.password) return false
        if (specializationId != other.specializationId) return false
        if (licenceName != other.licenceName) return false
        if (!licenceFile.contentEquals(other.licenceFile)) return false
        if (bankName != other.bankName) return false
        if (branchCode != other.branchCode) return false
        if (accountName != other.accountName) return false
        if (accountNumber != other.accountNumber) return false
        if (gender != other.gender) return false
        if (kraPin != other.kraPin) return false
        if (!profilePicture.contentEquals(other.profilePicture)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = fullName.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + password.hashCode()
        result = 31 * result + specializationId.hashCode()
        result = 31 * result + licenceName.hashCode()
        result = 31 * result + licenceFile.hashCode()
        result = 31 * result + bankName.hashCode()
        result = 31 * result + branchCode.hashCode()
        result = 31 * result + accountName.hashCode()
        result = 31 * result + accountNumber.hashCode()
        result = 31 * result + gender.hashCode()
        result = 31 * result + kraPin.hashCode()
        result = 31 * result + (profilePicture?.contentHashCode() ?: 0)
        return result
    }
}

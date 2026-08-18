package ke.co.smartroundclinic.doctor.presentation.main.bookings.destinations

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object BookingList : NavKey

@Serializable
data class BookingDetail(val bookingId: String) : NavKey

@Serializable
data class MedicalRecordDetail(
    val appointmentId: String,
    val consultationId: String?,
    val patientId: String,
) : NavKey

@Serializable
data class ReferralReason(val appointmentId: String) : NavKey

@Serializable
data class ReferralDoctorPicker(val appointmentId: String, val reason: String) : NavKey

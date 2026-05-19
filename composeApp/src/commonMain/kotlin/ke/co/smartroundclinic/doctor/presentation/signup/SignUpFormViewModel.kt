package ke.co.smartroundclinic.doctor.presentation.signup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import ke.co.smartroundclinic.doctor.domain.model.Bank
import ke.co.smartroundclinic.doctor.domain.model.BankBranch

// Holds all sign-up form state across all three steps so navigation back/forward
// doesn't lose filled fields. Never touches SavedState / Bundle.
// Scoped to SignUpRoot — created once before the NavDisplay, passed down explicitly.
class SignUpFormViewModel : ViewModel() {

    // ── Step 1 — Personal Info ──────────────────────────────────────────────────
    var fullName by mutableStateOf("")
    var gender by mutableStateOf("")
    var dob by mutableStateOf("")
    var email by mutableStateOf("")
    var phoneNumber by mutableStateOf("")
    var countryDialCode by mutableStateOf("+254")
    var countryFlag by mutableStateOf("🇰🇪")
    var countryName by mutableStateOf("Kenya")
    var kraPin by mutableStateOf("")
    var password by mutableStateOf("")

    // ── Step 2 — Specialization & Compliance ───────────────────────────────────
    var selectedSpecialityId by mutableStateOf("")
    var selectedSpecialityName by mutableStateOf("")

    // ── Step 3 — Bank Details ───────────────────────────────────────────────────
    var bankQuery by mutableStateOf("")
    var selectedBank by mutableStateOf<Bank?>(null)
    var branchQuery by mutableStateOf("")
    var selectedBranch by mutableStateOf<BankBranch?>(null)
    var accountName by mutableStateOf("")
    var accountNumber by mutableStateOf("")
    var agreedToTerms by mutableStateOf(false)
}

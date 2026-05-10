package ke.co.smartroundclinic.doctor.koin

import ke.co.smartroundclinic.doctor.domain.usecase.auth.DeleteProfilePictureUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.auth.SignOutUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.auth.GetUserUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.auth.RefreshTokenUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.auth.RequestPasswordResetUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.auth.ResendAccountUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.auth.ResendPasswordResetOtpUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.auth.SignInUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.auth.SignUpUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.auth.UpdatePasswordUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.auth.UpdatePersonalInfoUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.auth.UploadProfilePictureUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.auth.VerifyAccountUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.bank.AddPaymentDetailsUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.bank.GetLocalBanksUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.bank.GetPaymentDetailsUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.bank.SearchBanksUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.bank.UpdatePaymentDetailsUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.datastore.GetKeyUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.datastore.ObserveKeyUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.datastore.SetKeyUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.scheduling.CancelAppointmentUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.scheduling.CompleteAppointmentUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.scheduling.ConfirmAppointmentUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.scheduling.GetAppointmentsUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.scheduling.GetScheduleUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.scheduling.NoShowAppointmentUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.scheduling.UpdateAvailabilityUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.scheduling.UpsertAvailabilityUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.speciality.AddDoctorSpecializationUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.speciality.GetDoctorSpecializationUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.speciality.GetSpecialitiesUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.speciality.UpdateDoctorSpecializationUseCase
import ke.co.smartroundclinic.doctor.presentation.auth.ForgotPasswordViewModel
import ke.co.smartroundclinic.doctor.presentation.main.bookings.BookingsViewModel
import ke.co.smartroundclinic.doctor.presentation.main.profile.ScheduleViewModel
import ke.co.smartroundclinic.doctor.presentation.auth.SignInViewModel
import ke.co.smartroundclinic.doctor.presentation.main.profile.PaymentDetailsViewModel
import ke.co.smartroundclinic.doctor.presentation.main.profile.PersonalInfoViewModel
import ke.co.smartroundclinic.doctor.presentation.main.profile.SpecializationViewModel
import ke.co.smartroundclinic.doctor.presentation.splash.SplashViewModel
import ke.co.smartroundclinic.doctor.presentation.onboarding.OnboardingScreenViewModel
import ke.co.smartroundclinic.doctor.presentation.signup.AccountVerificationViewModel
import ke.co.smartroundclinic.doctor.presentation.signup.BankDetailsViewModel
import ke.co.smartroundclinic.doctor.presentation.signup.SignUpFilesViewModel
import ke.co.smartroundclinic.doctor.presentation.signup.SpecializationComplianceViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val useCaseModule = module {
    single { ObserveKeyUseCase(get()) }
    single { GetKeyUseCase(get()) }
    single { SetKeyUseCase(get()) }
    single { GetLocalBanksUseCase(get(), get()) }
    single { SearchBanksUseCase(get(), get()) }
    single { GetPaymentDetailsUseCase(get(), get()) }
    single { UpdatePaymentDetailsUseCase(get(), get()) }
    single { AddPaymentDetailsUseCase(get()) }
    single { GetSpecialitiesUseCase(get(), get()) }
    single { GetDoctorSpecializationUseCase(get(), get()) }
    single { AddDoctorSpecializationUseCase(get()) }
    single { UpdateDoctorSpecializationUseCase(get()) }
    single { SignUpUseCase(get()) }
    single { VerifyAccountUseCase(get()) }
    single { ResendAccountUseCase(get()) }
    single { SignInUseCase(get(), get()) }
    single { RefreshTokenUseCase(get(), get()) }
    single { RequestPasswordResetUseCase(get()) }
    single { ResendPasswordResetOtpUseCase(get()) }
    single { UpdatePasswordUseCase(get()) }
    single { UpdatePersonalInfoUseCase(get()) }
    single { UploadProfilePictureUseCase(get()) }
    single { DeleteProfilePictureUseCase(get()) }
    single { GetUserUseCase(get(), get()) }
    single { SignOutUseCase(get(), get(), get(), get(), get(), get()) }
    single { GetScheduleUseCase(get(), get()) }
    single { UpsertAvailabilityUseCase(get(), get()) }
    single { UpdateAvailabilityUseCase(get(), get()) }
    single { GetAppointmentsUseCase(get(), get()) }
    single { ConfirmAppointmentUseCase(get(), get()) }
    single { CompleteAppointmentUseCase(get(), get()) }
    single { NoShowAppointmentUseCase(get(), get()) }
    single { CancelAppointmentUseCase(get(), get()) }

    viewModel { SplashViewModel(get(), get()) }
    viewModel { OnboardingScreenViewModel(get(), get()) }
    viewModel { SpecializationComplianceViewModel(get()) }
    viewModel { BankDetailsViewModel(get(), get(), get()) }
    viewModel { SignUpFilesViewModel() }
    viewModel { AccountVerificationViewModel(get(), get(), get()) }
    viewModel { SignInViewModel(get(), get()) }
    viewModel { ForgotPasswordViewModel(get(), get(), get(), get()) }
    viewModel { PersonalInfoViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { PaymentDetailsViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { SpecializationViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { ScheduleViewModel(get(), get(), get(), get(), get()) }
    viewModel { BookingsViewModel(get(), get(), get(), get(), get(), get(), get()) }
}

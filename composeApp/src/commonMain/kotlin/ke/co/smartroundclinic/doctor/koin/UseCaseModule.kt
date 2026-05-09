package ke.co.smartroundclinic.doctor.koin

import ke.co.smartroundclinic.doctor.domain.usecase.auth.RefreshTokenUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.auth.RequestPasswordResetUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.auth.ResendAccountUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.auth.ResendPasswordResetOtpUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.auth.SignInUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.auth.SignUpUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.auth.UpdatePasswordUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.auth.VerifyAccountUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.bank.GetLocalBanksUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.bank.SearchBanksUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.datastore.GetKeyUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.datastore.ObserveKeyUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.datastore.SetKeyUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.speciality.GetSpecialitiesUseCase
import ke.co.smartroundclinic.doctor.presentation.auth.ForgotPasswordViewModel
import ke.co.smartroundclinic.doctor.presentation.auth.SignInViewModel
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
    single { GetSpecialitiesUseCase(get(), get()) }
    single { SignUpUseCase(get()) }
    single { VerifyAccountUseCase(get()) }
    single { ResendAccountUseCase(get()) }
    single { SignInUseCase(get(), get()) }
    single { RefreshTokenUseCase(get(), get()) }
    single { RequestPasswordResetUseCase(get()) }
    single { ResendPasswordResetOtpUseCase(get()) }
    single { UpdatePasswordUseCase(get()) }

    viewModel { SplashViewModel(get(), get()) }
    viewModel { OnboardingScreenViewModel(get(), get()) }
    viewModel { SpecializationComplianceViewModel(get()) }
    viewModel { BankDetailsViewModel(get(), get(), get()) }
    viewModel { SignUpFilesViewModel() }
    viewModel { AccountVerificationViewModel(get(), get(), get()) }
    viewModel { SignInViewModel(get(), get()) }
    viewModel { ForgotPasswordViewModel(get(), get(), get(), get()) }
}

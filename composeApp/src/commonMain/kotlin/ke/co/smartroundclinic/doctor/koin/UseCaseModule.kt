package ke.co.smartroundclinic.doctor.koin

import ke.co.smartroundclinic.doctor.domain.usecase.profile.GetDoctorProfileUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.profile.UpdateDoctorProfileUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.PreloadDashboardUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.notification.GetMyNotificationsUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.wallet.GetWalletTransactionsUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.wallet.GetPaymentSummaryUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.wallet.GetWithdrawalBalanceUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.support.CreateSupportTicketUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.support.GetIssueCategoriesUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.support.GetMyTicketsUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.support.GetSupportChatHistoryUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.support.UploadChatFileUseCase
import ke.co.smartroundclinic.doctor.presentation.main.support.SupportChatViewModel
import ke.co.smartroundclinic.doctor.presentation.main.support.SupportViewModel
import ke.co.smartroundclinic.doctor.domain.usecase.wallet.GetWithdrawalByIdUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.wallet.GetWithdrawalHistoryUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.wallet.WithdrawUseCase
import ke.co.smartroundclinic.doctor.presentation.main.wallet.WalletViewModel
import ke.co.smartroundclinic.doctor.domain.usecase.notification.MarkNotificationReadUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.notification.RegisterDeviceTokenUseCase
import ke.co.smartroundclinic.doctor.presentation.main.notifications.NotificationsViewModel
import ke.co.smartroundclinic.doctor.domain.usecase.consultation.DeleteConversationThreadUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.consultation.GetMergedConsultationHistoryUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.consultation.CancelCallUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.consultation.DeclineCallUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.consultation.InviteToCallUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.consultation.JoinConsultationCallUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.consultation.ListConversationThreadsUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.medicalrecord.GetMedicalRecordUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.medicalrecord.GetPatientBioUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.medicalrecord.GetPatientMedicalHistoryUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.medicalrecord.SaveMedicalRecordUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.rating.DeletePatientRatingUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.rating.GetPatientRatingForAppointmentUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.rating.GetPatientRatingHistoryUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.rating.RatePatientUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.rating.UpdatePatientRatingUseCase
import ke.co.smartroundclinic.doctor.presentation.main.bookings.MedicalRecordViewModel
import ke.co.smartroundclinic.doctor.presentation.main.bookings.RatingViewModel
import ke.co.smartroundclinic.doctor.presentation.main.bookings.ReferralViewModel
import ke.co.smartroundclinic.doctor.presentation.main.chat.ConsultationViewModel
import ke.co.smartroundclinic.doctor.domain.usecase.articles.CreateArticleUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.licence.DeleteLicenceUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.licence.GetAllLicencesUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.licence.UploadLicenceUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.articles.DeleteArticleUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.articles.GetCategoriesUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.articles.GetLiveArticlesUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.articles.GetMyArticlesUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.articles.PublishArticleUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.articles.UnpublishArticleUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.articles.UpdateArticleUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.auth.ConfirmComplianceCorrectionUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.auth.DeleteProfilePictureUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.auth.GetComplianceStatusUseCase
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
import ke.co.smartroundclinic.doctor.domain.usecase.scheduling.DeactivateScheduleUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.scheduling.CompleteAppointmentUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.scheduling.ConfirmAppointmentUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.scheduling.GetAppointmentsUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.scheduling.GetNextAppointmentUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.scheduling.GetScheduleUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.scheduling.UpdateAvailabilityUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.scheduling.UpsertAvailabilityUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.speciality.AddDoctorSpecializationUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.speciality.GetDoctorSpecializationUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.speciality.GetSpecialitiesUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.speciality.UpdateDoctorSpecializationUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.directory.GetRecommendedDoctorsUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.referral.CheckReferralEligibilityUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.referral.CreateReferralUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.doctorchat.InitiateDoctorChatUseCase
import ke.co.smartroundclinic.doctor.presentation.main.chat.otherdoctors.DoctorChatViewModel
import ke.co.smartroundclinic.doctor.presentation.main.services.ServicesViewModel
import ke.co.smartroundclinic.doctor.presentation.auth.ForgotPasswordViewModel
import ke.co.smartroundclinic.doctor.presentation.main.ComplianceViewModel
import ke.co.smartroundclinic.doctor.presentation.main.articles.ArticlesViewModel
import ke.co.smartroundclinic.doctor.presentation.main.bookings.BookingsViewModel
import ke.co.smartroundclinic.doctor.presentation.main.profile.LicenceViewModel
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
import ke.co.smartroundclinic.doctor.presentation.signup.SignUpFormViewModel
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
    single { GetRecommendedDoctorsUseCase(get()) }
    single { CheckReferralEligibilityUseCase(get()) }
    single { CreateReferralUseCase(get()) }
    single { InitiateDoctorChatUseCase(get()) }
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
    single { GetComplianceStatusUseCase(get()) }
    single { ConfirmComplianceCorrectionUseCase(get()) }
    single { SignOutUseCase(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    single { PreloadDashboardUseCase(get(), get(), get(), get(), get()) }
    single { GetScheduleUseCase(get(), get()) }
    single { UpsertAvailabilityUseCase(get(), get()) }
    single { UpdateAvailabilityUseCase(get(), get()) }
    single { GetAppointmentsUseCase(get(), get()) }
    single { GetNextAppointmentUseCase(get()) }
    single { ConfirmAppointmentUseCase(get()) }
    single { CompleteAppointmentUseCase(get()) }
    single { CancelAppointmentUseCase(get()) }
    single { DeactivateScheduleUseCase(get(), get()) }
    single { GetCategoriesUseCase(get(), get()) }
    single { GetMyArticlesUseCase(get(), get()) }
    single { GetLiveArticlesUseCase(get(), get()) }
    single { CreateArticleUseCase(get(), get()) }
    single { UpdateArticleUseCase(get(), get()) }
    single { PublishArticleUseCase(get(), get()) }
    single { UnpublishArticleUseCase(get(), get()) }
    single { DeleteArticleUseCase(get(), get()) }
    single { GetAllLicencesUseCase(get()) }
    single { UploadLicenceUseCase(get()) }
    single { DeleteLicenceUseCase(get()) }

    single { RegisterDeviceTokenUseCase(get()) }
    single { GetMyNotificationsUseCase(get()) }
    single { MarkNotificationReadUseCase(get()) }
    single { GetWalletTransactionsUseCase(get()) }
    single { GetPaymentSummaryUseCase(get()) }
    single { GetWithdrawalBalanceUseCase(get()) }
    single { GetWithdrawalHistoryUseCase(get()) }
    single { GetWithdrawalByIdUseCase(get()) }
    single { WithdrawUseCase(get()) }
    single { GetDoctorProfileUseCase(get()) }
    single { UpdateDoctorProfileUseCase(get()) }

    // Consultation use cases
    single { JoinConsultationCallUseCase(get()) }
    single { InviteToCallUseCase(get()) }
    single { DeclineCallUseCase(get()) }
    single { CancelCallUseCase(get()) }
    single { ListConversationThreadsUseCase(get()) }
    single { GetMergedConsultationHistoryUseCase(get()) }
    single { DeleteConversationThreadUseCase(get()) }

    // Medical record use cases
    single { SaveMedicalRecordUseCase(get()) }
    single { GetMedicalRecordUseCase(get()) }
    single { GetPatientMedicalHistoryUseCase(get()) }
    single { GetPatientBioUseCase(get()) }

    // Rating use cases
    single { RatePatientUseCase(get()) }
    single { UpdatePatientRatingUseCase(get()) }
    single { DeletePatientRatingUseCase(get()) }
    single { GetPatientRatingForAppointmentUseCase(get()) }
    single { GetPatientRatingHistoryUseCase(get()) }

    viewModel { SplashViewModel(get(), get(), get()) }
    viewModel { OnboardingScreenViewModel(get(), get()) }
    viewModel { SpecializationComplianceViewModel(get()) }
    viewModel { BankDetailsViewModel(get(), get(), get()) }
    viewModel { SignUpFilesViewModel() }
    viewModel { SignUpFormViewModel() }
    viewModel { AccountVerificationViewModel(get(), get(), get()) }
    viewModel { SignInViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { ForgotPasswordViewModel(get(), get(), get(), get()) }
    viewModel { PersonalInfoViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { PaymentDetailsViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { SpecializationViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { ScheduleViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { ComplianceViewModel(get(), get(), get(), get(), get()) }
    viewModel { BookingsViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { ArticlesViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { LicenceViewModel(get(), get(), get(), get()) }
    viewModel { ConsultationViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { MedicalRecordViewModel(get(), get(), get(), get(), get()) }
    viewModel { RatingViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { ReferralViewModel(get(), get()) }
    viewModel { NotificationsViewModel(get(), get()) }
    viewModel { WalletViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { ServicesViewModel(get(), get()) }
    viewModel { DoctorChatViewModel(get(), get(), get(), get(), get(), get()) }

    // Support
    single { GetIssueCategoriesUseCase(get()) }
    single { CreateSupportTicketUseCase(get()) }
    single { GetMyTicketsUseCase(get()) }
    single { UploadChatFileUseCase(get()) }
    single { GetSupportChatHistoryUseCase(get()) }
    viewModel { SupportViewModel(get(), get(), get(), get()) }
    viewModel { (ticketId: String) -> SupportChatViewModel(ticketId, get(), get(), get(), get(), get()) }
}

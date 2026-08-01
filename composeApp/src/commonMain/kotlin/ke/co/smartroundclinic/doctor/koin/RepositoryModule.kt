package ke.co.smartroundclinic.doctor.koin

import ke.co.smartroundclinic.doctor.core.database.AppDatabase
import ke.co.smartroundclinic.doctor.data.repository.AppointmentLocalRepositoryImpl
import ke.co.smartroundclinic.doctor.data.repository.ConsultationRepositoryImpl
import ke.co.smartroundclinic.doctor.data.repository.DoctorProfileRepositoryImpl
import ke.co.smartroundclinic.doctor.data.repository.MedicalRecordRepositoryImpl
import ke.co.smartroundclinic.doctor.data.repository.NotificationRepositoryImpl
import ke.co.smartroundclinic.doctor.data.repository.SupportRepositoryImpl
import ke.co.smartroundclinic.doctor.data.repository.WalletRepositoryImpl
import ke.co.smartroundclinic.doctor.data.repository.ArticleCategoryLocalRepositoryImpl
import ke.co.smartroundclinic.doctor.data.repository.ArticleLocalRepositoryImpl
import ke.co.smartroundclinic.doctor.data.repository.ArticleRepositoryImpl
import ke.co.smartroundclinic.doctor.data.repository.LicenceRepositoryImpl
import ke.co.smartroundclinic.doctor.data.repository.AuthRepositoryImpl
import ke.co.smartroundclinic.doctor.data.repository.BankLocalRepositoryImpl
import ke.co.smartroundclinic.doctor.data.repository.BankRepositoryImpl
import ke.co.smartroundclinic.doctor.data.repository.AppointmentRepositoryImpl
import ke.co.smartroundclinic.doctor.data.repository.ScheduleLocalRepositoryImpl
import ke.co.smartroundclinic.doctor.data.repository.SchedulingRepositoryImpl
import ke.co.smartroundclinic.doctor.data.repository.DatastoreRepositoryImpl
import ke.co.smartroundclinic.doctor.data.repository.DoctorSpecializationLocalRepositoryImpl
import ke.co.smartroundclinic.doctor.data.repository.SpecialityLocalRepositoryImpl
import ke.co.smartroundclinic.doctor.data.repository.SpecialityRepositoryImpl
import ke.co.smartroundclinic.doctor.data.repository.PaymentDetailsLocalRepositoryImpl
import ke.co.smartroundclinic.doctor.data.repository.UserLocalRepositoryImpl
import ke.co.smartroundclinic.doctor.domain.repository.AppointmentLocalRepository
import ke.co.smartroundclinic.doctor.domain.repository.ConsultationRepository
import ke.co.smartroundclinic.doctor.domain.repository.DoctorProfileRepository
import ke.co.smartroundclinic.doctor.domain.repository.MedicalRecordRepository
import ke.co.smartroundclinic.doctor.domain.repository.NotificationRepository
import ke.co.smartroundclinic.doctor.domain.repository.SupportRepository
import ke.co.smartroundclinic.doctor.domain.repository.WalletRepository
import ke.co.smartroundclinic.doctor.domain.repository.AppointmentRepository
import ke.co.smartroundclinic.doctor.domain.repository.ArticleCategoryLocalRepository
import ke.co.smartroundclinic.doctor.domain.repository.ArticleLocalRepository
import ke.co.smartroundclinic.doctor.domain.repository.ArticleRepository
import ke.co.smartroundclinic.doctor.domain.repository.LicenceRepository
import ke.co.smartroundclinic.doctor.domain.repository.AuthRepository
import ke.co.smartroundclinic.doctor.domain.repository.BankLocalRepository
import ke.co.smartroundclinic.doctor.domain.repository.BankRepository
import ke.co.smartroundclinic.doctor.domain.repository.ScheduleLocalRepository
import ke.co.smartroundclinic.doctor.domain.repository.SchedulingRepository
import ke.co.smartroundclinic.doctor.domain.repository.DatastoreRepository
import ke.co.smartroundclinic.doctor.domain.repository.DoctorSpecializationLocalRepository
import ke.co.smartroundclinic.doctor.domain.repository.PaymentDetailsLocalRepository
import ke.co.smartroundclinic.doctor.domain.repository.SpecialityLocalRepository
import ke.co.smartroundclinic.doctor.domain.repository.SpecialityRepository
import ke.co.smartroundclinic.doctor.domain.repository.UserLocalRepository
import ke.co.smartroundclinic.doctor.data.repository.PatientBioRepositoryImpl
import ke.co.smartroundclinic.doctor.domain.repository.PatientBioRepository
import ke.co.smartroundclinic.doctor.data.repository.PatientRatingRepositoryImpl
import ke.co.smartroundclinic.doctor.domain.repository.PatientRatingRepository
import ke.co.smartroundclinic.doctor.data.repository.DoctorDirectoryRepositoryImpl
import ke.co.smartroundclinic.doctor.domain.repository.DoctorDirectoryRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<DatastoreRepository> { DatastoreRepositoryImpl(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<BankRepository> { BankRepositoryImpl(get()) }
    single<BankLocalRepository> { BankLocalRepositoryImpl(get<AppDatabase>().bankDao) }
    single<SpecialityRepository> { SpecialityRepositoryImpl(get()) }
    single<SpecialityLocalRepository> { SpecialityLocalRepositoryImpl(get<AppDatabase>().specialityDao) }
    single<UserLocalRepository> { UserLocalRepositoryImpl(get<AppDatabase>().userDao) }
    single<PaymentDetailsLocalRepository> { PaymentDetailsLocalRepositoryImpl(get<AppDatabase>().paymentDetailsDao) }
    single<DoctorSpecializationLocalRepository> { DoctorSpecializationLocalRepositoryImpl(get<AppDatabase>().doctorSpecializationDao) }
    single<SchedulingRepository> { SchedulingRepositoryImpl(get()) }
    single<AppointmentRepository> { AppointmentRepositoryImpl(get()) }
    single<ScheduleLocalRepository> { ScheduleLocalRepositoryImpl(get<AppDatabase>().doctorAvailabilityDao) }
    single<AppointmentLocalRepository> { AppointmentLocalRepositoryImpl(get<AppDatabase>().appointmentDao) }
    single<ArticleRepository> { ArticleRepositoryImpl(get()) }
    single<ArticleLocalRepository> { ArticleLocalRepositoryImpl(get<AppDatabase>().articleDao) }
    single<ArticleCategoryLocalRepository> { ArticleCategoryLocalRepositoryImpl(get<AppDatabase>().articleCategoryDao) }
    single<LicenceRepository> { LicenceRepositoryImpl(get()) }
    single<ConsultationRepository> { ConsultationRepositoryImpl(get()) }
    single<NotificationRepository> { NotificationRepositoryImpl(get()) }
    single<WalletRepository> { WalletRepositoryImpl(get()) }
    single<SupportRepository> { SupportRepositoryImpl(get()) }
    single<DoctorProfileRepository> { DoctorProfileRepositoryImpl(get()) }
    single<MedicalRecordRepository> { MedicalRecordRepositoryImpl(get()) }
    single<PatientBioRepository> { PatientBioRepositoryImpl(get()) }
    single<PatientRatingRepository> { PatientRatingRepositoryImpl(get()) }
    single<DoctorDirectoryRepository> { DoctorDirectoryRepositoryImpl(get()) }
}

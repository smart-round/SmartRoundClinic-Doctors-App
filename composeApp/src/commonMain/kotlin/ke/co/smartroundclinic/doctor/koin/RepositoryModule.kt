package ke.co.smartroundclinic.doctor.koin

import ke.co.smartroundclinic.doctor.core.database.AppDatabase
import ke.co.smartroundclinic.doctor.data.repository.AppointmentLocalRepositoryImpl
import ke.co.smartroundclinic.doctor.data.repository.ConsultationRepositoryImpl
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
}

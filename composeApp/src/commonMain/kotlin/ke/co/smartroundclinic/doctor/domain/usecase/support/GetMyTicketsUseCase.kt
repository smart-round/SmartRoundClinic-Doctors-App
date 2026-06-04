package ke.co.smartroundclinic.doctor.domain.usecase.support

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.SupportTicket
import ke.co.smartroundclinic.doctor.domain.repository.SupportRepository

class GetMyTicketsUseCase(private val repository: SupportRepository) {
    suspend operator fun invoke(): Resource<List<SupportTicket>> = repository.getMyTickets()
}

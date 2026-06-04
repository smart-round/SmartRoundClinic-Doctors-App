package ke.co.smartroundclinic.doctor.domain.usecase.support

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.IssueCategory
import ke.co.smartroundclinic.doctor.domain.repository.SupportRepository

class GetIssueCategoriesUseCase(private val repository: SupportRepository) {
    suspend operator fun invoke(): Resource<List<IssueCategory>> = repository.getCategories()
}

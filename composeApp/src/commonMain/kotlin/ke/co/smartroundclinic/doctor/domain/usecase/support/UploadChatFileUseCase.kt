package ke.co.smartroundclinic.doctor.domain.usecase.support

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.SupportChatMessage
import ke.co.smartroundclinic.doctor.domain.repository.SupportRepository

class UploadChatFileUseCase(private val repository: SupportRepository) {
    suspend operator fun invoke(
        ticketId: String,
        bytes: ByteArray,
        fileName: String,
        contentType: String,
    ): Resource<SupportChatMessage> = repository.uploadChatFile(ticketId, bytes, fileName, contentType)
}

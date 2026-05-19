package ke.co.smartroundclinic.doctor.domain.repository

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.response.GetAllMyLicencesRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.GetMyLicenceByIdRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.SuccessRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.UploadLicenceRes


interface LicenceRepository {
    suspend fun uploadLicence(licenceName: String, licenceFileName: String, licence: ByteArray): Resource<UploadLicenceRes>
    suspend fun getAllMyLicences(): Resource<GetAllMyLicencesRes>
    suspend fun getMyLicenceById(id: String): Resource<GetMyLicenceByIdRes>
    suspend fun deleteMyLicence(idString: String): Resource<SuccessRes>
}
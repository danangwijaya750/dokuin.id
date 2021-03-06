package id.infiniteuny.dokuin.data.repository

import id.infiniteuny.dokuin.data.model.SendEmailOTP
import id.infiniteuny.dokuin.data.service.WebService
import id.infiniteuny.dokuin.data.service.WebServiceBigBox


class UserRepository(private val service: WebService) {
    suspend fun getUser(id:String) = service.getUser(id)
    suspend fun getKey()=service.getKey()
    suspend fun loginUser(email:String,password:String) = service.loginUser(email,password)

}
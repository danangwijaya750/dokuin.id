package id.infiniteuny.dokuin.ui.upload_file

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import id.infiniteuny.dokuin.base.BasePresenter
import id.infiniteuny.dokuin.data.model.ResponseModel
import id.infiniteuny.dokuin.data.repository.UploadRepository
import id.infiniteuny.dokuin.util.logE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.*

class UploadFilePresenter(private val repository: UploadRepository,private val view: UploadFileView): BasePresenter() {

    fun doUploadFile(signator1:String,signator2:String,email:String,filename:String,file: File,role:String){
        view.onLoading(true)
        val fileReqBody = RequestBody.create(MediaType.parse("application/pdf"),file)
        val formData= MultipartBody.Part.createFormData("file", filename, fileReqBody)
        launch {
            try {
                val result = withContext(Dispatchers.IO){ repository.uploadFile(signator1,signator2, email, filename, formData)}
                //uploadFirestore(result,filename, email,role)
                    view.showResult(result)
                    view.onLoading(false)
            }catch (throwable:Throwable){
                logE(throwable.localizedMessage)
                view.onError(throwable.localizedMessage)
                view.onLoading(false)
            }
        }
    }
    fun doUploadFile(signator1:String,email:String,filename:String,file: File,role:String){
        view.onLoading(true)
        val fileReqBody = RequestBody.create(MediaType.parse("application/pdf"),file)
        val formData= MultipartBody.Part.createFormData("file", filename, fileReqBody)
        launch {
            try {
                val result = withContext(Dispatchers.IO){ repository.uploadFile(signator1, email, filename, formData)}
                //uploadFirestore(result,filename, email,role)
                view.showResult(result)
                view.onLoading(false)
            }catch (throwable:Throwable){
                logE(throwable.localizedMessage)
                view.onError(throwable.localizedMessage)
                view.onLoading(false)
            }
        }
    }
    fun doUploadFileSignator2(signator2:String,email:String,filename:String,file: File,role:String){
        view.onLoading(true)
        val fileReqBody = RequestBody.create(MediaType.parse("application/pdf"),file)
        val formData= MultipartBody.Part.createFormData("file", filename, fileReqBody)
        launch {
            try {
                val result = withContext(Dispatchers.IO){ repository.uploadFileSignator2(signator2, email, filename, formData)}
                //uploadFirestore(result,filename, email,role)
                view.showResult(result)
                view.onLoading(false)
            }catch (throwable:Throwable){
                logE(throwable.localizedMessage)
                view.onError(throwable.localizedMessage)
                view.onLoading(false)
            }
        }
    }
    private fun uploadFirestore(result:ResponseModel,filename: String,uid: String,role: String){
        val db=FirebaseFirestore.getInstance()
        val data = when(role){
            "student"->{
                hashMapOf(
                    "title" to filename,
                    "studentId" to uid,
                    "schoolId" to "REB6SPS67mZAxGS9kC3gpFjML6n1",
                    "dateUpload" to Timestamp(Date()),
                    "dateApproved" to Timestamp(Date()),
                    "status" to "waiting"
                )
            }
            else->{
                hashMapOf(
                    "title" to filename,
                    "studentId" to "kgRK8fcJ8pWRNRiIjXBp4KIC3cs2",
                    "schoolId" to uid,
                    "dateUpload" to Timestamp(Date()),
                    "dateApproved" to Timestamp(Date()),
                    "status" to "approved"
                )
            }
        }
        db.collection("documents").add(data)
            .addOnSuccessListener {
                if(role=="student") {
                    view.showResult(result)
                    view.onLoading(false)
                }else{
                    doVerify(filename,result)
                }
            }
            .addOnFailureListener {
                logE(it.localizedMessage)
                view.onError(it.localizedMessage)
                view.onLoading(false)
            }
    }
     fun doVerify(filename: String,result: ResponseModel){
        view.onLoading(true)
        launch {
            try {
                val resVerif = withContext(Dispatchers.IO) { repository.generateSignature(filename,"") }
                if(resVerif.status!!){
                    logE("ress ${resVerif.message.toString()}")
                    view.onLoading(false)
                    view.showResult(result)
                }
            }catch (t:Throwable){
                view.onError(t.localizedMessage)
                logE(t.localizedMessage)
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy(){
        cleanUp()
    }

}
interface UploadFileView{
    fun onLoading(state:Boolean)
    fun onError(msg:String)
    fun showResult(data:ResponseModel)
}
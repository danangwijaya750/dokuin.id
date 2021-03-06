package id.infiniteuny.dokuin.ui.student.beranda

import com.google.firebase.firestore.FirebaseFirestore
import id.infiniteuny.dokuin.base.BasePresenter
import id.infiniteuny.dokuin.data.model.Data
import id.infiniteuny.dokuin.data.model.DocumentModel
import id.infiniteuny.dokuin.data.repository.UploadRepository
import id.infiniteuny.dokuin.util.logE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BerandaPresenter(private val repository: UploadRepository,private val view: BerandaView) : BasePresenter() {
    fun getMyDocument(email:String){
        view.onLoading(true)
        launch {
            try {
                val result = withContext(Dispatchers.IO){ repository.getMyFile(email)}
                view.showData(result.data)
                view.onLoading(false)
            }catch (throwable:Throwable){
                logE(throwable.localizedMessage)
                view.onError(throwable.localizedMessage)
                view.onLoading(false)
            }
        }
    }

}

interface BerandaView {
    fun onLoading(state:Boolean)
    fun onError(msg:String)
    fun showResult(data:List<DocumentModel>)
    fun showResultWaiting(data:List<DocumentModel>)
    fun showData(data:List<Data>?)
}


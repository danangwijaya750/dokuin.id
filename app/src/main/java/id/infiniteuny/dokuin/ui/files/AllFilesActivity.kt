package id.infiniteuny.dokuin.ui.files

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import id.infiniteuny.dokuin.R
import id.infiniteuny.dokuin.base.BaseActivity
import id.infiniteuny.dokuin.base.RvAdapter
import id.infiniteuny.dokuin.data.local.SharedPref
import id.infiniteuny.dokuin.data.model.Data
import id.infiniteuny.dokuin.data.model.DocumentModel
import id.infiniteuny.dokuin.ui.detail.DetailFileActivity
import id.infiniteuny.dokuin.util.logE
import id.infiniteuny.dokuin.util.toast
import kotlinx.android.synthetic.main.activity_all_files.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class AllFilesActivity : BaseActivity(R.layout.activity_all_files),AllFilesView {
    private val db=FirebaseFirestore.getInstance()

    private val documentList= mutableListOf<Data>()
    private val presenter by inject<AllFilesPresenter> {
        parametersOf(this)
    }
    private val rvAdapter=object:RvAdapter<Data>(documentList,{
     handleClick(it)
    }){
        override fun layoutId(position: Int, obj: Data): Int = R.layout.item_document_detail

        override fun viewHolder(view: View, viewType: Int): RecyclerView.ViewHolder =AllFilesVH(view)
    }

    override fun viewCreated(savedInstanceState: Bundle?) {

        rv_all_files.apply {
            adapter=rvAdapter
            val layMan=LinearLayoutManager(this@AllFilesActivity)
            layMan.orientation=LinearLayoutManager.VERTICAL
            layoutManager=layMan
        }
        iv_back.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        getPopulateData()
    }

    private fun getPopulateData(){
        when(SharedPref(this).userRole){
            "student"->{
               presenter.getMyFiles(SharedPref(this).userEmail)
            }
            "school"->{
                presenter.getMyFilesSignator(SharedPref(this).userEmail)
            }
            "instansi"->{

            }
        }
    }

    private fun handleClick(data: Data){
            val intent = Intent(this, DetailFileActivity::class.java)
            intent.putExtra("data", data)
            startActivity(intent)
    }

    private fun getStudentFiles(){
        documentList.clear()
        db.collection("documents")
            .whereEqualTo("studentId",FirebaseAuth.getInstance().currentUser!!.uid)
            .get()
            .addOnSuccessListener {
                if(!it.isEmpty){
                    it.forEach {snap->
                        //documentList.add(snap.toObject(DocumentModel::class.java).withId(snap.id))
                    }
                    rvAdapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener {
                logE(it.localizedMessage)
                toast(it.localizedMessage)
            }
    }
    private fun getSchoolFiles(){
        documentList.clear()
        db.collection("documents")
            .whereEqualTo("schoolId",FirebaseAuth.getInstance().currentUser!!.uid)
            .get()
            .addOnSuccessListener {
                if(!it.isEmpty){
                    it.forEach {snap->
                        //documentList.add(snap.toObject(DocumentModel::class.java).withId(snap.id))
                    }
                    rvAdapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener {
                logE(it.localizedMessage)
                toast(it.localizedMessage)
            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onLoading(state: Boolean) {

    }

    override fun onError(msg: String) {

    }

    override fun showData(data: List<Data>?) {
        logE(data?.size.toString())
       if(!data.isNullOrEmpty()){
           documentList.clear()
           documentList.addAll(data)
           rvAdapter.notifyDataSetChanged()
       }
    }


}
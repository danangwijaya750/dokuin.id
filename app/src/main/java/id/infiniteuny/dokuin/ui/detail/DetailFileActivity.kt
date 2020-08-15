package id.infiniteuny.dokuin.ui.detail

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import id.infiniteuny.dokuin.R
import id.infiniteuny.dokuin.base.BaseActivity
import id.infiniteuny.dokuin.data.local.SharedPref
import id.infiniteuny.dokuin.data.model.DocumentModel
import id.infiniteuny.dokuin.data.model.VerifyResponse
import id.infiniteuny.dokuin.data.repository.UploadRepository
import id.infiniteuny.dokuin.util.logE
import id.infiniteuny.dokuin.util.toast
import kotlinx.android.synthetic.main.activity_detail_file.*

class DetailFileActivity : BaseActivity((R.layout.activity_detail_file)),DetailFileView {

    private val presenter=DetailFilePresenter(UploadRepository(),this)
    override fun viewCreated(savedInstanceState: Bundle?) {
        val data=intent.extras?.get("data") as DocumentModel
        showData(data)
        if(SharedPref(this).userRole=="school"){
            btn_approve.visibility= View.VISIBLE
            btn_approve.setOnClickListener {
                presenter.doVerify(data.title)
            }
        }
    }

    private fun showData(data:DocumentModel){
        tv_doc_title.text=data.title
        val url= "https://a64a2e07d0ef.ngrok.io/document?filename=${data.title}&key=${SharedPref(this).key}"
        logE(url)
        val doc= "<iframe src='http://docs.google.com/viewer?url=${url}&embedded=true'width='100%' height='100%'style='border: none;'></iframe>";
        wv_viewer.settings.javaScriptEnabled = true
        wv_viewer.settings.allowFileAccess = true
        wv_viewer.loadUrl(url)
    }

    override fun onStart() {
        super.onStart()
        lifecycle.addObserver(presenter)
    }

    override fun onLoading(state: Boolean) {
        when(state){
            true->pg_loading.visibility=View.VISIBLE
            false->pg_loading.visibility=View.GONE
        }
    }

    override fun onError(msg: String) {
        toast(msg)
    }

    override fun showResult(data: VerifyResponse) {
        toast(data.data!!.status)
    }


}




package id.infiniteuny.dokuin.ui.upload_file

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import id.infiniteuny.dokuin.R
import id.infiniteuny.dokuin.base.BaseActivity
import id.infiniteuny.dokuin.data.local.SharedPref
import id.infiniteuny.dokuin.data.model.ResponseModel
import id.infiniteuny.dokuin.ui.files.AllFilesActivity
import id.infiniteuny.dokuin.util.logE
import id.infiniteuny.dokuin.util.toast
import kotlinx.android.synthetic.main.activity_upload_file.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.io.*


class UploadFileActivity : BaseActivity(R.layout.activity_upload_file), UploadFileView {

    private var path: String? = ""
    private val presenter by inject<UploadFilePresenter> {
        parametersOf(this)
    }
    private val mustApprovedBy = mutableListOf<String>()

    companion object {
        private const val PICK_FILE = 101
        private const val BUFFER_SIZE = 1024 * 2
        private const val DOC_DIRECTORY = "/dokuin_directory"
        private const val GALLERY = 1
    }

    override fun viewCreated(savedInstanceState: Bundle?) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/pdf"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        try {
            startActivityForResult(Intent.createChooser(intent, "Select a File"), PICK_FILE)
        } catch (ex: Exception) {
            logE(ex.localizedMessage)
        }

        btn_upload.setOnClickListener {
            if (path!!.isNotEmpty()&& mustApprovedBy.size>0) {
                doUpload()
            }
        }
        cb_kepsek.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                mustApprovedBy.add("kepsek@stembayo.id")
            }
            else{
                mustApprovedBy.remove("kepsek@stembayo.id")
            }
        }
        cb_kesiswaan.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                mustApprovedBy.add("kesiswaan@stembayo.id")
            }
            else{
                mustApprovedBy.remove("kesiswaan@stembayo.id")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        lifecycle.addObserver(presenter)
    }

    private fun doUpload() {
        val role = SharedPref(this).userRole
        val email= SharedPref(this).userEmail
        var signator1=""
        var signator2=""
        if(cb_kepsek.isChecked){signator1="kepsek@stembayo.id"}
        if(cb_kesiswaan.isChecked){signator2="kesiswaan@stembayo.id"}
        if (et_filename.text.toString().isNotEmpty()) {
            if(signator1.isNotEmpty() && signator2.isNotEmpty()){
                presenter.doUploadFile(
                    signator1,
                    signator2,
                    email,
                    "${et_filename.text}.pdf",
                    File(path),
                    role
                )
            }else if(signator1.isNotEmpty()&&signator2.isEmpty()){
                presenter.doUploadFile(
                    signator1,
                    email,
                    "${et_filename.text}.pdf",
                    File(path),
                    role
                )
            }else if(signator1.isEmpty()&&signator2.isNotEmpty()) {
                presenter.doUploadFileSignator2(
                    signator2,
                    email,
                    "${et_filename.text}.pdf",
                    File(path),
                    role
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PICK_FILE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val uri = data?.data
                    val file = File(uri.toString())
                    logE(uri.toString())
                    path = getFilePathFromURI(this, uri)
                    logE("path is $path")
                    extractFile()
                } else {
                    finish()
                }
            }
        }
    }

    private fun extractFile() {
        logE("File Name : ${path?.substring(path!!.lastIndexOf("/") + 1)}")
    }


    private fun getFilePathFromURI(context: Context, contentUri: Uri?): String? {
        //copy file and send new file path
        val fileName: String? = getFileName(contentUri)
        val documentDirectory = File(getExternalFilesDir(null).toString() + DOC_DIRECTORY)
        // have the object build the directory structure, if needed.
        if (!documentDirectory.exists()) {
            documentDirectory.mkdirs()
        }
        if (!TextUtils.isEmpty(fileName)) {
            val copyFile = File(documentDirectory.toString() + File.separator.toString() + fileName)
            // create folder if not exists
            copy(context, contentUri, copyFile)
            return copyFile.absolutePath
        }
        return null
    }

    private fun getFileName(uri: Uri?): String? {
        if (uri == null) return null
        var fileName: String? = null
        val path = uri.path
        val cut = path!!.lastIndexOf('/')
        if (cut != -1) {
            fileName = path.substring(cut + 1)
        }
        return fileName
    }

    private fun copy(
        context: Context,
        srcUri: Uri?,
        dstFile: File?
    ) {
        try {
            val inputStream = context.contentResolver.openInputStream(srcUri!!)
                ?: return
            val outputStream: OutputStream = FileOutputStream(dstFile!!)
            copystream(inputStream, outputStream)
            inputStream.close()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    @Throws(java.lang.Exception::class, IOException::class)
    private fun copystream(input: InputStream?, output: OutputStream?): Int {
        val buffer = ByteArray(BUFFER_SIZE)
        val `in` = BufferedInputStream(input, BUFFER_SIZE)
        val out = BufferedOutputStream(output, BUFFER_SIZE)
        var count = 0
        var n = 0
        try {
            while (`in`.read(buffer, 0, BUFFER_SIZE).also({ n = it }) != -1) {
                out.write(buffer, 0, n)
                count += n
            }
            out.flush()
        } finally {
            try {
                out.close()
            } catch (e: IOException) {
                logE(e.localizedMessage)
            }
            try {
                `in`.close()
            } catch (e: IOException) {
                logE(e.localizedMessage)
            }
        }
        return count
    }

    override fun onLoading(state: Boolean) {
        when (state) {
            true -> pg_loading.visibility = View.VISIBLE
            false -> pg_loading.visibility = View.GONE
        }
    }

    override fun onError(msg: String) {
        logE(msg)
    }

    override fun showResult(data: ResponseModel) {
        logE(data.message)
        afterResult(data)
    }

    private fun afterResult(data: ResponseModel) {
        toast(data.message)
        startActivity(Intent(this, AllFilesActivity::class.java))
        finish()
    }


}
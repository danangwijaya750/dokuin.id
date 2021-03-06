package id.infiniteuny.dokuin.ui.student.beranda

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import id.infiniteuny.dokuin.base.RvAdapter
import id.infiniteuny.dokuin.data.model.Data
import id.infiniteuny.dokuin.data.model.DocumentModel
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_document_waiting_student.*
import java.text.SimpleDateFormat
import java.util.*


class WaitingDocumentStudentVH (override val containerView: View): RecyclerView.ViewHolder(containerView)
    , LayoutContainer, RvAdapter.Binder<Data>{

    override fun bindData(data: Data, listen: (Data) -> Unit, position: Int) {
        tv_doc_title.text=data.fileTitle
        val date = Date(data.timestamp!!.toLong())
        val format = SimpleDateFormat("dd-MM-yyyy HH:mm")
        tv_doc_date.text=format.format(date)
        itemView.setOnClickListener { listen(data) }
    }
}
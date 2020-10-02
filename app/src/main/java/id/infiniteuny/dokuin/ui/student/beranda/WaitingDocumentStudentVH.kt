package id.infiniteuny.dokuin.ui.student.beranda

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import id.infiniteuny.dokuin.base.RvAdapter
import id.infiniteuny.dokuin.data.model.DocumentModel
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_document_waiting_student.*


class WaitingDocumentStudentVH (override val containerView: View): RecyclerView.ViewHolder(containerView)
    , LayoutContainer, RvAdapter.Binder<DocumentModel>{

    override fun bindData(data: DocumentModel, listen: (DocumentModel) -> Unit, position: Int) {
        tv_doc_title.text=data.title
        tv_doc_date.text=data.dateUpload.toString()
        itemView.setOnClickListener { listen(data) }
    }
}
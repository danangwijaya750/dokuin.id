package id.infiniteuny.dokuin.ui.detail_siswa

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import id.infiniteuny.dokuin.R
import id.infiniteuny.dokuin.base.RvAdapter
import id.infiniteuny.dokuin.data.model.DocumentModel
import id.infiniteuny.dokuin.data.model.UserModel
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_document_detail.*


class StudentDocumentVH (override val containerView: View): RecyclerView.ViewHolder(containerView)
    , LayoutContainer, RvAdapter.Binder<DocumentModel>{

    override fun bindData(data: DocumentModel, listen: (DocumentModel) -> Unit, position: Int) {
        tv_doc_title.text=data.title
        tv_doc_date.text=data.dateUpload.toString()
        textView2.text=data.status.capitalize()
        when(data.status){
            "waiting"->{
                textView2.background= containerView.context.resources.getDrawable(R.drawable.rounded_status_yellow)
            }
            "approved"->{
                textView2.background= containerView.context.resources.getDrawable(R.drawable.rounded_status_green)
            }
            "rejected"->{
                textView2.background= containerView.context.resources.getDrawable(R.drawable.rounded_status_red)
            }
        }

        itemView.setOnClickListener { listen(data) }
    }
}
package id.infiniteuny.dokuin.base

import android.view.LayoutInflater.from
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


abstract class RvAdapter<T>(private val data: List<T>,private val listener:(T)->Unit)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return viewHolder(
            from(parent.context)
                .inflate(viewType, parent, false), viewType
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as Binder<T>).bindData(data[position],listener,position)
    }

    override fun getItemCount() = data.size

    override fun getItemViewType(position: Int): Int {
        return layoutId(position, data[position])
    }

    protected abstract fun layoutId(position: Int, obj: T): Int

    abstract fun viewHolder(view: View, viewType: Int): RecyclerView.ViewHolder

    internal interface Binder<T> {
        fun bindData(data: T,listen:(T)->Unit,position: Int)
    }
}
package dev.codeismail.linkcapture.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.codeismail.linkcapture.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.layout_link_list_item.view.*

class LinkAdapter : ListAdapter<Link, LinkAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<Link>() {

        override fun areItemsTheSame(oldItem: Link, newItem: Link): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Link, newItem: Link): Boolean =
            oldItem == newItem

    }) {

    private var onItemClickListener: ((position: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.layout_link_list_item, parent, false)
        return ViewHolder(itemView, onItemClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setOnItemClickListener(onItemClickListener: (position: Int) -> Unit) {
        this.onItemClickListener = onItemClickListener
    }

    public override fun getItem(position: Int): Link = super.getItem(position)

    class ViewHolder(
        override val containerView: View,
        onItemClickListener: ((position: Int) -> Unit)?
    ) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

        init {
            containerView.openLinkBtn.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener?.invoke(position)
                }
            }
        }

        fun bind(item: Link) {
            item.run {
                 containerView.linkTv.text = item.linkString
            }
        }

    }

}
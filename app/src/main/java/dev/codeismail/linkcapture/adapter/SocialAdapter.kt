package dev.codeismail.linkcapture.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dev.codeismail.linkcapture.R
import dev.codeismail.linkcapture.utils.CustomSocialCheckComponent
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.layout_link_list_item.view.*
import kotlinx.android.synthetic.main.layout_social_item.view.*

class SocialAdapter : RecyclerView.Adapter<SocialAdapter.LinkViewHolder>() {
    private var onItemClickListener: ((position: Int, view: View) -> Unit)? = null
    var data: List<Link> = emptyList()
        set(newList) {
            val calculateDiff = DiffUtil.calculateDiff(LinkDiffCallback(field, newList))
            calculateDiff.dispatchUpdatesTo(this)
            field = newList
        }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkViewHolder {
        return LinkViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_social_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SocialAdapter.LinkViewHolder, position: Int) {
        holder.bind(data[position])
    }
    fun setOnItemClickListener(onItemClickListener: (position: Int, view: View) -> Unit) {
        this.onItemClickListener = onItemClickListener
    }
    inner class LinkViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView),
        LayoutContainer {
        init {
            containerView.twitterComponent.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val checkState = (it as CustomSocialCheckComponent).getCheckState()
                    if (checkState)
                        it.setCheckState(false) else
                        it.setCheckState(true)
                    onItemClickListener?.invoke(position, it)
                }
            }
            containerView.instagramComponent.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val checkState = (it as CustomSocialCheckComponent).getCheckState()
                    if (checkState)
                        it.setCheckState(false) else
                        it.setCheckState(true)
                    onItemClickListener?.invoke(position, it)
                }
            }
            containerView.githubComponent.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val checkState = (it as CustomSocialCheckComponent).getCheckState()
                    if (checkState)
                        it.setCheckState(false) else
                        it.setCheckState(true)
                    onItemClickListener?.invoke(position, it)
                }
            }
        }
        fun bind(item: Link) = with(itemView) {
            userNameTv.text = item.linkString
        }
    }
}

class LinkDiffCallback(val oldList: List<Link>, val newList: List<Link>) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val old = oldList[oldItemPosition]
        val new = newList[newItemPosition]

        return old.id == new.id

    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }
}
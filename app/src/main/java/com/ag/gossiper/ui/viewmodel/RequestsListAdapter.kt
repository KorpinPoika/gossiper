package com.ag.gossiper.ui.viewmodel

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableArrayList
import androidx.recyclerview.widget.RecyclerView
import com.ag.gossiper.databinding.RequestItemBinding
import androidx.databinding.ObservableList.OnListChangedCallback


class RequestsListAdapter(
    private var requests: ObservableArrayList<RequestItem>
) : RecyclerView.Adapter<RequestsListAdapter.RequestItemViewHolder>()
{
    init {
        requests.addOnListChangedCallback(Notifier(this))
    }

    var onItemClickHandler: ((RequestItem) -> Unit)? = null

    fun updateItems(items: ObservableArrayList<RequestItem>?) {
        requests = items ?: ObservableArrayList()
        requests.addOnListChangedCallback(Notifier(this))
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return RequestItemViewHolder(
            RequestItemBinding.inflate(inflater, parent, false).root
        )
    }

    override fun onBindViewHolder(holder: RequestItemViewHolder, position: Int) {
        val selectedItem = requests[position]
        holder.binding!!.request  = selectedItem

        holder.itemView.setOnClickListener { _ ->
            onItemClickHandler?.invoke(selectedItem)
        }
    }

    override fun getItemCount(): Int = requests.size



    inner class RequestItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val binding = DataBindingUtil.bind<RequestItemBinding>(itemView)
    }

    //todo: use different notification, not only notifyDataSetChanged()
    inner class Notifier(private val adapter: RequestsListAdapter): OnListChangedCallback<ObservableArrayList<RequestItem>>(){
        override fun onChanged(sender: ObservableArrayList<RequestItem>?) {
            adapter.notifyDataSetChanged()
        }

        override fun onItemRangeChanged(
            sender: ObservableArrayList<RequestItem>?,
            positionStart: Int,
            itemCount: Int
        ) {
            adapter.notifyDataSetChanged()
        }

        override fun onItemRangeInserted(
            sender: ObservableArrayList<RequestItem>?,
            positionStart: Int,
            itemCount: Int
        ) {
            adapter.notifyDataSetChanged()
        }

        override fun onItemRangeMoved(
            sender: ObservableArrayList<RequestItem>?,
            fromPosition: Int,
            toPosition: Int,
            itemCount: Int
        ) {
            adapter.notifyDataSetChanged()
        }

        override fun onItemRangeRemoved(
            sender: ObservableArrayList<RequestItem>?,
            positionStart: Int,
            itemCount: Int
        ) {
            adapter.notifyDataSetChanged()
        }

    }
}

/*
class MyListAdapter() :
    ListAdapter<RequestItem, MyListAdapter.ViewHolder>(myListItemDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(private val binding: ListItemContentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RequestItem) {

            binding.item = item
            binding.executePendingBindings()
        }

        companion object {

            fun from(parent: ViewGroup): ViewHolder {

                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = TopUpListItemContentBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }
}

class myListItemDiffCallback : DiffUtil.ItemCallback<RequestItem>() {

    override fun areItemsTheSame(oldItem: RequestItem, newItem: RequestItem): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(
        oldItem: RequestItem,
        newItem: RequestItem
    ): Boolean {
        return oldItem == newItem
    }
}
*/



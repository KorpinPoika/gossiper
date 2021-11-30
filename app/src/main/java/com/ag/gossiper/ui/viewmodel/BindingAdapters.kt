package com.ag.gossiper.ui.viewmodel

import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableArrayList
import androidx.recyclerview.widget.RecyclerView

@BindingAdapter("app:itemViewModels")
fun setItems(listView: RecyclerView, items: ObservableArrayList<RequestItem>?) {
    items?.let {
        val adapter = getOrCreateAdapter(listView, items)
        adapter?.updateItems(items)
    }
}

private fun getOrCreateAdapter(recyclerView: RecyclerView, items: ObservableArrayList<RequestItem>?): RequestsListAdapter? {
    return if (recyclerView.adapter != null && recyclerView.adapter is RequestsListAdapter) {
        recyclerView.adapter as RequestsListAdapter
    } else {
        val bindableRecyclerAdapter = RequestsListAdapter(items ?: ObservableArrayList())
        recyclerView.adapter = bindableRecyclerAdapter
        null
    }
}
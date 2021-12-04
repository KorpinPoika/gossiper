package com.ag.gossiper.ui.viewmodel

import android.view.inputmethod.EditorInfo
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableArrayList
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText

@BindingAdapter("app:requestOnEditorActionListener")
fun setCustomOnEditorActionListener(view: TextInputEditText, listener: OnRequestEditorActionListener?) {
    if (listener == null) {
        view.setOnEditorActionListener(null)
    } else {
        view.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                listener.onEditorActionDone(view.text.toString())
            }
            false
        }
    }
}
@BindingAdapter("app:itemViewModels")
fun setItems(listView: RecyclerView, items: ObservableArrayList<RequestItem>?) {
    items?.let {
        val adapter = getOrCreateAdapter(listView, items)
        adapter?.updateItems(items)
    }
}

@BindingAdapter("app:onClickItem")
fun setOnItemClickHandler(listView: RecyclerView, listener: OnListItemActionListener?) {
    listener?.let { handler ->
        val adapter = getOrCreateAdapter(listView, null)
        (adapter ?: listView.adapter as RequestsListAdapter)?.let {
            it.onItemClickHandler = { x ->
                handler.onItemClick(x)
            }
        }
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


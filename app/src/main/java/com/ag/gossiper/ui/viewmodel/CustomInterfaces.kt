package com.ag.gossiper.ui.viewmodel

interface OnRequestEditorActionListener {
    fun onEditorActionDone(text: String?)
}

interface OnListItemActionListener {
    fun onItemClick(item: RequestItem)
}

interface IErrorHandler {
    fun showRequestInputError(msg: String)
    fun showSnackBar(msg: String)
}
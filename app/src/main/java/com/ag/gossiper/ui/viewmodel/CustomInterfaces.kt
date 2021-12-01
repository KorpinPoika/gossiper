package com.ag.gossiper.ui.viewmodel

interface OnRequestEditorActionListener {
    fun onEditorActionDone()
}

interface IErrorHandler {
    fun showRequestInputError(msg: String)
    fun showSnackBar(msg: String)
}
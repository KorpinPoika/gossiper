package com.ag.gossiper.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ag.gossiper.R
import com.ag.gossiper.databinding.ActivityMainBinding
import com.ag.gossiper.ui.viewmodel.IErrorHandler
import com.ag.gossiper.ui.viewmodel.MainViewModel
import com.ag.gossiper.ui.viewmodel.MainViewModelFactory
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(), IErrorHandler {

    lateinit var viewModel: MainViewModel
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.executePendingBindings()
        setSupportActionBar(binding.toolbar)

        viewModel = ViewModelProvider(this, MainViewModelFactory())[MainViewModel::class.java]
        binding.viewModel = viewModel
        viewModel.errorHandler = this

        binding.toolbar.setOnMenuItemClickListener { onMenuItemClick(it) }
        binding.lvRequests.layoutManager = LinearLayoutManager(this)

//        binding.txtRequest.onEditorAction()

        //WAEngine
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun onMenuItemClick(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_stop -> {
                viewModel?.stop()
                return true
            }
            R.id.action_clear -> {
                viewModel?.clearRequests()
                return true
            }
        }
        return false
    }

    override fun showRequestInputError(msg: String) {
        binding.txtRequest.error = msg
    }

    override fun showSnackBar(msg: String) {
        Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_INDEFINITE).apply {
            setAction(android.R.string.ok) {
                dismiss()
            }
            show()
        }
    }

}
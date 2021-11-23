package com.ag.gossiper.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.ag.gossiper.R
import com.ag.gossiper.databinding.ActivityMainBinding
import com.ag.gossiper.ui.viewmodel.MainViewModel
import com.ag.gossiper.ui.viewmodel.MainViewModelFactory

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MainViewModel
    lateinit var binding: ActivityMainBinding   //todo: do I need that?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.executePendingBindings()

        viewModel = ViewModelProvider(this, MainViewModelFactory())[MainViewModel::class.java]
        binding.viewModel = viewModel

        setContentView(R.layout.activity_main)
    }
}
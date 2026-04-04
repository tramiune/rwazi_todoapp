package com.rwazi.app.todo.ui.main

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.rwazi.app.todo.base.BaseActivity
import com.rwazi.app.todo.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity :
    BaseActivity<ActivityMainBinding, MainViewModel>(
        ActivityMainBinding::inflate
    ) {

    override val isDynamicTheme: Boolean = true
    override val isSplashScreen: Boolean = true

    override val classTypeOfViewModel: Class<MainViewModel>
        get() = MainViewModel::class.java

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun initControl(savedInstanceState: Bundle?) {
        palette?.let {
            binding.rootView.setBackgroundColor(it.backgroundColor)
        }
    }
}
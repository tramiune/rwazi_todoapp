package com.rwazi.app.todo.base

import DynamicBackgroundUtils
import ThemePalette
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<VB : ViewBinding, VM : BaseViewModel>(
    private val bindingFactory: (LayoutInflater) -> VB,
) : AppCompatActivity() {

    protected var palette: ThemePalette? = null
    protected open val isDynamicTheme: Boolean = false
    protected open val isSplashScreen: Boolean = false

    protected lateinit var binding: VB
        private set

    protected open val typeFullScreen: TypeFullScreen = TypeFullScreen.HIDE_NAVIGATION_BAR

    protected lateinit var viewModel: VM private set
    protected abstract val classTypeOfViewModel: Class<VM>

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        if (isSplashScreen) {
            installSplashScreen()
        }
        if (isDynamicTheme) {
            setupDynamicTheme()
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewModel = ViewModelProvider(this)[classTypeOfViewModel]
        binding = bindingFactory(layoutInflater)
        setContentView(binding.root)
        hideSystemUI()
        initControl(savedInstanceState)
        listener()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI()
        }
    }

    private fun setupDynamicTheme() {
        palette = DynamicBackgroundUtils.getRandomPalette()
        palette?.let {
            setTheme(it.themeResId)
            // Note: System bars will be set in hideSystemUI using the palette info
        }
    }

    private fun hideSystemUI() {
        val controller = WindowInsetsControllerCompat(window, window.decorView)

        palette?.let {
            controller.isAppearanceLightStatusBars = it.isLight
            controller.isAppearanceLightNavigationBars = it.isLight
        }

        when (typeFullScreen) {
            TypeFullScreen.HIDE_NAVIGATION_BAR -> {
                controller.hide(WindowInsetsCompat.Type.navigationBars())
            }

            TypeFullScreen.HIDE_STATUS_BAR -> {
                controller.hide(WindowInsetsCompat.Type.statusBars())
            }

            TypeFullScreen.HIDE_BOTH -> {
                controller.hide(
                    WindowInsetsCompat.Type.statusBars() or
                            WindowInsetsCompat.Type.navigationBars()
                )
            }

            TypeFullScreen.NORMAL -> {
                controller.show(
                    WindowInsetsCompat.Type.statusBars() or
                            WindowInsetsCompat.Type.navigationBars()
                )
            }
        }

        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }


    protected abstract fun initControl(savedInstanceState: Bundle?)
    open fun listener() {}
    open fun observer() {}

}

enum class TypeFullScreen {
    HIDE_NAVIGATION_BAR,
    HIDE_STATUS_BAR,
    HIDE_BOTH,
    NORMAL
}
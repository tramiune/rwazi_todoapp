package com.rwazi.app.todo.base

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.rwazi.app.todo.base.extension.hideKeyboard
import com.rwazi.app.todo.data.local.DataStorageManager
import com.rwazi.app.todo.util.DynamicBackgroundUtils
import com.rwazi.app.todo.util.ThemePalette
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

abstract class BaseActivity<VB : ViewBinding, VM : BaseViewModel>(
    private val bindingFactory: (LayoutInflater) -> VB,
) : AppCompatActivity() {

    @Inject
    lateinit var themeUtils: DynamicBackgroundUtils

    @Inject
    lateinit var dataStorageManager: DataStorageManager

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
        super.onCreate(savedInstanceState)
        if (isDynamicTheme) {
            setupDynamicTheme(savedInstanceState)
        }
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

    private fun setupDynamicTheme(savedInstanceState: Bundle?) {
        val savedThemeResId = savedInstanceState?.getInt("KEY_THEME_RES_ID", 0) ?: 0
        
        runBlocking {
            if (savedThemeResId != 0) {
                // Restore theme from rotation
                palette = themeUtils.getPalette(savedThemeResId)
            }
            
            if (palette == null) {
                val isAuto = dataStorageManager.isAutoTheme.first()
                val selectedResId = dataStorageManager.selectedThemeResId.first()

                palette = if (isAuto) {
                    themeUtils.getRandomPalette()
                } else {
                    themeUtils.getPalette(selectedResId) ?: themeUtils.getRandomPalette()
                }
            }
        }

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
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    v.clearFocus()
                    this.hideKeyboard(v)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

}

enum class TypeFullScreen {
    HIDE_NAVIGATION_BAR,
    HIDE_STATUS_BAR,
    HIDE_BOTH,
    NORMAL
}
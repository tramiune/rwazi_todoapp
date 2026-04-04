package com.rwazi.app.todo.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.rwazi.app.todo.base.type.ProgressType
import com.rwazi.app.todo.base.type.ViewState
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

// This is for ViewBinding
abstract class BaseFragment<VB : ViewBinding, VM : BaseViewModel>(val bindingFactory: (LayoutInflater) -> VB) :
    Fragment() {

    protected lateinit var viewModel: VM private set
    protected lateinit var binding: VB private set
    protected abstract val classTypeOfViewModel: Class<VM>
    open val shouldObserveViewModelState: Boolean = true
    
    /**
     * Set to true to automatically apply status bar padding to the root view.
     * Default is true. Override this if you want full-screen content without top padding.
     */
    open val shouldApplyWindowInsets: Boolean = true

    open fun setupClick() {}
    open fun observer() {}

    abstract fun initControl(view: View, savedInstanceState: Bundle?)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = bindingFactory(layoutInflater)
        viewModel = createViewModelLazy(classTypeOfViewModel.kotlin, { viewModelStore }).value
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        if (shouldApplyWindowInsets) {
            val originalPaddingTop = view.paddingTop
            ViewCompat.setOnApplyWindowInsetsListener(view) { v, windowInsets ->
                val insets = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars())
                v.setPadding(v.paddingLeft, insets.top + originalPaddingTop, v.paddingRight, v.paddingBottom)
                windowInsets
            }
        }

        if (shouldObserveViewModelState) {
            observeViewModelState(viewModel)
        }
        initControl(view, savedInstanceState)
        setupClick()
        observer()
    }

    fun observeViewModelState(viewModel: BaseViewModel) {
        viewModel.viewStateFlow.filterIsInstance<ViewState.Loading>().map { it.progress }
            .onEach(this::showProgressView).launchIn(lifecycleScope)

        viewModel.viewStateFlow.filterIsInstance<ViewState.Idle>().onEach(this::hideProgress)
            .launchIn(lifecycleScope)

        viewModel.viewStateFlow.filterIsInstance<ViewState.Error>().onEach(this::displayError)
            .launchIn(lifecycleScope)
    }


    fun showToast(msg: String, duration: Int = Toast.LENGTH_LONG) {
        Toast.makeText(context, msg, duration).show()
    }

    override fun onDestroyView() {
        (binding.root.parent as? ViewGroup?)?.endViewTransition(binding.root)
        super.onDestroyView()
    }

    open fun showProgressView(progress: ProgressType) {}

    open fun hideProgress(idle: ViewState.Idle) {}

    open fun displayError(error: ViewState.Error) {
        if (error.showError) {
            showToast(error.error)
        }
    }
}

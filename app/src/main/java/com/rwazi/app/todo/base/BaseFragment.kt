package com.rwazi.app.todo.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

    open fun listener() {}
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
        if (shouldObserveViewModelState) {
            observeViewModelState(viewModel)
        }
        initControl(view, savedInstanceState)
        listener()
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

    abstract fun showProgressView(progress: ProgressType)

    abstract fun hideProgress(idle: ViewState.Idle)

    abstract fun displayError(error: ViewState.Error)

}

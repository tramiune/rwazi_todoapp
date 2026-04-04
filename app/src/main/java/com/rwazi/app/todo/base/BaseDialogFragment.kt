package com.rwazi.app.todo.base

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

abstract class BaseDialogFragment<B : ViewBinding> : DialogFragment() {

    private var _binding: B? = null
    protected val binding get() = _binding!!
    protected open val dialogGravity: Int = Gravity.CENTER
    protected open val topMargin: Int = 0
    protected open val isFullScreen: Boolean = false

    abstract fun initDialog()

    override fun onCreateDialog(savedInstanceState: Bundle?) =
        super.onCreateDialog(savedInstanceState).apply {
            setCancelable(false)
            setOnKeyListener { _, keyCode, event ->
                keyCode == KeyEvent.KEYCODE_BACK &&
                        event.action == KeyEvent.ACTION_UP
            }
        }

    @Suppress("UNCHECKED_CAST")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val clazz =
            (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<B>
        val method = clazz.getMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        )
        _binding = method?.invoke(null, inflater, container, false) as B

        dialog?.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            val widthParam =
                if (isFullScreen) ViewGroup.LayoutParams.MATCH_PARENT else ViewGroup.LayoutParams.WRAP_CONTENT
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, widthParam)
            setGravity(dialogGravity)

            val params = attributes
            params.y = topMargin
            attributes = params
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDialog()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

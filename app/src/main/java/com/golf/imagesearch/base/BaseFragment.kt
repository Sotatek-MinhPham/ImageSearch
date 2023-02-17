package com.golf.imagesearch.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.viewbinding.ViewBinding
import com.google.android.material.appbar.MaterialToolbar
import com.golf.imagesearch.BaseNavigationListener
import com.golf.imagesearch.R
import com.golf.imagesearch.utils.consumeAllClicks
import com.golf.imagesearch.utils.hideKeyboard
import com.golf.imagesearch.utils.setNavigationIconClick

abstract class BaseFragment<VM : BaseViewModel, VB : ViewBinding> : Fragment(), LifecycleObserver {
    protected abstract fun getViewModelClass(): Class<VM>
    private var _binding: ViewBinding? = null
    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB
    abstract fun setupViews(savedInstanceState: Bundle?)
    protected lateinit var viewModel: VM
    protected lateinit var baseNavigationListener: BaseNavigationListener
    private var appInBackground = false

    open fun onCreateViewModel() {
        viewModel = ViewModelProvider(this).get(getViewModelClass())
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        baseNavigationListener = context as BaseNavigationListener
    }

    @Suppress("UNCHECKED_CAST")
    protected val binding: VB
        get() = _binding as VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindingInflater.invoke(inflater, container, false)
        return requireNotNull(_binding).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onCreateViewModel()
        view.consumeAllClicks()
        hideKeyboard()
        initViewModel(viewModel)
        if (enableNavigationIcon()) view.findViewById<MaterialToolbar>(R.id.layoutToolbar)
            ?.setNavigationIconClick { baseNavigationListener.popFragment() }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    baseNavigationListener.popFragment()
                }
            })
        setupViews(savedInstanceState)
        lifecycle.addObserver(this)
    }

    fun initViewModel(viewModel: BaseViewModel) {
        viewModel.apply {
            progressLoadingLiveData.observe(viewLifecycleOwner, Observer {
                showProgress(it)
            })
            showErrorLiveData.observe(viewLifecycleOwner, Observer {
                showErrorSnackbar(it)
            })
            showMessageLiveData.observe(viewLifecycleOwner, Observer {
                showSnackbar(it)
            })
            showMessageSuccessLiveData.observe(viewLifecycleOwner, Observer {
                showSuccessSnackbar(it)
            })

            viewModel.navigationFragmentLiveData.observe(viewLifecycleOwner, Observer {
                handleNavigation(it)
            })
            viewModel.popToFragmentLiveData.observe(viewLifecycleOwner, Observer {
                baseNavigationListener.popToFragment(it)
            })
        }
    }

    private fun handleNavigation(navigationFragment: NavigationFragment) {
        when (navigationFragment) {
            is NavigationFragment.HomeFragment -> {
                baseNavigationListener.showHomeFragment()
            }

        }
    }

    override fun onStop() {
        super.onStop()
        hideKeyboard()
    }

    override fun onResume() {
        super.onResume()
        hideKeyboard()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun startSomething() {
        if (appInBackground) {
            appFromBackground()
        }
        appInBackground = false
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stopSomething() {
        appInBackground = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    open fun appFromBackground() {

    }

    private fun showProgress(
        visible: Boolean
    ) {
        val fragment = childFragmentManager.findFragmentByTag(ProgressDialogFragment.PROGRESS_TAG)
        if (fragment != null && !visible) {
            (fragment as ProgressDialogFragment).dismissAllowingStateLoss()
            childFragmentManager.executePendingTransactions()
        } else if (fragment == null && visible) {
            val backgroundColorResId = R.color.transparent
            ProgressDialogFragment.newInstance(backgroundColorResId, 0)
                .show(childFragmentManager, ProgressDialogFragment.PROGRESS_TAG)
            childFragmentManager.executePendingTransactions()
        }
    }

    open fun visibleChange(visible: Boolean) {
    }

    open fun enableBackPressed() = true

    open fun killApp() = false

    open fun enableNavigationIcon() = true

    open fun onNewIntent(intent: Intent?) {

    }
}

sealed class NavigationFragment {
    object HomeFragment : NavigationFragment()

}


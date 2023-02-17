package com.golf.imagesearch

import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.golf.imagesearch.base.BaseActivity
import com.golf.imagesearch.base.BaseFragment
import com.golf.imagesearch.base.NavigationFragment
import com.golf.imagesearch.home.imagesearch.ImageSearchFragment
import com.golf.imagesearch.home.imagesearch.ImageSearchViewModel
import com.golf.imagesearch.home.imagesearch.ImageViewFragment
import com.golf.imagesearch.model.Photo
import com.golf.imagesearch.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : BaseActivity(), BaseNavigationListener,
     ImageNavigationListener {

    private val imageSearchViewModel by viewModels<ImageSearchViewModel>()
    private var pendingTaskLogin: (() -> Unit)? = null
    override fun layoutId(): Int = R.layout.activity_main

    override fun setupViews() {
        setSystemBarTheme(this, false)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) window.statusBarColor = Color.GRAY
        if (supportFragmentManager.fragments.isEmpty()) {
            showImageSearchFragment()
            fragmentStackChange()
        }
    }

    override fun showImageSearchFragment() {
        overlayFragment(
            ImageSearchFragment(),
            animation = false,
            animateFromBot = false
        )
    }


    override fun showImageViewFragment(src: MutableList<Photo?>) {
        overlayFragment(
            ImageViewFragment().apply {
                this.imageList.addAll(src)
            },
            animation = false,
            animateFromBot = false
        )
    }

    override fun popFragment() {
        supportFragmentManager.popBackStack()
    }

    override fun showHomeFragment() {
        if (supportFragmentManager.isStateSaved) {
            pendingTaskLogin = {
                login()
            }
        } else {
            login()
        }
    }

    private fun login() {
        val pendingFragment = imageSearchViewModel.pendingFragment.value
        val pendingTask = imageSearchViewModel.pendingTask.value
        if (pendingFragment != null && containFragment(pendingFragment) && containFragment(
                ImageSearchFragment()
            )
        ) {
            popToFragment(pendingFragment)
            imageSearchViewModel.pendingFragment.postValue(null)
            imageSearchViewModel.pendingTask.postValue { }
        } else {
            clearALlFragment()
            replaceFragment(ImageSearchFragment().apply {
            })
        }
    }

    override fun popToFragment(fragment: Fragment) {
        if (supportFragmentManager.isStateSaved) {
            pendingTaskLogin = {
                Handler(Looper.getMainLooper()).post {
                    popToFragments(fragment)
                }
            }
        } else {
            Handler(Looper.getMainLooper()).post {
                popToFragments(fragment)
            }
        }
    }

    private fun fragmentStackChange() {
        supportFragmentManager.addOnBackStackChangedListener {
            val countStack = supportFragmentManager.backStackEntryCount
            if (countStack == 1) {
                val homeFr =
                    supportFragmentManager.findFragmentByTag(NavigationFragment.HomeFragment::class.java.canonicalName)
                homeFr?.let {
                    if (it is BaseFragment<*, *>) {
                        it.visibleChange(false)
                    }
                }
            }
            if (countStack >= 2) {
                val lastEntry = supportFragmentManager.getBackStackEntryAt(countStack - 2)
                val lastFragmentInStack = supportFragmentManager.findFragmentByTag(lastEntry.name)
                if (lastFragmentInStack is BaseFragment<*, *>) {
                    lastFragmentInStack.visibleChange(false)
                }
            }
            val currentFragment = currentFragment()
            if (currentFragment is BaseFragment<*, *>) {
                currentFragment.visibleChange(true)
            }
        }
        lifecycleScope.launchWhenStarted {
            delay(300)
            supportFragmentManager.executePendingTransactions()
            val currentFragment = currentFragment()
            if (currentFragment is BaseFragment<*, *>) {
                currentFragment.visibleChange(true)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val currentFragment = currentFragment()
        if (currentFragment is BaseFragment<*, *> && currentFragment.enableBackPressed()) {
            val stackCount = supportFragmentManager.backStackEntryCount
            if (stackCount == 0) {
                finish()
            } else {
                super.onBackPressed()
            }

        }
        if (currentFragment is BaseFragment<*, *> && currentFragment.killApp()) {
            finish()
        }
    }

}

interface BaseNavigationListener {
    fun popFragment()
    fun popToFragment(fragment: Fragment)
    fun showHomeFragment()
    val forImage get() = this as ImageNavigationListener
}

interface ImageNavigationListener : BaseNavigationListener {
    fun showImageSearchFragment()
    fun showImageViewFragment(src: MutableList<Photo?>)
}
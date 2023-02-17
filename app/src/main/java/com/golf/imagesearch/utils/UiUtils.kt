package com.golf.imagesearch.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.material.card.MaterialCardView
import com.golf.imagesearch.R


inline fun View.setOnDebouncedClickListener(
    minimumClicksIntervalMillis: Long = 800L,
    crossinline action: (view: View) -> Unit
) {
    setOnClickListener(object : DebounceClickListener(minimumClicksIntervalMillis) {
        override fun onDebouncedClick(view: View) {
            action.invoke(view)
        }
    })
}

@SuppressLint("ClickableViewAccessibility")
fun View.consumeAllClicks() {
    this.setOnTouchListener { _, _ -> true }
}

fun Fragment.hideKeyboard() = activity?.hideKeyboard()

fun Activity.hideKeyboard() {
    val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    currentFocus?.let {
        manager.hideSoftInputFromWindow(it.windowToken, 0)
        it.clearFocus()
    }
}

fun Toolbar.setNavigationIconClick(@OptIn onClickNavigation: ((View) -> Unit)? = null) {
    if (onClickNavigation != null) {
        navigationIcon = ContextCompat.getDrawable(context, R.drawable.ic_arrow_back_24)
        setNavigationOnClickListener(onClickNavigation)
    } else {
        navigationIcon = null
    }
}

fun FragmentActivity.popToFragments(fragment: Fragment) {
    var currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
    while (fragment.javaClass.canonicalName != currentFragment?.javaClass?.canonicalName) {
        supportFragmentManager.popBackStackImmediate()
        currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
    }
}

fun setSystemBarTheme(pActivity: Activity, pIsDark: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        // Fetch the current flags.
        val lFlags = pActivity.window.decorView.systemUiVisibility
        // Update the SystemUiVisibility dependening on whether we want a Light or Dark theme.
        pActivity.window.decorView.systemUiVisibility =
            if (pIsDark) lFlags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv() else lFlags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
}

fun FragmentActivity.currentFragment(): Fragment? {
    return supportFragmentManager.findFragmentById(R.id.fragment_container)
}

fun FragmentActivity.containFragment(fragment: Fragment): Boolean {
    val currentFragment = supportFragmentManager.findFragmentByTag(fragment.javaClass.canonicalName)
    return currentFragment != null
}

fun FragmentActivity.clearALlFragment(
) {
    try {
        for (i in 0 until supportFragmentManager.backStackEntryCount) {
            supportFragmentManager.popBackStack()
        }
    } catch (e: Exception) {

    }
}

inline fun FragmentActivity.fragmentTransaction(block: FragmentTransaction.() -> Unit) {
    supportFragmentManager.beginTransaction().apply(block).apply {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) commit() else commitAllowingStateLoss()
    }
}

fun FragmentActivity.replaceFragment(
    fragment: Fragment,
    @OptIn addToBackStack: Boolean = false,
    animation: Boolean = false
) {
    val tag = fragment.javaClass.canonicalName
    fragmentTransaction {
        if (animation) setCustomAnimations(
            R.anim.slide_in_right,
            R.anim.slide_out_left,
            R.anim.slide_in_left,
            R.anim.slide_out_right
        )
        replace(R.id.fragment_container, fragment, tag)
        if (addToBackStack) addToBackStack(tag)
    }
}

fun FragmentActivity.overlayFragment(
    fragment: Fragment,
    addToBackStack: Boolean = true,
    animation: Boolean = true,
    animateFromBot: Boolean = false,
    animateTopRight: Boolean = false
) {
    val tag = fragment.javaClass.canonicalName
    fragmentTransaction {
        if (animation) {
            if (animateFromBot) {
                setCustomAnimations(
                    R.anim.slide_in_bottom,
                    R.anim.slide_out_top,
                    R.anim.slide_in_top,
                    R.anim.slide_out_bottom
                )
            } else if (animateTopRight) {
                setCustomAnimations(
                    R.anim.animate_shopping_cation_enter,
                    R.anim.animate_shopping_caution_exit,
                    R.anim.animate_shopping_cation_enter,
                    R.anim.animate_shopping_caution_exit
                )
            } else {
                setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
            }

        }
        add(R.id.fragment_container, fragment, tag)
        if (addToBackStack) addToBackStack(tag)
    }
}

fun Toolbar.setStyledTitle(title: String = "") {
    navigationIcon?.mutate()
    val toolbarTitle = findViewById<TextView>(R.id.tvToolbarTitle)
    toolbarTitle?.text = title
    val context = this.context
    toolbarTitle?.typeface = Typeface.create(
        ResourcesCompat.getFont(context, R.font.roboto),
        Typeface.BOLD
    )

}

fun TextView.hideKeyboard() {
    clearFocus()
    val manager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    manager.hideSoftInputFromWindow(windowToken, 0)
}

fun loadImageSearch(
    cardView: MaterialCardView,
    imageView: ImageView,
    url: Any?,
    withAnimation: Boolean = false,
    placeHolder: Int = -1
) {
    val params = imageView.layoutParams
    params.height = imageView.context.dpToPx(40).toInt()
    params.width = imageView.context.dpToPx(40).toInt()
    imageView.layoutParams = params

    Glide.with(imageView.context)
        .load(url)
        .placeholder(placeHolder)
        .apply(RequestOptions().override(Target.SIZE_ORIGINAL))
        .apply {
            if (withAnimation) {
                transition(DrawableTransitionOptions().crossFade())
            }
        }
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                p0: GlideException?,
                p1: Any?,
                p2: Target<Drawable>?,
                p3: Boolean
            ): Boolean {
                cardView.setCardBackgroundColor(
                    ContextCompat.getColor(
                        cardView.context,
                        R.color.offWhite
                    )
                )
                val params = imageView.layoutParams
                params.height = imageView.context.dpToPx(40).toInt()
                params.width = imageView.context.dpToPx(40).toInt()
                imageView.layoutParams = params

                return false
            }


            override fun onResourceReady(
                p0: Drawable?,
                p1: Any?,
                p2: Target<Drawable>?,
                p3: DataSource?,
                p4: Boolean
            ): Boolean {
                cardView.setCardBackgroundColor(
                    ContextCompat.getColor(
                        cardView.context,
                        R.color.lightGrey
                    )
                )
                val params = imageView.layoutParams
                params.height = ViewGroup.LayoutParams.MATCH_PARENT
                params.width = ViewGroup.LayoutParams.MATCH_PARENT
                imageView.layoutParams = params

                imageView.visibility = View.VISIBLE
                return false
            }
        })
        .into(imageView)
}
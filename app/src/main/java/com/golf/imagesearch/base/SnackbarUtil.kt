package com.golf.imagesearch.base

import android.app.Activity
import android.content.res.ColorStateList
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

@JvmOverloads
fun Fragment.showSuccessSnackbar(
    text: CharSequence
) = showSnackbar(text, BaseTransientBottomBar.LENGTH_LONG, android.R.color.holo_green_dark)

@JvmOverloads
fun Fragment.showErrorSnackbar(
    text: CharSequence,
    offsetY: Int = 0
) = showSnackbar(text, BaseTransientBottomBar.LENGTH_LONG, android.R.color.holo_red_dark)

const val ERROR_DISPLAY_DURATION = 4000

@JvmOverloads
fun Fragment.showWarningSnackbar(
    text: CharSequence
) = showSnackbar(text, BaseTransientBottomBar.LENGTH_LONG, android.R.color.holo_orange_dark)

@JvmOverloads
fun Fragment.showSnackbar(
    text: CharSequence,
    duration: Int = BaseTransientBottomBar.LENGTH_SHORT,
    @ColorRes bgColorRes: Int? = null,
    offsetY: Int = 0
): Snackbar? = view?.makeSnackbar(requireActivity(), text, duration)?.apply {
    if (bgColorRes != null) view.backgroundTintList =
        ColorStateList.valueOf(ContextCompat.getColor(context, bgColorRes))
    if (offsetY > 0) setBottomOffset(offsetY)
    show()
}

private fun View.makeSnackbar(
    activity: Activity,
    text: CharSequence,
    duration: Int = BaseTransientBottomBar.LENGTH_SHORT
): Snackbar {
    try {
        return Snackbar.make(this, text, duration).also {
            findViewById<TextView>(com.google.android.material.R.id.snackbar_text)?.let { snackTextView ->
                snackTextView.maxLines = 4
                val resources = snackTextView.context.resources
                TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                    snackTextView,
                    resources.getDimension(com.intuit.sdp.R.dimen._7sdp).toInt(),
                    resources.getDimension(com.intuit.sdp.R.dimen._11sdp).toInt(),
                    1,
                    TypedValue.COMPLEX_UNIT_PX
                )
            }
            it.view.apply {
                findViewById<TextView>(com.google.android.material.R.id.snackbar_text)?.let { snackTextView ->
                    snackTextView.maxLines = 4
                }
            }
        }
    } catch (e: Exception) {
        return Snackbar.make(activity.findViewById(android.R.id.content), text, duration).also {
            findViewById<TextView>(com.google.android.material.R.id.snackbar_text)?.let { snackTextView ->
                snackTextView.maxLines = 4
                val resources = snackTextView.context.resources
                TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                    snackTextView,
                    resources.getDimension(com.intuit.sdp.R.dimen._7sdp).toInt(),
                    resources.getDimension(com.intuit.sdp.R.dimen._11sdp).toInt(),
                    1,
                    TypedValue.COMPLEX_UNIT_PX
                )
            }
            it.view.apply {
                findViewById<TextView>(com.google.android.material.R.id.snackbar_text)?.let { snackTextView ->
                    snackTextView.maxLines = 4
                }
            }
        }
    }
}

private fun Snackbar.setBottomOffset(offsetY: Int) {
    view.layoutParams.let { params ->
        when (params) {
            is LinearLayout.LayoutParams -> params.bottomMargin += offsetY
            is FrameLayout.LayoutParams -> params.bottomMargin += offsetY
            is RelativeLayout.LayoutParams -> params.bottomMargin += offsetY
            is ConstraintLayout.LayoutParams -> params.bottomMargin += offsetY
        }
    }
}


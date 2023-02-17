package com.golf.imagesearch.base

import android.app.ActionBar
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.golf.imagesearch.R
import com.golf.imagesearch.databinding.FragmentProgressDialogBinding

class ProgressDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentProgressDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun getTheme(): Int {
        return R.style.ProgressDialogTheme
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(requireActivity(), theme) {
            override fun onAttachedToWindow() {
                super.onAttachedToWindow()
                this.window?.apply {
                    arguments?.let {
                        setBackgroundDrawableResource(it.getInt(ARG_BACKGROUND_RES_ID))
                    }
                    setLayout(
                        ActionBar.LayoutParams.MATCH_PARENT,
                        ActionBar.LayoutParams.MATCH_PARENT
                    )
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProgressDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val messageResId = requireArguments().getInt(ARG_MESSAGE_RES_ID)
        if (messageResId != 0) {
            binding.progressMessage.isVisible = true
            binding.progressMessage.setText(messageResId)
        } else {
            binding.progressMessage.isVisible = false
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        try {
            val ft = manager.beginTransaction()
            ft.add(this, tag)
            ft.commitAllowingStateLoss()
        } catch (e: Exception) {
        }
    }

    companion object {
        const val PROGRESS_TAG = "progress_dialog"

        private const val ARG_BACKGROUND_RES_ID = "arg_background_res_id"
        private const val ARG_MESSAGE_RES_ID = "arg_message_res_id"

        fun newInstance(backgroundResId: Int = R.color.transparent, @OptIn messageResId: Int = 0) =
            ProgressDialogFragment().apply {
                arguments = bundleOf(
                    ARG_BACKGROUND_RES_ID to backgroundResId,
                    ARG_MESSAGE_RES_ID to messageResId
                )
            }
    }
}
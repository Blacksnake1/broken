package com.nekosoft.brokenglass.customView.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.brokenscreen.prankapp.crack.screen.R
import com.brokenscreen.prankapp.crack.screen.databinding.BottomSheetLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetDialog : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetLayoutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetLayoutBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setOnShowListener { it ->
            isCancelable = true
        }

        binding.vgTouch.setOnClickListener {
            context?.resources?.getColor(R.color.yellow_ff7c05)
                ?.let {
                    binding.tvTouch.setTextColor(it)
                    binding.ivTouch.setColorFilter(it)
                    binding.cbTouch.setImageResource(R.drawable.ic_checked)
                }
            context?.resources?.getColor(R.color.gray_7b7c80)
                ?.let {
                    binding.tvShake.setTextColor(it)
                    binding.ivShake.setColorFilter(it)
                    binding.cbShake.setImageResource(R.drawable.ic_uncheck)
                }
        }

        binding.vgShake.setOnClickListener {
            context?.resources?.getColor(R.color.gray_7b7c80)
                ?.let {
                    binding.tvTouch.setTextColor(it)
                    binding.ivTouch.setColorFilter(it)
                    binding.cbTouch.setImageResource(R.drawable.ic_uncheck)
                }
            context?.resources?.getColor(R.color.yellow_ff7c05)
                ?.let {
                    binding.tvShake.setTextColor(it)
                    binding.ivShake.setColorFilter(it)
                    binding.cbShake.setImageResource(R.drawable.ic_checked)
                }
        }
    }


    companion object {
        const val TAG = "ModalBottomSheetDialog"
    }

}
package com.nekosoft.brokenglass.customView.dialog

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import com.brokenscreen.prankapp.crack.screen.R
import com.brokenscreen.prankapp.crack.screen.databinding.DialogRateBinding
import com.google.android.play.core.review.ReviewManagerFactory
import com.nekosoft.brokenglass.utils.ConstantsApp.IS_NEW_TURN_FOR_SHOW_DIALOG_RATE
import com.nekosoft.brokenglass.extention.disableExt
import com.nekosoft.brokenglass.extention.enableExt
import com.nekosoft.brokenglass.extention.goneExt
import com.nekosoft.brokenglass.extention.visibleExt
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class DialogRateShort(
    val context: Activity,
) : Dialog(context) {
    private lateinit var binding: DialogRateBinding
    var numberStar = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogRateBinding.inflate(layoutInflater)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        setCancelable(false)
        setContentView(binding.root)
        val width = (context.resources.displayMetrics.widthPixels * 0.95).toInt()
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        window?.setLayout(width, height)


        val stars =
            listOf(
                binding.imvStar1,
                binding.imvStar2,
                binding.imvStar3,
                binding.imvStar4,
                binding.imvStar5
            )

        MainScope().launch {
            stars.forEach {
                delay(100)
                it.setImageResource(R.drawable.ic_star_rate)
                scale(it)
                it.enableExt()
            }
        }

        binding.btnMayBe.setOnClickListener {
            dismiss()
            IS_NEW_TURN_FOR_SHOW_DIALOG_RATE = false
            DialogRateUtil().setTimeCancelRate(context, System.currentTimeMillis())
        }

        binding.btnOnGoogle.setOnClickListener {
            dismiss()
            if (numberStar >= 3)
                rateApp()
            else
                Toast.makeText(
                    context,
                    context.getString(R.string.thank_you_for_rating_us),
                    Toast.LENGTH_SHORT
                ).show()
            DialogRateUtil().setRate(context)
//            LogEvent.log(context, "click_rate_rateus")
        }
        binding.btnRate.setOnClickListener {
            dismiss()
            rateApp()
            DialogRateUtil().setRate(context)
        }

        val listString =
            listOf(
                R.string.content_1_star,
                R.string.content_2_star,
                R.string.content_3_star,
                R.string.content_4_star,
                R.string.content_5_star
            )
        val listIcon =
            listOf(
                R.drawable.ic_rate_1_star,
                R.drawable.ic_rate_2_star,
                R.drawable.ic_rate_3_star,
                R.drawable.ic_rate_4_star,
                R.drawable.ic_rate_5_star
            )
        for (i in stars.indices) {
            val star = stars[i]
            star.disableExt(1f)
            star.setOnClickListener {
                binding.btnOnGoogle.visibleExt()
                binding.btnMayBe.goneExt()
                binding.btnRate.goneExt()
                binding.tvContentRate.visibleExt()

                for (j in stars.indices) {
                    val shouldFill = j <= i
                    stars[j].setImageResource(
                        if (shouldFill) R.drawable.ic_star_rate
                        else R.drawable.ic_star_empty
                    )
                }
                numberStar = i
                if (i >= 3) {
                    if (i == 3) {
                        binding.tvTitleRate.text = context.getString(R.string.title_4_star)
                        binding.btnOnGoogle.text = context.getString(R.string.rate_us)
                    } else if (i == 4) {
                        binding.tvTitleRate.text = context.getString(R.string.title_5_star)
                        binding.btnOnGoogle.text = context.getString(R.string.rate_on_google)
                    }
                } else if (i in 0..2) {
                    binding.tvTitleRate.text = context.getString(R.string.title_1_star)
                    binding.btnOnGoogle.text = context.getString(R.string.rate_us)
                }
                binding.tvContentRate.text = context.getString(listString[i])
                binding.imvStar.setImageResource(listIcon[i])
            }
        }

    }

    private fun scale(view: View) {
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.5f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.5f)
        val animator = ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY)
        animator.repeatCount = 1
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.duration = 1000
        animator.disableViewDuringAnimation(view)
        animator.start()
    }

    private fun ObjectAnimator.disableViewDuringAnimation(view: View) {
        // This extension method listens for start/end
        // events on an animation and disables
        // the given view for the entirety of that animation.
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                view.isEnabled = false
            }

            override fun onAnimationEnd(animation: Animator) {
                view.isEnabled = true
            }
        })
    }


    private fun rateApp() {
//        try {
        showDialogRate()
//            context.startActivity(
//                Intent(
//                    "android.intent.action.VIEW",
//                    Uri.parse("market://details?id=" + context.packageName)
//                )
//            )
//        } catch (unused: ActivityNotFoundException) {
//            context.startActivity(
//                Intent(
//                    "android.intent.action.VIEW",
//                    Uri.parse("https://play.google.com/store/apps/details?id=" + context.packageName)
//                )
//            )
//        }
    }

    private fun showDialogRate() {
        val manager = context?.let { ReviewManagerFactory.create(it) }
        manager?.requestReviewFlow()?.addOnCompleteListener {
            if (it.isSuccessful) {
                manager.launchReviewFlow(context, it.result)
            }
        }
    }

    override fun setOnDismissListener(listener: DialogInterface.OnDismissListener?) {
        super.setOnDismissListener(listener)
    }


}



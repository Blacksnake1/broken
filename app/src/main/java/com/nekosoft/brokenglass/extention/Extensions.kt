package com.nekosoft.brokenglass.extention

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.util.Size
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewTreeObserver
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentActivity
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.math.hypot

// # Kotlin Extensions

//- [View](#view)
//- [Context](#context)
//- [Fragment](#fragment)
//- [Activity](#activity)
//- [ViewGroup](#viewgroup)
//- [TextView](#textview)
//- [String](#string)
//- [Other](#other)


// ## View

fun sdkEqualOrGreaterThan(version: Int): Boolean {
    return Build.VERSION.SDK_INT >= version
}


/**
 * Extension method to provide simpler access to {@link View#getResources()#getString(int)}.
 */
fun View.getString(stringResId: Int): String = resources.getString(stringResId)

/**
 * Extension method to show a keyboard for View.
 */
fun View.showKeyboard() {
    this.requestFocus()
    (context.getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager)?.showSoftInput(this, 0)
    ViewCompat.getWindowInsetsController(this)?.show(WindowInsetsCompat.Type.ime())
}


/**
 * Try to hide the keyboard and returns whether it worked
 * https://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
 */
fun View.hideKeyboard() {
    (context.getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager)?.hideSoftInputFromWindow(
        windowToken,
        0
    )
    ViewCompat.getWindowInsetsController(this)?.hide(WindowInsetsCompat.Type.ime())
}

fun View.visibleExt() {
    this.visibility = View.VISIBLE
}

fun View.invisibleExt() {
    this.visibility = View.INVISIBLE
}

fun View.goneExt() {
    this.visibility = View.GONE
}

fun View.disableExt(opacity: Float?) {
    this.isEnabled = false
    this.isClickable = false
    var opa = 0.3f
    if (opacity != null) {
        opa = opacity
    }
    this.alpha = opa
}

fun View.enableExt() {
    this.isEnabled = true
    this.isClickable = true
    this.alpha = 1f
}

fun disableViewsExt(vararg view: View, opacity: Float?) {
    for (v in view) {
        v.disableExt(opacity)
    }
}

fun enableViewsExt(vararg view: View) {
    for (v in view) {
        v.enableExt()
    }
}

fun showHideGoneViewsExt(
    viewVisible: Array<View?> = emptyArray(),
    viewGone: Array<View?> = emptyArray(),
    viewInVisible: Array<View?> = emptyArray()
) {
    viewVisible.forEach { it?.visibility = View.VISIBLE }
    viewGone.forEach { it?.visibility = View.GONE }
    viewInVisible.forEach { it?.visibility = View.INVISIBLE }
}

//fun Exception.safeLog() {
//    if (BuildConfig.DEBUG) printStackTrace()
//}
//
//fun Throwable.safeLog() {
//    if (BuildConfig.DEBUG) printStackTrace()
//}


fun View.visibleCircular(cx: Int = width / 2, cy: Int = height / 2) {
    // get the center for the clipping circle
//    val cx = width / 2
//    val cy = height / 2

    // get the final radius for the clipping circle
    val finalRadius = hypot(cx.toDouble(), cy.toDouble()).toFloat()

    // create the animator for this view (the start radius is zero)
    val anim = ViewAnimationUtils.createCircularReveal(this, cx, cy, 0f, finalRadius)
    // make the view visible and start the animation
    visibleExt()
    if (isAttachedToWindow) {
        anim.start()
    }
}


fun View.invisibleCircular(
    cx: Int = width / 2,
    cy: Int = height / 2,
    onAnimationEnd: (() -> Unit)? = null
) {
    // get the center for the clipping circle
//    val cx = width / 2
//    val cy = height / 2

    // get the initial radius for the clipping circle
    val initialRadius = hypot(cx.toDouble(), cy.toDouble()).toFloat()

    // create the animation (the final radius is zero)
    val anim = ViewAnimationUtils.createCircularReveal(this, cx, cy, initialRadius, 0f)

    // make the view invisible when the animation is done
    anim.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            invisibleExt()
            onAnimationEnd?.invoke()
        }
    })

    // start the animation
    if (isAttachedToWindow) {
        anim.start()
    }
}


fun View.fadeOut(duration: Long = 200) {
    this.apply {
        animate()
            .setInterpolator(FastOutSlowInInterpolator())
            .alpha(0f)
            .setDuration(duration)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    goneExt()
                }
            })
            .setStartDelay(100)
            .start()
    }
}

fun View.fadeIn(duration: Long = 200) {
    this.apply {
        visibleExt()
        alpha = 0f
        animate()
            .setInterpolator(FastOutSlowInInterpolator())
            .alpha(1f)
            .setDuration(duration)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                }
            })
            .start()
    }
}

fun View.observeViewSize(callBack: (Size) -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            callBack.invoke(Size(measuredWidth, measuredHeight))
        }
    })
}

fun AppCompatActivity.setFullScreenMode() {
    window.decorView.systemUiVisibility =
        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    setWindowFlag(window, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
    window.statusBarColor = Color.TRANSPARENT
}

fun AppCompatTextView.setupTextColor(@ColorRes colorId: Int) {
    this.setTextColor(ContextCompat.getColor(context, colorId))
}


//fun Context.ifIsInternetAvailable(doTask: () -> Unit) {
//    if (Networking.isNetworkConnected(this)) {
//        doTask.invoke()
//    } else {
//        showDialog(
//            message = R.string.please_connect_to_network,
//            textPositive = R.string.ok
//        )
//    }
//}


private fun setWindowFlag(window: Window, bits: Int, on: Boolean) {
    val winParams = window.attributes
    if (on) {
        winParams.flags = winParams.flags or bits
    } else {
        winParams.flags = winParams.flags and bits.inv()
    }
    window.attributes = winParams
}

fun RecyclerView.removeBlink(): RecyclerView {
    val animator = this.itemAnimator
    if (animator is SimpleItemAnimator) {
        animator.supportsChangeAnimations = false
    }
    return this
}

fun <T> List<T>.isEqual(other: List<T>): Boolean {
    if (this.size != other.size) return false
    return this.zip(other).all { (x, y) -> x == y }
}

// Convert ms to mm:ss format
fun Long.toDurationFormat(): String =
    String.format(
        "%02d:%02d",
        TimeUnit.MILLISECONDS.toMinutes(this),
        TimeUnit.MILLISECONDS.toSeconds(this) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(this))
    )

fun View.setBackgroundTint(color: Int) {
    try {
        DrawableCompat.setTint(
            background, ContextCompat.getColor(context, color)
        )
    } catch (e: Exception) {
        Log.e("Exception", "setBackgroundTint_266:" + e)
    }
}

//animation cho ngôi sao trong màn rate
fun ObjectAnimator.disableViewDuringAnimation(view: View) {
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

fun LifecycleOwner.launchWhenResumed(block: suspend CoroutineScope.() -> Unit) {
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.RESUMED) {
            block()
            this@launch.cancel()
        }
    }
}


/*

/**
 * Extension method to remove the required boilerplate for running code after a view has been
 * inflated and measured.
 *
 * @author Antonio Leiva
 * @see <a href="https://antonioleiva.com/kotlin-ongloballayoutlistener/>Kotlin recipes: OnGlobalLayoutListener</a>
 */
inline fun <T : View> T.afterMeasured(crossinline f: T.() -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            if (measuredWidth > 0 && measuredHeight > 0) {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                f()
            }
        }
    })
}


/**
 * Extension method to get ClickableSpan.
 * e.g.
 * val loginLink = getClickableSpan(context.getColorCompat(R.color.colorAccent), { })
 */
fun View.doOnLayout(onLayout: (View) -> Boolean) {
    addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
        override fun onLayoutChange(view: View, left: Int, top: Int, right: Int, bottom: Int,
                                    oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
            if (onLayout(view)) {
                view.removeOnLayoutChangeListener(this)
            }
        }
    })
}


/**
 * Extension method to simplify view binding.
 */
fun <T : ViewDataBinding> View.bind() = DataBindingUtil.bind<T>(this) as T


/**
 * Extension method to provide quicker access to the [LayoutInflater] from a [View].
 */
fun View.getLayoutInflater() = context.getLayoutInflater()


/**
 * Transforms static java function Snackbar.make() to an extension function on View.
 */
fun View.showSnackbar(snackbarText: String, timeLength: Int) {
    Snackbar.make(this, snackbarText, timeLength).show()
}


/**
 * Extension method to update padding of view.
 *
 */
fun View.updatePadding(paddingStart: Int = getPaddingStart(),
                       paddingTop: Int = getPaddingTop(),
                       paddingEnd: Int = getPaddingEnd(),
                       paddingBottom: Int = getPaddingBottom()) {
    setPaddingRelative(paddingStart, paddingTop, paddingEnd, paddingBottom)
}


/**
 * Triggers a snackbar message when the value contained by snackbarTaskMessageLiveEvent is modified.
 */
fun View.setupSnackbar(lifecycleOwner: LifecycleOwner,
                       snackbarMessageLiveEvent: SingleLiveEvent<Int>, timeLength: Int) {
    snackbarMessageLiveEvent.observe(lifecycleOwner, Observer {
        it.let { showSnackbar(context.getString(it), timeLength) }
    })
}


//[Source](https://github.com/VictorChow/KotlinAndroidLib)

/**
 * Extension method to set View's left padding.
 */
fun View.setPaddingLeft(value: Int) = setPadding(value, paddingTop, paddingRight, paddingBottom)


//[Source](https://github.com/VictorChow/KotlinAndroidLib)

/**
 * Extension method to set View's right padding.
 */
fun View.setPaddingRight(value: Int) = setPadding(paddingLeft, paddingTop, value, paddingBottom)


//[Source](https://github.com/VictorChow/KotlinAndroidLib)

/**
 * Extension method to set View's top padding.
 */
fun View.setPaddingTop(value: Int) = setPaddingRelative(paddingStart, value, paddingEnd, paddingBottom)


//[Source](https://github.com/VictorChow/KotlinAndroidLib)

/**
 * Extension method to set View's bottom padding.
 */
fun View.setPaddingBottom(value: Int) = setPaddingRelative(paddingStart, paddingTop, paddingEnd, value)


//[Source](https://github.com/VictorChow/KotlinAndroidLib)

/**
 * Extension method to set View's start padding.
 */
fun View.setPaddingStart(value: Int) = setPaddingRelative(value, paddingTop, paddingEnd, paddingBottom)


//[Source](https://github.com/VictorChow/KotlinAndroidLib)

/**
 * Extension method to set View's end padding.
 */
fun View.setPaddingEnd(value: Int) = setPaddingRelative(paddingStart, paddingTop, value, paddingBottom)


//[Source](https://github.com/VictorChow/KotlinAndroidLib)

/**
 * Extension method to set View's horizontal padding.
 */
fun View.setPaddingHorizontal(value: Int) = setPaddingRelative(value, paddingTop, value, paddingBottom)


//[Source](https://github.com/VictorChow/KotlinAndroidLib)

/**
 * Extension method to set View's vertical padding.
 */
fun View.setPaddingVertical(value: Int) = setPaddingRelative(paddingStart, value, paddingEnd, value)


//[Source](https://github.com/VictorChow/KotlinAndroidLib)

/**
 * Extension method to set View's height.
 */
fun View.setHeight(value: Int) {
    val lp = layoutParams
    lp?.let {
        lp.height = value
        layoutParams = lp
    }
}


//[Source](https://github.com/VictorChow/KotlinAndroidLib)

/**
 * Extension method to set View's width.
 */
fun View.setWidth(value: Int) {
    val lp = layoutParams
    lp?.let {
        lp.width = value
        layoutParams = lp
    }
}


//[Source](https://github.com/VictorChow/KotlinAndroidLib)

/**
 * Extension method to resize View with height & width.
 */
fun View.resize(width: Int, height: Int) {
    val lp = layoutParams
    lp?.let {
        lp.width = width
        lp.height = height
        layoutParams = lp
    }
}


//[Source](https://github.com/VictorChow/KotlinAndroidLib)

/**
 * Extension method to access the view's children as a list
 */
val ViewGroup.children: List<View>
    get() = (0 until childCount).map { getChildAt(it) }

fun View.animateWidth(toValue: Int, duration: Long = DURATION, interpolator: Interpolator = INTERPOLATOR): AnimatePropsWrapper {
    if (toValue == width || layoutParams == null) {
        return AnimatePropsWrapper(null)
    }
    return AnimatePropsWrapper(ValueAnimator().apply {
        setIntValues(width, toValue)
        setDuration(duration)
        setInterpolator(interpolator)
        addUpdateListener {
            val lp = layoutParams
            lp.width = it.animatedValue as Int
            layoutParams = lp
        }
        start()
    })
}


//[Source](https://github.com/VictorChow/KotlinAndroidLib)

/**
 * Set an onclick listener
 */
fun <T : View> T.click(block: (T) -> Unit) = setOnClickListener { block(it as T) }


//[Source](https://github.com/VictorChow/KotlinAndroidLib)

/**
 * Extension method to set OnClickListener on a view.
 */
fun <T : View> T.longClick(block: (T) -> Boolean) = setOnLongClickListener { block(it as T) }


/**
 * Show the view  (visibility = View.VISIBLE)
 */
fun View.show(): View {
    if (visibility != View.VISIBLE) {
        visibility = View.VISIBLE
    }
    return this
}


//[Source](https://github.com/VictorChow/KotlinAndroidLib)

/**
 * Show the view if [condition] returns true
 * (visibility = View.VISIBLE)
 */
inline fun View.showIf(condition: () -> Boolean): View {
    if (visibility != View.VISIBLE && block()) {
        visibility = View.VISIBLE
    }
    return this
}


//[Source](https://github.com/VictorChow/KotlinAndroidLib)

/**
 * Hide the view. (visibility = View.INVISIBLE)
 */
fun View.hide(): View {
    if (visibility != View.INVISIBLE) {
        visibility = View.INVISIBLE
    }
    return this
}


//[Source](https://github.com/VictorChow/KotlinAndroidLib)

/**
 * Hide the view if [predicate] returns true
 * (visibility = View.INVISIBLE)
 */
inline fun View.hideIf(predicate: () -> Boolean): View {
    if (visibility != View.INVISIBLE && block()) {
        visibility = View.INVISIBLE
    }
    return this
}


//[Source](https://github.com/VictorChow/KotlinAndroidLib)

/**
 * Remove the view (visibility = View.GONE)
 */
fun View.remove(): View {
    if (visibility != View.GONE) {
        visibility = View.GONE
    }
    return this
}


//[Source](https://github.com/VictorChow/KotlinAndroidLib)

/**
 * Remove the view if [predicate] returns true
 * (visibility = View.GONE)
 */
inline fun View.removeIf(predicate: () -> Boolean): View {
    if (visibility != View.GONE && block()) {
        visibility = View.GONE
    }
    return this
}


/**
 * Toggle a view's visibility
 */
fun View.toggleVisibility(): View {
    if (visibility == View.VISIBLE) {
        visibility = View.INVISIBLE
    } else {
        visibility = View.INVISIBLE
    }
    return this
}


//[Source](https://github.com/VictorChow/KotlinAndroidLib)

/**
 * Extension method to animate View's width by value.
 */
fun View.animateWidthBy(byValue: Int, duration: Long = DURATION, interpolator: Interpolator = INTERPOLATOR) = animateWidth(width + byValue, duration, interpolator)


//[Source](https://github.com/VictorChow/KotlinAndroidLib)

/**
 * Extension method to animate ViewGroup's childs by height by value.
 */
fun View.animateHeight(toValue: Int, duration: Long = DURATION, interpolator: Interpolator = INTERPOLATOR): AnimatePropsWrapper {
    if (toValue == height || layoutParams == null) {
        return AnimatePropsWrapper(null)
    }
    return AnimatePropsWrapper(ValueAnimator().apply {
        setIntValues(height, toValue)
        setDuration(duration)
        setInterpolator(interpolator)
        addUpdateListener {
            val lp = layoutParams
            lp.height = it.animatedValue as Int
            layoutParams = lp
        }
        start()
    })
}


//[Source](https://github.com/VictorChow/KotlinAndroidLib)

/**
 * Extension method to animate View's width by value.
 */
fun View.animateHeightBy(byValue: Int, duration: Long = DURATION, interpolator: Interpolator = INTERPOLATOR) = animateHeight(height + byValue, duration, interpolator)


//[Source](https://github.com/VictorChow/KotlinAndroidLib)

/**
 * Animate the view
 */
fun View.animateX(toValue: Float, duration: Long = DURATION, interpolator: Interpolator = INTERPOLATOR): AnimatePropsWrapper {
    if (toValue == translationX) {
        return AnimatePropsWrapper(null)
    }
    return AnimatePropsWrapper(ValueAnimator().apply {
        setFloatValues(translationX, toValue)
        setDuration(duration)
        setInterpolator(interpolator)
        addUpdateListener { this@animateX.translationX = it.animatedValue as Float }
        start()
    })
}


//[Source](https://github.com/VictorChow/KotlinAndroidLib)

/**
 * Animate the view
 */
fun View.animateXBy(toValue: Float, duration: Long = DURATION, interpolator: Interpolator = INTERPOLATOR) = animateX(translationX + toValue, duration, interpolator)


//[Source](https://github.com/VictorChow/KotlinAndroidLib)

/**
 * Extension method to animate a view's height.
 */
fun View.animateY(toValue: Float, duration: Long = DURATION, interpolator: Interpolator = INTERPOLATOR): AnimatePropsWrapper {
    if (toValue == translationY) {
        return AnimatePropsWrapper(null)
    }
    return AnimatePropsWrapper(ValueAnimator().apply {
        setFloatValues(translationY, toValue)
        setDuration(duration)
        setInterpolator(interpolator)
        addUpdateListener { this@animateY.translationY = it.animatedValue as Float }
        start()
    })
}


//[Source](https://github.com/VictorChow/KotlinAndroidLib)

/**
 * Extension method to animate a view's height by a value.
 */
fun View.animateYBy(toValue: Float, duration: Long = DURATION, interpolator: Interpolator = INTERPOLATOR) = animateY(translationY + toValue, duration, interpolator)


//[Source](https://github.com/VictorChow/KotlinAndroidLib)

/**
 * Extension method to get a view as bitmap.
 */
fun View.getBitmap(): Bitmap {
    val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmp)
    draw(canvas)
    canvas.save()
    return bmp
}


/**
 * Show a snackbar with [message]
 */
fun View.snack(message: String, length: Int = Snackbar.LENGTH_LONG) = snack(message, length) {}


/**
 * Show a snackbar with [messageRes]
 */
fun View.snack(@StringRes messageRes: Int, length: Int = Snackbar.LENGTH_LONG) = snack(messageRes, length) {}


/**
 * Show a snackbar with [message], execute [f] and show it
 */
inline fun View.snack(message: String, @BaseTransientBottomBar.Duration length: Int = Snackbar.LENGTH_LONG, f: Snackbar.() -> Unit) {
    val snack = Snackbar.make(this, message, length)
    snack.f()
    snack.show()
}


/**
 * Show a snackbar with [messageRes], execute [f] and show it
 */
inline fun View.snack(@StringRes messageRes: Int, @BaseTransientBottomBar.Duration length: Int = Snackbar.LENGTH_LONG, f: Snackbar.() -> Unit) {
    val snack = Snackbar.make(this, messageRes, length)
    snack.f()
    snack.show()
}


/**
 * Find a parent of type [parentType], assuming it exists
 */
tailrec fun <T : View> View.findParent(parentType: Class<T>): T {
    return if (parent.javaClass == parentType) parent as T else (parent as View).findParent(parentType)
}


/**
 * Like findViewById but with type interference, assume the view exists
 */
inline fun <reified T : View> View.find(@IdRes id: Int): T = findViewById(id) as T


/**
 *  Like findViewById but with type interference, or null if not found
 */
inline fun <reified T : View> View.findOptional(@IdRes id: Int): T? = findViewById(id) as? T


//## Context


/**
 * Extension method to provide simpler access to {@link ContextCompat#getColor(int)}.
 */
fun Context.getColorCompat(color: Int) = ContextCompat.getColor(this, color)


/**
 * Extension method to find a device width in pixels
 */
inline val Context.displayWidth: Int
    get() = resources.displayMetrics.widthPixels


/**
 * Extension method to find a device height in pixels
 */
inline val Context.displayHeight: Int
    get() = resources.displayMetrics.heightPixels


/**
 * Extension method to get displayMetrics in Context.displayMetricks
 */
inline val Context.displayMetricks: DisplayMetrics
    get() = resources.displayMetrics


/**
 * Extension method to get LayoutInflater
 */
inline val Context.inflater: LayoutInflater
    get() = LayoutInflater.from(this)


/**
 * Extension method to get a new Intent for an Activity class
 */
inline fun <reified T : Any> Context.intent() = Intent(this, T::class.java)


/**
 * Create an intent for [T] and apply a lambda on it
 */
inline fun <reified T : Any> Context.intent(body: Intent.() -> Unit): Intent {
    val intent = Intent(this, T::class.java)
    intent.body()
    return intent
}


/**
 * Extension method to startActivity for Context.
 */
inline fun <reified T : Activity> Context?.startActivity() = this?.startActivity(Intent(this, T::class.java))


/**
 * Extension method to start Service for Context.
 */
inline fun <reified T : Service> Context?.startService() = this?.startService(Intent(this, T::class.java))


/**
 * Extension method to startActivity with Animation for Context.
 */
inline fun <reified T : Activity> Context.startActivityWithAnimation(enterResId: Int = 0, exitResId: Int = 0) {
    val intent = Intent(this, T::class.java)
    val bundle = ActivityOptionsCompat.makeCustomAnimation(this, enterResId, exitResId).toBundle()
    ContextCompat.startActivity(this, intent, bundle)
}


/**
 * Extension method to startActivity with Animation for Context.
 */
inline fun <reified T : Activity> Context.startActivityWithAnimation(enterResId: Int = 0, exitResId: Int = 0, intentBody: Intent.() -> Unit) {
    val intent = Intent(this, T::class.java)
    intent.intentBody()
    val bundle = ActivityOptionsCompat.makeCustomAnimation(this, enterResId, exitResId).toBundle()
    ContextCompat.startActivity(this, intent, bundle)
}

/**
 * Kotlin Extensions for simpler, easier and funw way
 * of launching of Activities
 */

inline fun <reified T : Any> Activity.launchActivity (
        requestCode: Int = -1,
        options: Bundle? = null,
        noinline init: Intent.() -> Unit = {})
{
    val intent = newIntent<T>(this)
    intent.init()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
    {
        startActivityForResult(intent, requestCode, options)
    } else {
        startActivityForResult(intent, requestCode)
    }
}

inline fun <reified T : Any> Context.launchActivity (
        options: Bundle? = null,
        noinline init: Intent.() -> Unit = {})
{
    val intent = newIntent<T>(this)
    intent.init()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
    {
        startActivity(intent, options)
    } else {
        startActivity(intent)
    }
}

inline fun <reified T : Any> newIntent(context: Context): Intent =
        Intent(context, T::class.java)

// Simple Activities
launchActivity<UserDetailActivity>()

// Add Intent extras
launchActivity<UserDetailActivity> {
    putExtra(INTENT_USER_ID, user.id)
}

// Add custom flags
launchActivity<UserDetailActivity> {
    putExtra(INTENT_USER_ID, user.id)
    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
}

// Add Shared Transistions
val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, avatar, "avatar")
launchActivity<UserDetailActivity>(options = options) {
    putExtra(INTENT_USER_ID, user.id)
}

// Add requestCode for startActivityForResult() call
launchActivity<UserDetailActivity>(requestCode = 1234) {
    putExtra(INTENT_USER_ID, user.id)
}

/**
 * Extension method to show toast for Context.
 */
fun Context?.toast(text: CharSequence, duration: Int = Toast.LENGTH_LONG) = this?.let { Toast.makeText(it, text, duration).show() }


/**
 * Extension method to show toast for Context.
 */
fun Context?.toast(@StringRes textId: Int, duration: Int = Toast.LENGTH_LONG) = this?.let { Toast.makeText(it, textId, duration).show() }


/**
 * Extension method to Get Integer resource for Context.
 */
fun Context.getInteger(@IntegerRes id: Int) = resources.getInteger(id)


/**
 * Extension method to Get Boolean resource for Context.
 */
fun Context.getBoolean(@BoolRes id: Int) = resources.getBoolean(id)


/**
 * Extension method to Get Color for resource for Context.
 */
fun Context.getColor(@ColorRes id: Int) = ContextCompat.getColor(this, id)


/**
 * Extension method to Get Drawable for resource for Context.
 */
fun Context.getDrawable(@DrawableRes id: Int) = ContextCompat.getDrawable(this, id)


/**
 * InflateLayout
 */
fun Context.inflateLayout(@LayoutRes layoutId: Int, parent: ViewGroup? = null, attachToRoot: Boolean = false): View = LayoutInflater.from(this).inflate(layoutId, parent, attachToRoot)


/**
 * Extension method to get inputManager for Context.
 */
inline val Context.inputManager: InputMethodManager?
    get() = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager


/**
 * Extension method to get notificationManager for Context.
 */
inline val Context.notificationManager: NotificationManager?
    get() = getSystemService(NOTIFICATION_SERVICE) as? NotificationManager


/**
 * Extension method to get keyguardManager for Context.
 */
inline val Context.keyguardManager: KeyguardManager?
    get() = getSystemService(KEYGUARD_SERVICE) as? KeyguardManager


/**
 * Extension method to get telephonyManager for Context.
 */
inline val Context.telephonyManager: TelephonyManager?
    get() = getSystemService(TELEPHONY_SERVICE) as? TelephonyManager


/**
 * Extension method to get devicePolicyManager for Context.
 */
inline val Context.devicePolicyManager: DevicePolicyManager?
    get() = getSystemService(DEVICE_POLICY_SERVICE) as? DevicePolicyManager


/**
 * Extension method to get connectivityManager for Context.
 */
inline val Context.connectivityManager: ConnectivityManager?
    get() = getSystemService(CONNECTIVITY_SERVICE) as? ConnectivityManager


/**
 * Extension method to get alarmManager for Context.
 */
inline val Context.alarmManager: AlarmManager?
    get() = getSystemService(ALARM_SERVICE) as? AlarmManager


/**
 * Extension method to get clipboardManager for Context.
 */
inline val Context.clipboardManager: ClipboardManager?
    get() = getSystemService(CLIPBOARD_SERVICE) as? ClipboardManager


/**
 * Extension method to get jobScheduler for Context.
 */
inline val Context.jobScheduler: JobScheduler?
    get() = getSystemService(JOB_SCHEDULER_SERVICE) as? JobScheduler


/**
 * Extension method to show notification for Context.
 */
inline fun Context.notification(body: NotificationCompat.Builder.() -> Unit): Notification {
    val builder = NotificationCompat.Builder(this)
    builder.body()
    return builder.build()
}


/**
 * Extension method to browse for Context.
 */
fun Context.browse(url: String, newTask: Boolean = false): Boolean {
    try {
        val intent = Intent(ACTION_VIEW).apply {
            data = Uri.parse(url)
            if (newTask) addFlags(FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
        return true
    } catch (e: Exception) {
        return false
    }
}


/**
 * Extension method to share for Context.
 */
fun Context.share(text: String, subject: String = ""): Boolean {
    val intent = Intent()
    intent.type = "text/plain"
    intent.putExtra(EXTRA_SUBJECT, subject)
    intent.putExtra(EXTRA_TEXT, text)
    try {
        startActivity(createChooser(intent, null))
        return true
    } catch (e: ActivityNotFoundException) {
        return false
    }
}


/**
 * Extension method to send email for Context.
 */
fun Context.email(email: String, subject: String = "", text: String = ""): Boolean {
    val intent = intent(ACTION_SENDTO) {
        data = Uri.parse("mailto:")
        putExtra(EXTRA_EMAIL, arrayOf(email))
        if (subject.isNotBlank()) putExtra(EXTRA_SUBJECT, subject)
        if (text.isNotBlank()) putExtra(EXTRA_TEXT, text)
    }
    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
        return true
    }
    return false
}


/**
 * Extension method to make call for Context.
 */
fun Context.makeCall(number: String): Boolean {
    try {
        val intent = Intent(ACTION_CALL, Uri.parse("tel:$number"))
        startActivity(intent)
        return true
    } catch (e: Exception) {
        return false
    }
}


/**
 * Extension method to Send SMS for Context.
 */
fun Context.sendSms(number: String, text: String = ""): Boolean {
    try {
        val intent = intent(ACTION_VIEW, Uri.parse("sms:$number")) {
            putExtra("sms_body", text)
        }
        startActivity(intent)
        return true
    } catch (e: Exception) {
        return false
    }
}


/**
 * Extension method to rate app on PlayStore for Context.
 */
fun Context.rate(): Boolean = browse("market://details?id=$packageName") or browse("http://play.google.com/store/apps/details?id=$packageName")


/**
 * Extension method to provide quicker access to the [LayoutInflater] from [Context].
 */
fun Context.getLayoutInflater() = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


//[Source](https://github.com/VictorChow/KotlinAndroidLib)


/**
 * Extension method to send sms for Context.
 */
fun Context.sms(phone: String?, body: String = "") {
    val smsToUri = Uri.parse("smsto:" + phone)
    val intent = Intent(Intent.ACTION_SENDTO, smsToUri)
    intent.putExtra("sms_body", body)
    startActivity(intent)
}


//[Source](https://github.com/VictorChow/KotlinAndroidLib)


/**
 * Extension method to dail telephone number for Context.
 */
fun Context.dial(tel: String?) = startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tel)))


//## Fragment


/**
 * Extension method to display toast text for Fragment.
 */
fun Fragment?.toast(text: CharSequence, duration: Int = Toast.LENGTH_LONG) = this?.let { activity.toast(text, duration) }


/**
 * Extension method to display toast text for Fragment.
 */
fun Fragment?.toast(@StringRes textId: Int, duration: Int = Toast.LENGTH_LONG) = this?.let { activity.toast(textId, duration) }


/**
 * Extension method to display toast text for SupportFragment.
 */
fun SupportFragment?.toast(text: CharSequence, duration: Int = Toast.LENGTH_LONG) = this?.let { activity.toast(text, duration) }


/**
 * Extension method to display toast text for SupportFragment.
 */
fun SupportFragment?.toast(@StringRes textId: Int, duration: Int = Toast.LENGTH_LONG) = this?.let { activity.toast(textId, duration) }


/**
 * Extension method to display notification text for Fragment.
 */
inline fun Fragment.notification(body: NotificationCompat.Builder.() -> Unit) = activity.notification(body)


/**
 * Extension method to display notification text for SupportFragment.
 */
inline fun SupportFragment.notification(body: NotificationCompat.Builder.() -> Unit) = activity.notification(body)


/**
 * Extension method to browse url text for Fragment.
 */
fun Fragment.browse(url: String, newTask: Boolean = false) = activity.browse(url, newTask)


/**
 * Extension method to browse url text for SupportFragment.
 */
fun SupportFragment.browse(url: String, newTask: Boolean = false) = activity.browse(url, newTask)


/**
 * Extension method to share text for Fragment.
 */
fun Fragment.share(text: String, subject: String = "") = activity.share(text, subject)


/**
 * Extension method to share text for SupportFragment.
 */
fun SupportFragment.share(text: String, subject: String = "") = activity.share(text, subject)


/**
 * Extension method to send email for Fragment.
 */
fun Fragment.email(email: String, subject: String = "", text: String = "") = activity.email(email, subject, text)


/**
 * Extension method to send email for SupportFragment.
 */
fun SupportFragment.email(email: String, subject: String = "", text: String = "") = activity.email(email, subject, text)


/**
 * Extension method to make call for Fragment.
 */
fun Fragment.makeCall(number: String) = activity.makeCall(number)


/**
 * Extension method to make call for SupportFragment.
 */
fun SupportFragment.makeCall(number: String) = activity.makeCall(number)


/**
 * Extension method to send sms for Fragment.
 */
fun Fragment.sendSms(number: String, text: String = "") = activity.sendSms(number, text)


/**
 * Extension method to send sms for SupportFragment.
 */
fun SupportFragment.sendSms(number: String, text: String = "") = activity.sendSms(number, text)


/**
 * Extension method to rate in playstore for Fragment.
 */
fun Fragment.rate() = activity.rate()


/**
 * Extension method to rate in playstore for SupportFragment.
 */
fun SupportFragment.rate() = activity.rate()


/**
 * Extension method to provide hide keyboard for [Fragment].
 */
fun Fragment.hideSoftKeyboard() {
    activity?.hideSoftKeyboard()
}


//## Activity


/**
 * Extension method to provide hide keyboard for [Activity].
 */
fun Activity.hideSoftKeyboard() {
    if (currentFocus != null) {
        val inputMethodManager = getSystemService(Context
                .INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }
}


/**
 * The `fragment` is added to the container view with id `frameId`. The operation is
 * performed by the `fragmentManager`.
 */
fun AppCompatActivity.replaceFragmentInActivity(fragment: Fragment, @IdRes frameId: Int) {
    supportFragmentManager.transact {
        replace(frameId, fragment)
    }
}


/**
 * The `fragment` is added to the container view with tag. The operation is
 * performed by the `fragmentManager`.
 */
fun AppCompatActivity.addFragmentToActivity(fragment: Fragment, tag: String) {
    supportFragmentManager.transact {
        add(fragment, tag)
    }
}


/**
 * Setup actionbar
 */
fun AppCompatActivity.setupActionBar(@IdRes toolbarId: Int, action: ActionBar.() -> Unit) {
    setSupportActionBar(findViewById(toolbarId))
    supportActionBar?.run {
        action()
    }
}


/**
 * Extension method to get ContentView for ViewGroup.
 */
fun Activity.getContentView(): ViewGroup {
    return this.findViewById(android.R.id.content) as ViewGroup
}


//## ViewGroup


/**
 * Extension method to inflate layout for ViewGroup.
 */
fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}


/**
 * Extension method to get views by tag for ViewGroup.
 */
fun ViewGroup.getViewsByTag(tag: String): ArrayList<View> {
    val views = ArrayList<View>()
    val childCount = childCount
    for (i in 0..childCount - 1) {
        val child = getChildAt(i)
        if (child is ViewGroup) {
            views.addAll(child.getViewsByTag(tag))
        }

        val tagObj = child.tag
        if (tagObj != null && tagObj == tag) {
            views.add(child)
        }

    }
    return views
}


/**
 * Extension method to remove views by tag ViewGroup.
 */
fun ViewGroup.removeViewsByTag(tag: String) {
    for (i in 0..childCount - 1) {
        val child = getChildAt(i)
        if (child is ViewGroup) {
            child.removeViewsByTag(tag)
        }

        if (child.tag == tag) {
            removeView(child)
        }
    }
}


/**
 * Extension method to simplify view inflating and binding inside a [ViewGroup].
 *
 * e.g.
 * This:
 *<code>
 *     binding = bind(R.layout.widget_card)
 *</code>
 *
 * Will replace this:
 *<code>
 *     binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.widget_card, this, true)
 *</code>
 */
fun <T : ViewDataBinding> ViewGroup.bind(layoutId: Int): T {
    return DataBindingUtil.inflate(getLayoutInflater(), layoutId, this, true)
}


//## TextView

//[Source](https://github.com/VictorChow/KotlinAndroidLib)


/**
 * Extension method underLine for TextView.
 */
fun TextView.underLine() {
    paint.flags = paint.flags or Paint.UNDERLINE_TEXT_FLAG
    paint.isAntiAlias = true
}


//[Source](https://github.com/VictorChow/KotlinAndroidLib)


/**
 * Extension method deleteLine for TextView.
 */
fun TextView.deleteLine() {
    paint.flags = paint.flags or Paint.STRIKE_THRU_TEXT_FLAG
    paint.isAntiAlias = true
}


//[Source](https://github.com/VictorChow/KotlinAndroidLib)


/**
 * Extension method bold for TextView.
 */
fun TextView.bold() {
    paint.isFakeBoldText = true
    paint.isAntiAlias = true
}


/**
 * Extension method to set different color for substring TextView.
 */
fun TextView.setColorOfSubstring(substring: String, color: Int) {
    try {
        val spannable = android.text.SpannableString(text)
        val start = text.indexOf(substring)
        spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, color)), start, start + substring.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        text = spannable
    } catch (e: Exception) {
        Log.d("ViewExtensions", "exception in setColorOfSubstring, text=$text, substring=$substring", e)
    }
}


/**
 * Extension method to set font for TextView.
 */
fun TextView.font(font: String) {
    typeface = Typeface.createFromAsset(context.assets, "asset_fonts/$font.ttf")
}


/**
 * Extension method to set a drawable to the left of a TextView.
 */
fun TextView.setDrawableLeft(drawable: Int) {
    this.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0)
}


//## String

//[Source](https://github.com/VictorChow/KotlinAndroidLib)


/**
 * Extension method to show toast for String.
 */
fun String.toast(isShortToast: Boolean = true) = toast(this, isShortToast)


//[Source](https://github.com/VictorChow/KotlinAndroidLib)


/**
 * Extension method to get md5 string.
 */
fun String.md5() = encrypt(this, "MD5")


//[Source](https://github.com/VictorChow/KotlinAndroidLib)


/**
 * Extension method to get sha1 string.
 */
fun String.sha1() = encrypt(this, "SHA-1")


//[Source](https://github.com/VictorChow/KotlinAndroidLib)


/**
 * Extension method to check if String is Phone Number.
 */
fun String.isPhone(): Boolean {
    val p = "^1([34578])\\d{9}\$".toRegex()
    return matches(p)
}


//[Source](https://github.com/VictorChow/KotlinAndroidLib)


/**
 * Extension method to check if String is Email.
 */
fun String.isEmail(): Boolean {
    val p = "^(\\w)+(\\.\\w+)*@(\\w)+((\\.\\w+)+)\$".toRegex()
    return matches(p)
}


//[Source](https://github.com/VictorChow/KotlinAndroidLib)


/**
 * Extension method to check if String is Number.
 */
fun String.isNumeric(): Boolean {
    val p = "^[0-9]+$".toRegex()
    return matches(p)
}


//[Source](https://github.com/VictorChow/KotlinAndroidLib)


/**
 * Extension method to check String equalsIgnoreCase
 */
fun String.equalsIgnoreCase(other: String) = this.toLowerCase().contentEquals(other.toLowerCase())


//[Source](https://github.com/VictorChow/KotlinAndroidLib)


/**
 * Extension method to get encrypted string.
 */
private fun encrypt(string: String?, type: String): String {
    if (string.isNullOrEmpty()) {
        return ""
    }
    val md5: MessageDigest
    return try {
        md5 = MessageDigest.getInstance(type)
        val bytes = md5.digest(string?.toByteArray())
        bytes2Hex(bytes)
    } catch (e: NoSuchAlgorithmException) {
        ""
    }
}


//[Source](https://github.com/VictorChow/KotlinAndroidLib)


/**
 * Extension method to convert byteArray to String.
 */
private fun bytes2Hex(bts: ByteArray): String {
    var des = ""
    var tmp: String
    for (i in bts.indices) {
        tmp = Integer.toHexString(bts[i].toInt() and 0xFF)
        if (tmp.length == 1) {
            des += "0"
        }
        des += tmp
    }
    return des
}



## Other



/**
 * Extension method to cast a char with a decimal value to an [Int].
 */
fun Char.decimalValue(): Int {
    if (!isDigit())
        throw IllegalArgumentException("Out of range")
    return this.toInt() - '0'.toInt()
}


/**
 * Extension method to simplify the code needed to apply spans on a specific sub string.
 */
inline fun SpannableStringBuilder.withSpan(vararg spans: Any, action: SpannableStringBuilder.() -> Unit):
        SpannableStringBuilder {
    val from = length
    action()

    for (span in spans) {
        setSpan(span, from, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    return this
}


/**
 * Extension method to int time to 2 digit String
 */
fun Int.twoDigitTime() = if (this < 10) "0" + toString() else toString()


/**
 * Extension method to replace all text inside an [Editable] with the specified [newValue].
 */
fun Editable.replaceAll(newValue: String) {
    replace(0, length, newValue)
}


/**
 * Extension method to replace all text inside an [Editable] with the specified [newValue] while
 * ignoring any [android.text.InputFilter] set on the [Editable].
 */
fun Editable.replaceAllIgnoreFilters(newValue: String) {
    val currentFilters = filters
    filters = emptyArray()
    replaceAll(newValue)
    filters = currentFilters
}


/**
 * Extension method to get Date for String with specified format.
 */
fun String.dateInFormat(format: String): Date? {
    val dateFormat = SimpleDateFormat(format, Locale.getDefault())
    var parsedDate: Date? = null
    try {
        parsedDate = dateFormat.parse(this)
    } catch (ignored: ParseException) {
        ignored.printStackTrace()
    }
    return parsedDate
}


/**
 * Extension method to get ClickableSpan.
 * e.g.
 * val loginLink = getClickableSpan(context.getColorCompat(R.color.colorAccent), { })
 */
fun getClickableSpan(color: Int, action: (view: View) -> Unit): ClickableSpan {
    return object : ClickableSpan() {
        override fun onClick(view: View) {
            action(view)
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.color = color
        }
    }
}


/**
 * Extension method to load imageView from url.
 */
fun ImageView.loadFromUrl(imageUrl: String) {
    Glide.with(this).load(imageUrl).into(this)
}


/**
 * Extension method to load icon from url.
 */
fun MenuItem.loadIconFromUrl(context: Context, imageUrl: String) {
    Glide.with(context).asBitmap()
            .load(imageUrl)
            .into(object : SimpleTarget<Bitmap>(100, 100) {
                override fun onResourceReady(resource: Bitmap?, transition: Transition<in Bitmap>?) {
                    val circularIcon = RoundedBitmapDrawableFactory.create(context.resources, resource)
                    circularIcon.isCircular = true
                    icon = circularIcon
                }
            })
}


/**
 * Extension method to write preferences.
 */
inline fun SharedPreferences.edit(preferApply: Boolean = false, f: SharedPreferences.Editor.() -> Unit) {
    val editor = edit()
    editor.f()
    if (preferApply) editor.apply() else editor.commit()
}


/**
 * Runs a FragmentTransaction, then calls commit().
 */
private inline fun FragmentManager.transact(action: FragmentTransaction.() -> Unit) {
    beginTransaction().apply {
        action()
    }.commit()
}


/**
 * Get view model for Activity
 */
fun <T : ViewModel> AppCompatActivity.obtainViewModel(viewModelClass: Class<T>) =
        ViewModelProviders.of(this, ViewModelFactory.getInstance(application)).get(viewModelClass)


//[Source](https://github.com/VictorChow/KotlinAndroidLib)


/**
 * Extension method to get value from EditText.
 */
val EditText.value
    get() = text.toString()


//[Source](https://github.com/VictorChow/KotlinAndroidLib)


/**
 * Extension method to check is aboveApi.
 */
inline fun aboveApi(api: Int, included: Boolean = false, block: () -> Unit) {
    if (Build.VERSION.SDK_INT > if (included) api - 1 else api) {
        block()
    }
}


//[Source](https://github.com/VictorChow/KotlinAndroidLib)


/**
 * Extension method to check is belowApi.
 */
inline fun belowApi(api: Int, included: Boolean = false, block: () -> Unit) {
    if (Build.VERSION.SDK_INT < if (included) api + 1 else api) {
        block()
    }
}


//[Source](https://github.com/VictorChow/KotlinAndroidLib)


/**
 * Extension method to get base64 string for Bitmap.
 */
fun Bitmap.toBase64(): String {
    var result = ""
    val baos = ByteArrayOutputStream()
    try {
        compress(Bitmap.CompressFormat.JPEG, 100, baos)
        baos.flush()
        baos.close()
        val bitmapBytes = baos.toByteArray()
        result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT)
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        try {
            baos.flush()
            baos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return result
}


//[Source](https://github.com/VictorChow/KotlinAndroidLib)


/**
 * Extension method to resize Bitmap to specified height and width.
 */
fun Bitmap.resize(w: Number, h: Number): Bitmap {
    val width = width
    val height = height
    val scaleWidth = w.toFloat() / width
    val scaleHeight = h.toFloat() / height
    val matrix = Matrix()
    matrix.postScale(scaleWidth, scaleHeight)
    if (width > 0 && height > 0) {
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }
    return this
}


//[Source](https://github.com/VictorChow/KotlinAndroidLib)


/**
 * Extension method to save Bitmap to specified file path.
 */
fun Bitmap.saveFile(path: String) {
    val f = File(path)
    if (!f.exists()) {
        f.createNewFile()
    }
    val stream = FileOutputStream(f)
    compress(Bitmap.CompressFormat.PNG, 100, stream)
    stream.flush()
    stream.close()
}


//[Source](https://github.com/VictorChow/KotlinAndroidLib)


/**
 * Extension method to find color based on color resource.
 */
fun findColor(@ColorRes resId: Int) = ContextCompat.getColor(Ext.ctx, resId)


//[Source](https://github.com/VictorChow/KotlinAndroidLib)


/**
 * Extension method to find drawable based on drawable resource.
 */
fun findDrawable(@DrawableRes resId: Int): Drawable? = ContextCompat.getDrawable(Ext.ctx, resId)


//[Source](https://github.com/VictorChow/KotlinAndroidLib)


/**
 * Extension method to find ColorStateList.
 */
fun findColorStateList(@ColorRes resId: Int): ColorStateList? = ContextCompat.getColorStateList(Ext.ctx, resId)


//[Source](https://github.com/VictorChow/KotlinAndroidLib)


/**
 * Extension method to layout based on Layout Resource Id aot the parent ViewGroup
 */
fun inflate(@LayoutRes layoutId: Int, parent: ViewGroup?, attachToRoot: Boolean = false) = LayoutInflater.from(Ext.ctx).inflate(layoutId, parent, attachToRoot)


//[Source](https://github.com/VictorChow/KotlinAndroidLib)


/**
 * Extension method to layout based on Layout Resource Id.
 */
fun inflate(@LayoutRes layoutId: Int) = inflate(layoutId, null)


//[Source](https://github.com/VictorChow/KotlinAndroidLib)


/**
 * Extension method check if is Main Thread.
 */
fun isMainThread(): Boolean = Looper.myLooper() == Looper.getMainLooper()


//[Source](https://github.com/VictorChow/KotlinAndroidLib)


/**
 * Extension method to provide Date related functions.
 */
private enum class DateExpr {
    YEAR, MONTH, DAY,
    HOUR, MINUTE, SECOND,
    WEEK, DAY_YEAR, WEEK_YEAR,
    CONSTELLATION
}

fun Long.date(pattern: String = "yyyy-MM-dd HH:mm:ss"): String? = SimpleDateFormat(pattern, Locale.getDefault()).format(this)

fun Long.year() = getData(this, DateExpr.YEAR)

fun Long.month() = getData(this, DateExpr.MONTH)

fun Long.day() = getData(this, DateExpr.DAY)

fun Long.week() = getData(this, DateExpr.WEEK)

fun Long.hour() = getData(this, DateExpr.HOUR)

fun Long.minute() = getData(this, DateExpr.MINUTE)

fun Long.second() = getData(this, DateExpr.SECOND)

fun Long.dayOfYear() = getData(this, DateExpr.DAY_YEAR)

fun Long.weekOfYear() = getData(this, DateExpr.WEEK_YEAR)

fun Long.constellation() = getData(this, DateExpr.CONSTELLATION)

fun Int.isLeapYear() = (this % 4 == 0) && (this % 100 != 0) || (this % 400 == 0)


//[Source](https://github.com/VictorChow/KotlinAndroidLib)


/**
 * Extension method to get connectivityManager for Context.
 */
inline val connectivityManager: ConnectivityManager
    get() = Ext.ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager


//[Source](https://github.com/VictorChow/KotlinAndroidLib)


/**
 * Extension method to get alarmManager for Context.
 */
inline val alarmManager: AlarmManager
    get() = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager


//[Source](https://github.com/VictorChow/KotlinAndroidLib)


/**
 * Extension method to get telephonyManager for Context.
 */
inline val telephonyManager: TelephonyManager
    get() = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager


//[Source](https://github.com/VictorChow/KotlinAndroidLib)


/**
 * Extension method to get activityManager for Context.
 */
inline val activityManager: ActivityManager
    get() = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager


//[Source](https://github.com/VictorChow/KotlinAndroidLib)


/**
 * Extension method to get notificationManager for Context.
 */
inline val notificationManager: NotificationManager
    get() = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


//[Source](https://github.com/VictorChow/KotlinAndroidLib)


/**
 * Extension method to get appWidgetManager for Context.
 */
inline val appWidgetManager
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    get() = context.getSystemService(Context.APPWIDGET_SERVICE) as AppWidgetManager


//[Source](https://github.com/VictorChow/KotlinAndroidLib)


/**
 * Extension method to get inputMethodManager for Context.
 */
inline val inputMethodManager: InputMethodManager
    get() = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager


//[Source](https://github.com/VictorChow/KotlinAndroidLib)


/**
 * Extension method to get clipboardManager for Context.
 */
inline val clipboardManager
    get() = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager


//[Source](https://github.com/VictorChow/KotlinAndroidLib)


/**
 * Extension method to get bluetoothManager for Context.
 */
inline val bluetoothManager: BluetoothManager
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    get() = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager


//[Source](https://github.com/VictorChow/KotlinAndroidLib)


/**
 * Extension method to get audioManager for Context.
 */
inline val audioManager
    get() = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager


//[Source](https://github.com/VictorChow/KotlinAndroidLib)


/**
 * Extension method to get batteryManager for Context.
 */
inline val batteryManager
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    get() = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager


//[Source](https://github.com/VictorChow/KotlinAndroidLib)


/**
 * Extension method to get SpannableString with specific size text from start to end.
 */
fun spannableSize(text: String, textSize: Int, isDip: Boolean, start: Int, end: Int): SpannableString {
    val sp = SpannableString(text)
    sp.setSpan(AbsoluteSizeSpan(textSize, isDip), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
    return sp
}


//[Source](https://github.com/VictorChow/KotlinAndroidLib)


/**
 * Extension method to get SpannableString with Bold text from start to end.
 */
fun spannableBold(text: String, start: Int, end: Int): SpannableString {
    val sp = SpannableString(text)
    sp.setSpan(StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
    return sp
}


//[Source](https://github.com/VictorChow/KotlinAndroidLib)


/**
 * Extension method to get SpannableString with text with color resource from start to end.
 */
fun spannableColor(text: String, @ColorRes colorId: Int, start: Int, end: Int): SpannableString {
    val sp = SpannableString(text)
    sp.setSpan(ForegroundColorSpan(findColor(colorId)), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
    return sp
}


/**
 * Extension method to provide handler and mainThread.
 */
private object ContextHandler {
    val handler = Handler(Looper.getMainLooper())
    val mainThread = Looper.getMainLooper().thread
}


/**
 * Extension method to run block of code on UI Thread.
 */
fun runOnUiThread(action: () -> Unit) {
    if (ContextHandler.mainThread == Thread.currentThread()) action() else ContextHandler.handler.post { action() }
}


/**
 * Extension method to run block of code after specific Delay.
 */
fun runDelayed(delay: Long, timeUnit: TimeUnit = MILLISECONDS, action: () -> Unit) {
    Handler().postDelayed(action, timeUnit.toMillis(delay))
}


/**
 * Extension method to run block of code on UI Thread after specific Delay.
 */
fun runDelayedOnUiThread(delay: Long, timeUnit: TimeUnit = MILLISECONDS, action: () -> Unit) {
    ContextHandler.handler.postDelayed(action, timeUnit.toMillis(delay))
}


/**
 * Extension method to show Snackbar with color and action lambda.
 */
fun Snackbar.action(text: String, @ColorRes color: Int? = null, listener: (View) -> Unit) {
    setAction(text, listener)
    color?.let { setActionTextColor(color) }
}


/**
 * Extension method to get the TAG name for all object
 */
fun <T : Any> T.TAG() = this::class.simpleName

*/
package com.nekosoft.brokenglass.base

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

class SingleLiveEvent<T> : MutableLiveData<T>() {
  private val pending = AtomicBoolean(false)

  @MainThread
  override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
    if (hasActiveObservers()) {
      Log.w(TAG, "Multiple observers registered but only one will be notified of changes.")
    }

    super.observe(
      owner
    ) { t ->
      if (pending.compareAndSet(true, false)) {
        observer.onChanged(t)
      }
    }
  }

  @MainThread
  override fun setValue(t: T?) {
    pending.set(true)
    super.setValue(t)
  }

  /**
   * Used for cases where T is Void, to make calls cleaner.
   */
  @MainThread
  fun call() {
    postValue(null)
  }

  companion object {
    private const val TAG = "SingleLiveEvent"
  }
}
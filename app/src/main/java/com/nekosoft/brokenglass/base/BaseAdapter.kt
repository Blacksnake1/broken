package com.nekosoft.brokenglass.base

import android.annotation.SuppressLint
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.Executors


const val PAYLOAD_VISIBLE_CHECKBOX = "visible"
const val PAYLOAD_CHECKED = "checked"
const val PAYLOAD_SEARCH = "search"
const val PAYLOAD_IMAGE = "image"

abstract class BaseAdapter<T : Any, VH : RecyclerView.ViewHolder?>
    : ListAdapter<T, VH>(
    AsyncDifferConfig.Builder(object : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
            return if (oldItem is DiffUtilEquality) {
                (oldItem as DiffUtilEquality).itemTheSame(newItem)
            } else oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
            return if (oldItem is DiffUtilEquality) {
                (oldItem as DiffUtilEquality).realEquals(newItem)
            } else oldItem == newItem
        }

        override fun getChangePayload(oldItem: T, newItem: T): Any? {
            return if (oldItem is DiffUtilEquality) {
                (oldItem as DiffUtilEquality).payload(newItem)
            } else
                super.getChangePayload(oldItem, newItem)
        }
    })
        .setBackgroundThreadExecutor(Executors.newSingleThreadExecutor())
        .build()
) {
//    open fun notifyChecked(item: T, holder: VH) {}
//    open fun notifyVisibleCheckbox(item: T, holder: VH) {}
//    open fun notifySearch(item: T, holder: VH) {}
//    open fun notifyImage(item: T, holder: VH) {}
//
//    override fun onBindViewHolder(holder: VH & Any, position: Int, payloads: MutableList<Any>) {
//        if (payloads.isNotEmpty()) {
//            payloads.forEach {
//                when (it) {
//                    PAYLOAD_CHECKED -> {
//                        notifyChecked(getItem(position), holder)
//                    }
//
//                    PAYLOAD_VISIBLE_CHECKBOX -> {
//                        notifyVisibleCheckbox(getItem(position), holder)
//                    }
//                    PAYLOAD_SEARCH -> {
//                        notifySearch(getItem(position), holder)
//                    }
//                    PAYLOAD_IMAGE -> {
//                        notifyImage(getItem(position), holder)
//                    }
//                }
//            }
//        } else {
//            super.onBindViewHolder(holder, position, payloads)
//        }
//    }
}

interface DiffUtilEquality {
    fun itemTheSame(toCompare: Any?): Boolean
    fun realEquals(toCompare: Any?): Boolean
    fun payload(toCompare: Any?): Any?
}

const val UPDATE_ANIM = "UPDATE_ANIM"


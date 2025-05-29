package com.nekosoft.brokenglass.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.brokenscreen.prankapp.crack.screen.databinding.ItemHomeAdsBinding
import com.brokenscreen.prankapp.crack.screen.databinding.ItemHomeBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.gms.ads.nativead.NativeAd
import com.nekosoft.brokenglass.data.model.HomeModel
import com.nekosoft.brokenglass.data.model.ScreenModel
import com.nekosoft.brokenglass.extention.goneExt
import com.nekosoft.brokenglass.extention.visibleExt
import com.nekosoft.brokenglass.utils.clickSafe

class HomeAdapter(
    var context: Context,
    val isShowing: () -> Unit,
    var onClick: (HomeModel, Int) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var listItemHome = mutableListOf<Any>()
    private val VIEW_TYPE_EFFECT = 0
    private val VIEW_TYPE_ADS = 1

    @SuppressLint("NotifyDataSetChanged")
    fun setData(listScreen: MutableList<Any>) {
        this.listItemHome.clear()
        this.listItemHome.addAll(listScreen)
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(val binding: ItemHomeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(screenModel: HomeModel) {
            binding.root.clickSafe(300) {
                onClick.invoke(screenModel, absoluteAdapterPosition)
            }
        }
    }

    inner class AdsViewHolder(val binding: ItemHomeAdsBinding) :
        RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        return when (viewType) {
            VIEW_TYPE_EFFECT -> {
                val itemBinding = ItemHomeBinding.inflate(inflater, parent, false)
                ItemViewHolder(itemBinding)
            }

            VIEW_TYPE_ADS -> {
                val adsBinding = ItemHomeAdsBinding.inflate(inflater, parent, false)
                AdsViewHolder(adsBinding)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = listItemHome[position]
        when (item) {
            is HomeModel -> {
                (holder as ItemViewHolder).bind(item)
                val binding = holder.binding
                item.isloadSuccess = false
                Glide.with(context)
                    .load(item.drawble)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .addListener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            item.isloadSuccess = true
                            isShowing.invoke()
                            binding.btnItem.visibleExt()
                            binding.loadingImage.goneExt()
                            return false
                        }
                    })
                    .into(binding.btnItem)
            }

            else -> {
//                (holder as AdsViewHolder).binding.itemAds.apply {
//                    showShimmer(false)
//                    setNativeAd(item as NativeAd)
//                }
                (holder as AdsViewHolder).binding.root
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        return when (listItemHome[position]) {
            is HomeModel -> VIEW_TYPE_EFFECT
            else -> VIEW_TYPE_ADS
        }
    }

    override fun getItemCount(): Int = listItemHome.size


}
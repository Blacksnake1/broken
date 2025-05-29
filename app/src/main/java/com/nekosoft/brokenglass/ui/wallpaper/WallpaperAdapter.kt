package com.nekosoft.brokenglass.ui.wallpaper

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.brokenscreen.prankapp.crack.screen.databinding.ItemWallpaperAdsBinding
import com.brokenscreen.prankapp.crack.screen.databinding.ItemWallpaperBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.gms.ads.nativead.NativeAd
import com.nekosoft.brokenglass.data.model.CrankEffectItem
import com.nekosoft.brokenglass.data.model.NativeAdsModel
import com.nekosoft.brokenglass.data.model.ScreenModel
import com.nekosoft.brokenglass.extention.goneExt
import com.nekosoft.brokenglass.extention.visibleExt
import com.nekosoft.brokenglass.ui.crankEffect.CrankEffectAdapter.ItemViewHolder
import com.nekosoft.brokenglass.utils.clickSafe

class WallpaperAdapter(
    var context: Context,
    val isShowing: () -> Unit,
    var onClick: (ScreenModel, Int) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var listScreen = mutableListOf<Any>()
    private val VIEW_TYPE_EFFECT = 0
    private val VIEW_TYPE_ADS = 1

    @SuppressLint("NotifyDataSetChanged")
    fun setData(listScreen: MutableList<Any>) {
        this.listScreen.clear()
        this.listScreen.addAll(listScreen)
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(val binding: ItemWallpaperBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(screenModel: ScreenModel) {
            binding.root.clickSafe(300) {
                onClick.invoke(screenModel, absoluteAdapterPosition)
            }
        }
    }

    inner class AdsViewHolder(val binding: ItemWallpaperAdsBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        return when (viewType) {
            VIEW_TYPE_EFFECT -> {
                val itemBinding = ItemWallpaperBinding.inflate(inflater, parent, false)
                ItemViewHolder(itemBinding)
            }

            VIEW_TYPE_ADS -> {
                val adsBinding = ItemWallpaperAdsBinding.inflate(inflater, parent, false)
                AdsViewHolder(adsBinding)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = listScreen[position]
        when (item) {
            is ScreenModel -> {
                (holder as ItemViewHolder).bind(item)
                val binding = holder.binding
                item.isloadSuccess = false
                binding.loadingImage.visibleExt()
                if (item.url.isNullOrEmpty()) {
                    Glide.with(context)
                        .asBitmap()
                        .load(item.drawble)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .addListener(object : RequestListener<Bitmap> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Bitmap>?,
                                isFirstResource: Boolean,
                            ): Boolean {
                                return false
                            }

                            override fun onResourceReady(
                                resource: Bitmap?,
                                model: Any?,
                                target: Target<Bitmap>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean,
                            ): Boolean {
                                item.isloadSuccess = true
                                isShowing.invoke()
                                binding.loadingImage.goneExt()
                                return false
                            }
                        })
                        .into(binding.ivWallpaper)
                } else {
                    Glide.with(context)
                        .asBitmap()
                        .load(item.url)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .addListener(object : RequestListener<Bitmap> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Bitmap>?,
                                isFirstResource: Boolean,
                            ): Boolean {
                                return false
                            }

                            override fun onResourceReady(
                                resource: Bitmap?,
                                model: Any?,
                                target: Target<Bitmap>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean,
                            ): Boolean {
                                isShowing.invoke()
                                item.isloadSuccess = true
                                binding.loadingImage.goneExt()
                                return false
                            }
                        })
                        .into(binding.ivWallpaper)
                }

                if (item.isPremium == true) {
                    binding.vgPre.visibleExt()
                } else {
                    binding.vgPre.goneExt()
                }
            }

            is NativeAdsModel -> {
                (holder as AdsViewHolder).binding.itemAds.apply {
                    showShimmer(false)
                    setNativeAd(item.nativeAds as NativeAd)
                }
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        return when (listScreen[position]) {
            is ScreenModel -> VIEW_TYPE_EFFECT
            else -> VIEW_TYPE_ADS
        }
    }

    override fun getItemCount(): Int = listScreen.size


}
package com.nekosoft.brokenglass.ui.crankEffect

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.brokenscreen.prankapp.crack.screen.databinding.ItemBrokenScreenAdsBinding
import com.brokenscreen.prankapp.crack.screen.databinding.ItemBrokenScreenBinding
import com.bumptech.glide.Glide
import com.google.android.gms.ads.nativead.NativeAd
import com.nekosoft.brokenglass.data.model.CrankEffectItem
import com.nekosoft.brokenglass.data.model.EffectBrokenModel
import com.nekosoft.brokenglass.utils.clickSafe

class CrankEffectAdapter(
    var context: Context,
    var listScreen: MutableList<CrankEffectItem>,
    var onClick: (imageDrawable: EffectBrokenModel) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class ItemViewHolder(val binding: ItemBrokenScreenBinding) :
        RecyclerView.ViewHolder(binding.root) {}

    class AdsViewHolder(val binding: ItemBrokenScreenAdsBinding) :
        RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        return when (viewType) {
            VIEW_TYPE_EFFECT -> {
                val itemBinding = ItemBrokenScreenBinding.inflate(inflater, parent, false)
                ItemViewHolder(itemBinding)
            }

            VIEW_TYPE_ADS -> {
                val adsBinding = ItemBrokenScreenAdsBinding.inflate(inflater, parent, false)
                AdsViewHolder(adsBinding)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = listScreen[position]
        when (item) {
            is CrankEffectItem.Ads -> {
                val adsHolder = holder as AdsViewHolder
                adsHolder.binding.itemAds.apply {
                    showShimmer(false)
                    setNativeAd(item.adModel as NativeAd)
                }
            }

            is CrankEffectItem.Effect -> {
                val effectHolder = holder as ItemViewHolder
                Glide.with(context)
                    .load(item.crankEffectModel.image)
                    .into(effectHolder.binding.ivImage)
                effectHolder.binding.item.clickSafe {
                    onClick.invoke(item.crankEffectModel)
                }
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return when (listScreen[position]) {
            is CrankEffectItem.Effect -> VIEW_TYPE_EFFECT
            is CrankEffectItem.Ads -> VIEW_TYPE_ADS
        }
    }


    override fun getItemCount(): Int = listScreen.size

    private val VIEW_TYPE_EFFECT = 0
    private val VIEW_TYPE_ADS = 1
}
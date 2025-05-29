package com.nekosoft.brokenglass.ui.selectWeapon

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.brokenscreen.prankapp.crack.screen.R
import com.brokenscreen.prankapp.crack.screen.databinding.ItemWeaponBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.nekosoft.brokenglass.data.model.WeaponModel
import com.nekosoft.brokenglass.extention.goneExt
import com.nekosoft.brokenglass.utils.clickSafe
import com.nekosoft.brokenglass.extention.visibleExt

class SelectWeaponAdapter(
    var context: Context,
    var onClick: (WeaponModel, Int) -> Unit,
) : RecyclerView.Adapter<SelectWeaponAdapter.ItemViewHolder>() {
    private var listScreen = mutableListOf<WeaponModel>()
    var selectPosition = -1

    @SuppressLint("NotifyDataSetChanged")
    fun setData(listScreen: MutableList<WeaponModel>) {
        this.listScreen.clear()
        this.listScreen.addAll(listScreen)
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(val binding: ItemWeaponBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(weaponModel: WeaponModel) {
            weaponModel.isloadSuccess = false
            binding.loadingImage.visibleExt()
            Glide.with(context)
                .asBitmap()
                .load(weaponModel.image)
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
                        weaponModel.isloadSuccess = true
                        binding.loadingImage.goneExt()
                        binding.ivItemWeapon.visibleExt()
                        return false
                    }
                })
                .into(binding.ivItemWeapon)

            if (absoluteAdapterPosition == selectPosition) {
                weaponModel.selected = true
                binding.bgItemWeapon.setImageResource(R.drawable.theme_gun_selected)
            } else {
                weaponModel.selected = false
                binding.bgItemWeapon.setImageResource(R.drawable.theme_gun_unselected)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemBinding = ItemWeaponBinding.inflate(LayoutInflater.from(context), parent, false)
        return ItemViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val binding = holder.binding
        val item = listScreen[position]

        holder.bind(item)
        binding.root.clickSafe {
            if (item.isloadSuccess){
                notifyItemChanged(selectPosition)
                selectPosition = position
                notifyItemChanged(selectPosition)
                onClick.invoke(item, position)
            }

        }

    }


    override fun getItemCount(): Int = listScreen.size

}
package com.nekosoft.brokenglass.ui.recent

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.brokenscreen.prankapp.crack.screen.databinding.ItemBrokenScreenBinding
import com.bumptech.glide.Glide
import com.nekosoft.brokenglass.data.model.HistoryModel
import com.nekosoft.brokenglass.extention.goneExt
import com.nekosoft.brokenglass.utils.clickSafe
import com.nekosoft.brokenglass.extention.visibleExt

class RecentAdapter(
    var context: Context,
    var listScreen: MutableList<HistoryModel>,
    private var onClick: (lang: HistoryModel) -> Unit,
    private var onLongClick: (MutableList<HistoryModel>) -> Unit,
    private var onLongClickMode: (MutableList<HistoryModel>) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var binding: ItemBrokenScreenBinding? = null
    var isLongClickMode =
        false // đang ở chế độ delete sau khi ấn longclick item hay không: false là không, true là có

    var listDelete = mutableListOf<HistoryModel>()

    class ItemViewHolder(val binding: ItemBrokenScreenBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = ItemBrokenScreenBinding.inflate(LayoutInflater.from(context))
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ItemViewHolder
        val binding = holder.binding
        val imageAtPos = listScreen[position]
        if (imageAtPos.url.isNullOrEmpty()) {
            Glide.with(context)
                .load(imageAtPos.drawble)
                .into(binding.ivImage)
        } else {
            Glide.with(context)
                .load(imageAtPos.url)
                .into(binding.ivImage)
        }

        if (!isLongClickMode) {
            binding.ivCheck.goneExt()
        } else if (isLongClickMode && imageAtPos.isCheck == true) {
            binding.ivCheck.visibleExt()
        } else if (isLongClickMode && imageAtPos.isCheck == false) {
            binding.ivCheck.goneExt()
        }
        binding.item.clickSafe {
            if (!isLongClickMode) {
                onClick.invoke(imageAtPos)
            } else {
                if (!listDelete.contains(imageAtPos)) {
                    listDelete.add(imageAtPos)
                    binding.ivCheck.visibleExt()
                    imageAtPos.isCheck = true
                } else {
                    binding.ivCheck.goneExt()
                    imageAtPos.isCheck = false
                    listDelete.remove(imageAtPos)
                }
                onLongClickMode.invoke(listDelete)
            }
        }

        binding.item.setOnLongClickListener {
            if (!isLongClickMode) {
                isLongClickMode = true
                if (!listDelete.contains(imageAtPos)) {
                    listDelete.add(imageAtPos)
                    imageAtPos.isCheck = true
                   notifyItemChanged(position)
                } else {
                    listDelete.remove(imageAtPos)
                    imageAtPos.isCheck = false
                    notifyItemChanged(position)
                }
                onLongClick.invoke(listDelete)
            }
            return@setOnLongClickListener isLongClickMode
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteAll() {
        if (listDelete.size != listScreen.size) {
            listDelete.clear()
            listDelete.addAll(listScreen)
            listScreen.forEach {
                it.isCheck = true
            }
        } else {
            listDelete.clear()
            listScreen.forEach {
                it.isCheck = false
            }
        }
        onLongClick.invoke(listDelete)

        notifyDataSetChanged()
    }


    override fun getItemCount(): Int = listScreen.size
}
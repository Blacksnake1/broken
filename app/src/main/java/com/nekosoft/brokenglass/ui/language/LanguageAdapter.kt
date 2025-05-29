package com.nekosoft.brokenglass.ui.language

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.brokenscreen.prankapp.crack.screen.R
import com.brokenscreen.prankapp.crack.screen.databinding.ItemLanguageBinding
import java.util.*

class LanguageAdapter(
    var context: Context,
    private var onClick: (lang: LanguageModel) -> Unit,
) : RecyclerView.Adapter<LanguageAdapter.ItemViewHolder>() {
    var listShow = mutableListOf<LanguageModel>()
    var selectPosition = -1

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: MutableList<LanguageModel>) {
        listShow.clear()
        listShow.addAll(list)
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(var binding: ItemLanguageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bin(item: LanguageModel) {
            binding.ivFlag.setImageResource(item.flag)
            binding.tvLanguage.text = item.national
            if (absoluteAdapterPosition == selectPosition){
                binding.cb.isChecked = true
                binding.lnItem.setBackgroundResource(R.drawable.bg_stroke_solid_8)
            } else{
                binding.cb.isChecked = false
                binding.lnItem.setBackgroundResource(R.color.gray_35383f)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val bindingg = ItemLanguageBinding.inflate(LayoutInflater.from(context))
        return ItemViewHolder(bindingg)

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val languageSelected = listShow[position]
        holder.bin(languageSelected)

        holder.binding.lnItem.setOnClickListener {
            notifyItemChanged(selectPosition)
            selectPosition = position
            notifyItemChanged(selectPosition)
            onClick.invoke(languageSelected)
        }
    }

    override fun getItemCount(): Int {
        return listShow.size
    }
}
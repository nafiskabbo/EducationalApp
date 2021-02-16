package com.kabbodev.educational.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kabbodev.educational.data.model.Doubt
import com.kabbodev.educational.databinding.ItemDoubtBinding

class DoubtAdapter (private val listener: DoubtInterface) : RecyclerView.Adapter<DoubtAdapter.DoubtViewHolder>() {

    val doubtList = ArrayList<Doubt>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoubtViewHolder {
        val viewBinding =
            ItemDoubtBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val viewHolder = DoubtViewHolder(viewBinding)
        viewBinding.infoBtn.setOnClickListener {
            listener.onInfoBtnClick(viewHolder.bindingAdapterPosition)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: DoubtViewHolder, position: Int) {
        holder.viewBinding.doubtText.text = doubtList[position].questionId
    }

    override fun getItemCount(): Int = doubtList.size

    fun updateList(updatedList: List<Doubt>) {
        doubtList.clear()
        doubtList.addAll(updatedList)
        notifyDataSetChanged()
    }

    inner class DoubtViewHolder(val viewBinding: ItemDoubtBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

}

interface DoubtInterface {
    fun onInfoBtnClick(itemPos: Int)
}
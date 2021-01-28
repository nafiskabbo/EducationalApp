package com.kabbodev.educational.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kabbodev.educational.R
import com.kabbodev.educational.data.model.Plan
import com.kabbodev.educational.databinding.ItemPlanBinding

class PlanAdapter(private val listener: PlansInterface) :
    RecyclerView.Adapter<PlanAdapter.PlanViewHolder>() {

    private val TAG = "PlanAdapter"
    private val plansList = ArrayList<Plan>()

    inner class PlanViewHolder(var viewBinding: ItemPlanBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
        val viewBinding =
            ItemPlanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val viewHolder = PlanViewHolder(viewBinding)
        viewBinding.root.setOnClickListener {
            listener.onViewClick(viewHolder.bindingAdapterPosition)
        }
        viewBinding.typeBtn.setOnClickListener {
            listener.onBtnClick(viewHolder.bindingAdapterPosition)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
        val currentPos = plansList[position]
        holder.viewBinding.title.text = currentPos.title
        holder.viewBinding.subtitle.text = currentPos.subtitle
        holder.viewBinding.price.text = String.format(
            holder.viewBinding.root.context.getString(R.string.price_1_month),
            currentPos.price
        )
        holder.viewBinding.typeBtn.text = currentPos.type
    }

    override fun getItemCount(): Int = plansList.size

    fun updateList(updatedList: List<Plan>) {
        plansList.clear()
        plansList.addAll(updatedList)
        notifyDataSetChanged()
    }

}

interface PlansInterface {
    fun onViewClick(itemPos: Int)
    fun onBtnClick(itemPos: Int)
}
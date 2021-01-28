package com.kabbodev.educational.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kabbodev.educational.data.model.Subscription
import com.kabbodev.educational.databinding.ItemSubscribedPlanBinding

class SubscriptionAdapter(private val listener: SubscriptionInterface) :
    RecyclerView.Adapter<SubscriptionAdapter.SubscriptionViewHolder>() {

    private val TAG = "PlanAdapter"
    val subscriptionsList = ArrayList<Subscription>()

    inner class SubscriptionViewHolder(var viewBinding: ItemSubscribedPlanBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscriptionViewHolder {
        val viewBinding =
            ItemSubscribedPlanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val viewHolder = SubscriptionViewHolder(viewBinding)
//        viewBinding.infoBtn.setOnClickListener {
//            listener.onInfoBtnClick(viewHolder.bindingAdapterPosition)
//        }
//        viewBinding.settingBtn.setOnClickListener {
//            listener.onSettingsBtnClick(viewHolder.bindingAdapterPosition)
//        }
        viewBinding.startBtn.setOnClickListener {
            listener.onStartBtnClick(viewHolder.bindingAdapterPosition)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: SubscriptionViewHolder, position: Int) {
        val currentPos = subscriptionsList[position]
        holder.viewBinding.title.text = currentPos.plan?.title
        holder.viewBinding.subtitle.text = currentPos.plan?.subtitle
    }

    override fun getItemCount(): Int = subscriptionsList.size

    fun updateList(updatedList: List<Subscription>) {
        subscriptionsList.clear()
        subscriptionsList.addAll(updatedList)
        notifyDataSetChanged()
    }

}

interface SubscriptionInterface {
//    fun onInfoBtnClick(itemPos: Int)
//    fun onSettingsBtnClick(itemPos: Int)
    fun onStartBtnClick(itemPos: Int)
}
package com.kabbodev.educational.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kabbodev.educational.R
import com.kabbodev.educational.data.model.QuestionByUser
import com.kabbodev.educational.data.model.Recap
import com.kabbodev.educational.data.model.Subscription
import com.kabbodev.educational.databinding.ItemSubscribedPlanBinding
import com.kabbodev.educational.ui.interfaces.RecapCallback
import com.kabbodev.educational.ui.utils.snackbar
import com.kabbodev.educational.ui.viewModels.DashboardViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SubscriptionAdapter(private val listener: SubscriptionInterface, private val viewModel: DashboardViewModel) :
    RecyclerView.Adapter<SubscriptionAdapter.SubscriptionViewHolder>() {

    private val TAG = "PlanAdapter"
    val subscriptionsList = ArrayList<Subscription>()
    var listOfSubs: ArrayList<QuestionByUser>? = null
    var recapData: Recap? = null

    inner class SubscriptionViewHolder(var viewBinding: ItemSubscribedPlanBinding) : RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscriptionViewHolder {
        val viewBinding = ItemSubscribedPlanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val viewHolder = SubscriptionViewHolder(viewBinding)
        viewBinding.startBtn.setOnClickListener {
            if (viewBinding.startBtn.text == "Recap") {
                listener.onRecapBtnClick(viewHolder.bindingAdapterPosition, recapData!!, listOfSubs!!)
            } else {
                listener.onStartBtnClick(viewHolder.bindingAdapterPosition)
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: SubscriptionViewHolder, position: Int) {
        val currentPos = subscriptionsList[position]
        holder.viewBinding.title.text = currentPos.plan?.title
        holder.viewBinding.subtitle.text = currentPos.plan?.subtitle

        val listener = object : RecapCallback {
            override fun onSuccessListener(recap: Recap?, questionsList: ArrayList<QuestionByUser>?) {
                Log.d(TAG, "recap $recap")
                Log.d(TAG, "que $questionsList")
                if (recap == null) {
                    return
                }

                val recapAvailable = recap.recap
                if (!recapAvailable) {
                    return
                }

                val calendar = Calendar.getInstance()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                val dayFormat = SimpleDateFormat("dd", Locale.getDefault())

                calendar.add(Calendar.HOUR_OF_DAY, 3)
                val currentDate = calendar.time

                val recapDate: Date = dateFormat.parse(recap.date)!!
                calendar.time = recapDate
                calendar.add(Calendar.HOUR_OF_DAY, 3)

                val currentDay = dayFormat.format(currentDate).toInt()
                val recapDay = dayFormat.format(calendar.time).toInt()

                if (currentDay == recapDay) {
                    holder.viewBinding.startBtn.text = holder.viewBinding.root.context.getString(R.string.recap)
                    recapData = recap
                    listOfSubs = questionsList
                }
            }

            override fun onFailureListener(e: Exception) {
                Log.d(TAG, "Error ${e.message}")
                holder.viewBinding.root.snackbar("Error! ${e.message}")
            }
        }
        viewModel.getRecapData(currentPos.plan!!.id.toString(), listener)
    }

    override fun getItemCount(): Int = subscriptionsList.size

    fun updateList(updatedList: List<Subscription>) {
        subscriptionsList.clear()
        subscriptionsList.addAll(updatedList)
        notifyDataSetChanged()
    }

}

interface SubscriptionInterface {
    fun onStartBtnClick(itemPos: Int)
    fun onRecapBtnClick(itemPos: Int, recap: Recap, questionsList: ArrayList<QuestionByUser>)
}
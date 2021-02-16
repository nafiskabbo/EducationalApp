package com.kabbodev.educational.data.repository

import androidx.lifecycle.MutableLiveData
import com.kabbodev.educational.data.daos.PlanDao
import com.kabbodev.educational.data.model.Plan

class PlanRepository(private val planDao: PlanDao) {

    private val plansList: ArrayList<Plan> = ArrayList()
    private val liveData: MutableLiveData<ArrayList<Plan>> = MutableLiveData()

    fun getPlansList(className: String): MutableLiveData<ArrayList<Plan>> {
        if (plansList.size == 0) {
            planDao.loadAllPlansList(plansList, liveData, className)
        }
        liveData.postValue(plansList)
        return liveData
    }

}
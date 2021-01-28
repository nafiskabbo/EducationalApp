package com.kabbodev.educational.data.repository

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.kabbodev.educational.data.daos.DashboardDao
import com.kabbodev.educational.data.model.*
import com.kabbodev.educational.ui.`interface`.FirebaseCallback
import com.kabbodev.educational.ui.`interface`.StatsCallback

class DashboardRepository(private val dashboardDao: DashboardDao) {

    companion object {
        var user: User? = null
    }

    private val userLiveData: MutableLiveData<User> = MutableLiveData()

    private val subscriptionsList: ArrayList<Subscription> = ArrayList()
    private val subscriptionLiveData: MutableLiveData<ArrayList<Subscription>> = MutableLiveData()

    private val questionsLiveData: MutableLiveData<ArrayList<Question>> = MutableLiveData()

    private val plansList: ArrayList<Plan> = ArrayList()
    private val liveData: MutableLiveData<ArrayList<Plan>> = MutableLiveData()

    fun getCurrentUser(): FirebaseUser? = dashboardDao.getCurrentUser()

    fun getUser(): MutableLiveData<User> {
        if (user == null) {
            dashboardDao.loadUser(userLiveData)
        }
        userLiveData.postValue(user)
        return userLiveData
    }

    fun getPlansList(): MutableLiveData<ArrayList<Plan>> {
        if (plansList.size == 0) {
            dashboardDao.loadAllPlansList(plansList, liveData)
        }
        liveData.postValue(plansList)
        return liveData
    }

    fun getSubscriptionsList(updatedSubscriptionsList: List<String>): MutableLiveData<ArrayList<Subscription>> {
        if (subscriptionsList.size == 0) {
            dashboardDao.loadAllSubscriptionsList(
                subscriptionsList,
                subscriptionLiveData,
                updatedSubscriptionsList
            )
        }
        subscriptionLiveData.value = subscriptionsList
        return subscriptionLiveData
    }

    fun saveSubscriptionDetails(
        user: User,
        planId: String,
        paymentMonth: String,
        listener: FirebaseCallback
    ) = dashboardDao.saveSubscriptionDetails(user, planId, paymentMonth, listener)

    fun loadQuestions(chapterId: String): MutableLiveData<ArrayList<Question>> {
        dashboardDao.loadQuestions(chapterId, questionsLiveData)
        return questionsLiveData
    }

    fun downloadReport(type: String, listener: StatsCallback) {
        dashboardDao.downloadReport(type, listener)
    }

}
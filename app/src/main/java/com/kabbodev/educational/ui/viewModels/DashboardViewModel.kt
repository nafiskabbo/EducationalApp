package com.kabbodev.educational.ui.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.kabbodev.educational.data.daos.DashboardDao
import com.kabbodev.educational.data.model.*
import com.kabbodev.educational.data.repository.DashboardRepository
import com.kabbodev.educational.ui.`interface`.FirebaseCallback
import com.kabbodev.educational.ui.`interface`.StatsCallback

class DashboardViewModel : ViewModel() {

    private val repository: DashboardRepository

    private var user: MutableLiveData<User> = MutableLiveData()
    private var subscriptionsList: MutableLiveData<ArrayList<Subscription>> = MutableLiveData()

    private var answerList: ArrayList<String> = ArrayList()
    private var questionsList: MutableLiveData<ArrayList<Question>> = MutableLiveData()

    private var plansList: MutableLiveData<ArrayList<Plan>> = MutableLiveData()
    private var mutablePlan: MutableLiveData<Plan> = MutableLiveData()

    private val onSuccess: MutableLiveData<Boolean> = MutableLiveData()
    private val onError: MutableLiveData<Boolean> = MutableLiveData()

    init {
        val dashboardDao = DashboardDao()
        repository = DashboardRepository(dashboardDao)
    }

    fun getCurrentUser(): FirebaseUser? = repository.getCurrentUser()

    fun getUser(): LiveData<User> {
        user = repository.getUser()
        return user
    }

    fun setUser(updated: User) {
        user.value = updated
    }

    fun getPlansList(): LiveData<ArrayList<Plan>> {
        plansList = repository.getPlansList()
        return plansList
    }

    fun getSelectedPlan(): LiveData<Plan> = mutablePlan

    fun setSelectedPlan(position: Int) {
        val pattern: Plan? = plansList.value?.get(position)
        mutablePlan.value = pattern
    }

    fun saveSubscriptionDetails(
        user: User,
        planId: String,
        paymentMonth: String,
        listener: FirebaseCallback
    ) {
        repository.saveSubscriptionDetails(user, planId, paymentMonth, listener)
    }

    fun loadPlanDetail(updatedSubscriptionsList: List<String>): LiveData<ArrayList<Subscription>> {
        subscriptionsList = repository.getSubscriptionsList(updatedSubscriptionsList)
        return subscriptionsList
    }

    fun getAnswerList(): ArrayList<String> = answerList

    fun setAnswerList(updated: ArrayList<String>) {
        answerList.clear()
        answerList.addAll(updated)
    }

    fun loadQuestionsList(chapterId: String): MutableLiveData<ArrayList<Question>> {
        questionsList = repository.loadQuestions(chapterId)
        return questionsList
    }

    fun setUserQueList(newList: ArrayList<Question>) {
        questionsList.value = newList
    }

    fun resetAnswerForUser() {
        val updated: ArrayList<Question>? = questionsList.value
        updated?.forEach {
            it.answer = ""
        }
        questionsList.value = updated
    }

    fun updateUserAnsList(position: Int, value: String) {
        val updated: ArrayList<Question>? = questionsList.value
        updated?.get(position)?.answer = value
        questionsList.value = updated
    }

    fun getUserQueList(): MutableLiveData<ArrayList<Question>> {
        return questionsList
    }

    fun downloadReport(type: String, listener: StatsCallback) {
        repository.downloadReport(type, listener)
    }

    // payment
    fun getOnSuccess(): LiveData<Boolean> {
        return onSuccess
    }

    fun getOnError(): LiveData<Boolean> {
        return onError
    }

    fun setOnSuccess(result: Boolean?) {
        onSuccess.value = result
    }

    fun setOnError(result: Boolean?) {
        onError.value = result
    }

}
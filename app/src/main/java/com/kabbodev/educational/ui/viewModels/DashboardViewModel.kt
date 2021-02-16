package com.kabbodev.educational.ui.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.kabbodev.educational.data.daos.UserDao
import com.kabbodev.educational.data.daos.PlanDao
import com.kabbodev.educational.data.daos.QuestionDao
import com.kabbodev.educational.data.model.*
import com.kabbodev.educational.data.repository.UserRepository
import com.kabbodev.educational.data.repository.PlanRepository
import com.kabbodev.educational.data.repository.QuestionRepository
import com.kabbodev.educational.ui.interfaces.*

class DashboardViewModel : ViewModel() {

    private val userRepository: UserRepository
    private val planRepository: PlanRepository
    private val questionRepository: QuestionRepository

    private var user: MutableLiveData<User> = MutableLiveData()
    private var subscriptionsList: MutableLiveData<ArrayList<Subscription>> = MutableLiveData()

    private var queByUserList: ArrayList<QuestionByUser> = ArrayList()
    private var recap: Recap = Recap()

    private var answerList: ArrayList<String> = ArrayList()
    private var limitedQuestionsList: MutableLiveData<ArrayList<Question>> = MutableLiveData()

    private var plansList: MutableLiveData<ArrayList<Plan>> = MutableLiveData()
    private var mutablePlan: MutableLiveData<Plan> = MutableLiveData()

    private val onSuccess: MutableLiveData<Boolean> = MutableLiveData()
    private val onError: MutableLiveData<Boolean> = MutableLiveData()

    init {
        val userDao = UserDao()
        userRepository = UserRepository(userDao)
        val planDao = PlanDao()
        planRepository = PlanRepository(planDao)
        val questionDao = QuestionDao()
        questionRepository = QuestionRepository(questionDao)
    }

    fun getCurrentUser(): FirebaseUser? = userRepository.getCurrentUser()

    fun getUser(): LiveData<User> {
        user = userRepository.getUser()
        return user
    }

    fun setUser(updated: User) {
        user.value = updated
    }

    fun getPlansList(className: String): LiveData<ArrayList<Plan>> {
        plansList = planRepository.getPlansList(className)
        return plansList
    }

    fun getSelectedPlan(): LiveData<Plan> = mutablePlan

    fun setSelectedPlan(position: Int) {
        val pattern: Plan? = plansList.value?.get(position)
        mutablePlan.value = pattern
    }

    fun saveSubscriptionDetails(user: User, planId: String, paymentMonth: String, listener: FirebaseCallback) {
        userRepository.saveSubscriptionDetails(user, planId, paymentMonth, listener)
    }

    fun setRecapData(recapData: Recap) {
        recap = recapData
    }

    fun setQuestionsListByRecap(questionsList: ArrayList<QuestionByUser>) {
        queByUserList.clear()
        queByUserList.addAll(questionsList)
    }

    fun getRecapData() = recap

    fun getQuestionsListByRecap() = queByUserList

    fun loadPlanDetail(updatedSubscriptionsList: List<String>): LiveData<ArrayList<Subscription>> {
        subscriptionsList = userRepository.getSubscriptionsList(updatedSubscriptionsList)
        return subscriptionsList
    }

    fun getAnswerList(): ArrayList<String> = answerList

    fun setAnswerList(updated: ArrayList<String>) {
        answerList.clear()
        answerList.addAll(updated)
    }

    fun loadQuestionsList(selectedChapterIds: ArrayList<String>, selectedQuestionsCount: Int, listener: QuestionCallback) {
        questionRepository.loadQuestions(selectedChapterIds, selectedQuestionsCount, listener)
    }

    fun setUserQueList(newList: ArrayList<Question>) {
        limitedQuestionsList.value = newList
    }

    fun updateUserAnsList(position: Int, value: String) {
        val updated: ArrayList<Question>? = limitedQuestionsList.value
        updated?.get(position)?.answer = value
        Log.d("Questions", "op1 ${updated?.get(position)?.answer}")
        limitedQuestionsList.value = updated
        Log.d("Questions", "op2 ${limitedQuestionsList.value?.get(position)?.answer}")
    }

    fun getUserQueList(): MutableLiveData<ArrayList<Question>> {
        return limitedQuestionsList
    }

    fun saveDoubts(doubtList: ArrayList<Doubt>, listener: FirebaseCallback) {
        userRepository.saveDoubts(doubtList, listener)
    }

    fun loadDoubtsList(listener: DoubtCallback) {
        userRepository.loadDoubts(listener)
    }

    fun downloadReport(type: String, listener: StatsCallback) {
        userRepository.downloadReport(type, listener)
    }

    fun getRecapData(subsId: String, listener: RecapCallback) = userRepository.getRecapData(subsId, listener)

    fun saveRecapData(subscriptionId: String, recap: Recap, questionList: ArrayList<QuestionByUser>, listener: FirebaseCallback) =
        userRepository.saveRecapData(subscriptionId, recap, questionList, listener)


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
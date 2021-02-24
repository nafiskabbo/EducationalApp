package com.kabbodev.educational.data.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.kabbodev.educational.data.daos.UserDao
import com.kabbodev.educational.data.model.*
import com.kabbodev.educational.ui.interfaces.DoubtCallback
import com.kabbodev.educational.ui.interfaces.FirebaseCallback
import com.kabbodev.educational.ui.interfaces.RecapCallback
import com.kabbodev.educational.ui.interfaces.StatsCallback
import kotlin.collections.ArrayList

class UserRepository(private val userDao: UserDao) {

    companion object {
        var user: User? = null
    }

    private val userLiveData: MutableLiveData<User> = MutableLiveData()

    private val subscriptionsList: ArrayList<Subscription> = ArrayList()
    private val subscriptionLiveData: MutableLiveData<ArrayList<Subscription>> = MutableLiveData()

    fun getCurrentUser(): FirebaseUser? = userDao.getCurrentUser()

    fun getUser(): MutableLiveData<User> {
        if (user == null) {
            userDao.loadUser(userLiveData)
        }
        userLiveData.postValue(user)
        return userLiveData
    }

    fun getSubscriptionsList(update: Boolean, updatedSubscriptionsList: List<String>): MutableLiveData<ArrayList<Subscription>> {
        if (update) {
            userDao.loadAllSubscriptionsList(
                subscriptionsList,
                subscriptionLiveData,
                updatedSubscriptionsList
            )
            Log.d("AAA", "$subscriptionsList")
        } else {
            if (subscriptionsList.size == 0) {
                userDao.loadAllSubscriptionsList(
                    subscriptionsList,
                    subscriptionLiveData,
                    updatedSubscriptionsList
                )
            }
        }
        subscriptionLiveData.value = subscriptionsList
        return subscriptionLiveData
    }

    fun saveSubscriptionDetails(user: User, planId: String, paymentMonth: String, listener: FirebaseCallback) =
        userDao.saveSubscriptionDetails(user, planId, paymentMonth, listener)

    fun downloadReport(type: String, listener: StatsCallback) = userDao.downloadReport(type, listener)

    fun saveDoubts(doubtList: ArrayList<Doubt>, listener: FirebaseCallback) = userDao.saveDoubts(doubtList, listener)

    fun loadDoubts(listener: DoubtCallback) = userDao.loadDoubts(listener)

    fun getRecapData(subsId: String, listener: RecapCallback) = userDao.getRecapData(subsId, listener)

    fun saveRecapData(subscriptionId: String, recap: Recap, questionList: ArrayList<QuestionByUser>, listener: FirebaseCallback) =
        userDao.saveRecapData(subscriptionId, recap, questionList, listener)

}
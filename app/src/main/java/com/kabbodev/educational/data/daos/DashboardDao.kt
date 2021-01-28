package com.kabbodev.educational.data.daos

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.kabbodev.educational.data.model.*
import com.kabbodev.educational.data.repository.DashboardRepository
import com.kabbodev.educational.ui.`interface`.FirebaseCallback
import com.kabbodev.educational.ui.`interface`.StatsCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File

class DashboardDao {

    private val TAG = "dashboard"
    private val firebaseAuth = Firebase.auth
    private val firebaseFirestore = Firebase.firestore
    private val userCollection: CollectionReference = firebaseFirestore.collection("USERS")
    private val plansCollection: CollectionReference = firebaseFirestore.collection("Plans")
    private val chaptersCollection: CollectionReference = firebaseFirestore.collection("Chapters")
    private val questionsCollection: CollectionReference = firebaseFirestore.collection("Questions")

    fun getCurrentUser(): FirebaseUser? = firebaseAuth.currentUser

    fun loadUser(userLiveData: MutableLiveData<User>) {
        GlobalScope.launch(Dispatchers.IO) {
            val user = getUserByID(getCurrentUser()!!.uid).await().toObject(User::class.java)!!
            DashboardRepository.user = user
            userLiveData.postValue(user)
        }
    }

    private fun getUserByID(uid: String): Task<DocumentSnapshot> =
        userCollection.document(uid).get()

    fun loadAllSubscriptionsList(
        subscriptionsList: ArrayList<Subscription>,
        liveData: MutableLiveData<ArrayList<Subscription>>,
        updatedSubscriptionsList: List<String>
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            updatedSubscriptionsList.forEach {
                Log.d(TAG, "size 1 : ${subscriptionsList.size}")
                val plan = getPlanByID(it).await().toObject(Plan::class.java)

                val document = getChapterById(plan?.chapter_set!!).await()
                val totalChaptersCount = document.getString("chapters_count")
                val arrayList: ArrayList<Chapter> = ArrayList()

                for (i in 1 until (totalChaptersCount!!.toInt() + 1)) {
                    val chapterNo = if (i < 10) {
                        "0$i"
                    } else {
                        i.toString()
                    }
                    arrayList.add(Chapter(i, document.getString("chapter_${chapterNo}_que_set")))
                }

                subscriptionsList.add(
                    Subscription(
                        plan = plan,
                        chapter = arrayList
                    )
                )
            }
            Log.d(TAG, "size 2 : ${subscriptionsList.size}")
            liveData.postValue(subscriptionsList)
        }
    }

    fun loadAllPlansList(
        plansList: ArrayList<Plan>,
        liveData: MutableLiveData<ArrayList<Plan>>
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            val task = plansCollection.get().await()
            task.documents.forEach { document ->
                document?.let {
                    plansList.add(it.toObject(Plan::class.java)!!)
                }
            }
            liveData.postValue(plansList)
        }
    }

    private fun getPlanByID(planId: String): Task<DocumentSnapshot> =
        plansCollection.document(planId).get()

    fun saveSubscriptionDetails(
        user: User,
        planId: String,
        paymentMonth: String,
        listener: FirebaseCallback
    ) {
        val newList: ArrayList<String> = ArrayList()
        newList.addAll(user.subscriptions)
        newList.add(planId)

        val monthList: ArrayList<String> = ArrayList()
        monthList.addAll(user.subscriptionsMonth)
        monthList.add(paymentMonth)

        val updated = User(
            fullName = user.fullName,
            email = user.email,
            board = user.board,
            class_ = user.class_,
            password = user.password,
            subscriptions = newList,
            subscriptionsMonth = monthList
        )

        userCollection.document(getCurrentUser()!!.uid).set(updated)
            .addOnSuccessListener {
                listener.onSuccessListener(user = updated)
            }
            .addOnFailureListener {
                Log.d(TAG, "error $it")
                listener.onFailureListener(it)
            }
    }

    private fun getChapterById(chapterId: String): Task<DocumentSnapshot> =
        chaptersCollection.document(chapterId).get()

    private fun getQuestionsById(queSetId: String): Task<QuerySnapshot> =
        questionsCollection.document(queSetId).collection("Ques").get()

    fun loadQuestions(
        chapterId: String,
        questionsLiveData: MutableLiveData<ArrayList<Question>>
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            val list: ArrayList<Question> = ArrayList()
            getQuestionsById(chapterId).await().documents.forEach { snap ->
                snap?.let { document ->
                    val queSet = document.toObject(Question::class.java)
                    list.add(queSet!!)
                }
            }
            questionsLiveData.postValue(list)
        }
    }

    fun downloadReport(type: String, listener: StatsCallback) {
        GlobalScope.launch(Dispatchers.Main) {
            val stats =
                userCollection.document(firebaseAuth.uid!!).collection("STATS").get().await()
            Log.d(TAG, "${stats.isEmpty}")
            Log.d(TAG, "${stats.documents.size}")

            if (stats.isEmpty) {
                listener.onSuccessListener(totalEmpty = true)
            } else {
                stats.documents.forEach {
                    if (it.getString("type") == type) {
                        val downloadFile = it.getString("report_file")
                        if (downloadFile.equals("")) {
                            listener.onSuccessListener(totalEmpty = true)
                        } else {
                            downloadOnDevice(type = type, downloadFile, listener)
                        }
                    }
                }
            }

        }
    }

    private fun downloadOnDevice(type: String, downloadFile: String?, listener: StatsCallback) {
        val storageRef = Firebase.storage.reference
        storageRef.child("$type/$downloadFile")
        val localFile = File.createTempFile(type, "xlsx")

        storageRef.getFile(localFile)
            .addOnSuccessListener {
                listener.onSuccessListener(totalEmpty = false)
            }
            .addOnFailureListener {
                listener.onFailureListener(it)
            }
    }

}
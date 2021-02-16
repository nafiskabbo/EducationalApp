package com.kabbodev.educational.data.daos

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kabbodev.educational.data.model.*
import com.kabbodev.educational.data.repository.UserRepository
import com.kabbodev.educational.ui.interfaces.DoubtCallback
import com.kabbodev.educational.ui.interfaces.FirebaseCallback
import com.kabbodev.educational.ui.interfaces.RecapCallback
import com.kabbodev.educational.ui.interfaces.StatsCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class UserDao {

    private val TAG = "user"
    private val firebaseAuth = Firebase.auth
    private val firebaseFirestore = Firebase.firestore
    private val userCollection: CollectionReference = firebaseFirestore.collection("USERS")
    private val plansCollection: CollectionReference = firebaseFirestore.collection("Plans")
    private val chaptersCollection: CollectionReference = firebaseFirestore.collection("Chapters")

    fun getCurrentUser(): FirebaseUser? = firebaseAuth.currentUser

    fun loadUser(userLiveData: MutableLiveData<User>) {
        GlobalScope.launch(Dispatchers.IO) {
            val user = getUserByID(getCurrentUser()!!.uid).await().toObject(User::class.java)!!
            UserRepository.user = user
            userLiveData.postValue(user)
        }
    }

    private fun getUserByID(uid: String): Task<DocumentSnapshot> = userCollection.document(uid).get()

    fun loadAllSubscriptionsList(
        subscriptionsList: ArrayList<Subscription>,
        liveData: MutableLiveData<ArrayList<Subscription>>,
        updatedSubscriptionsList: List<String>
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            updatedSubscriptionsList.forEach {
                Log.d(TAG, "subscriptionsList size : ${subscriptionsList.size}")
                Log.d(TAG, "updatedSubscriptionsList : $updatedSubscriptionsList")

                val plan = getPlanByID(it).await().toObject(Plan::class.java)
                Log.d(TAG, "plan $plan")
                Log.d(TAG, "plan ${plan?.type}")

                if (plan?.type == "Join") {
                    subscriptionsList.add(Subscription(plan = plan, chapter = null))
                } else {
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

                    subscriptionsList.add(Subscription(plan = plan, chapter = arrayList))

                }

            }
            Log.d(TAG, "size 2 : ${subscriptionsList.size}")
            liveData.postValue(subscriptionsList)
        }
    }


    private fun getPlanByID(planId: String): Task<DocumentSnapshot> = plansCollection.document(planId).get()

    fun saveSubscriptionDetails(user: User, planId: String, paymentMonth: String, listener: FirebaseCallback) {
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

    private fun getChapterById(chapterId: String): Task<DocumentSnapshot> = chaptersCollection.document(chapterId).get()

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
                            listener.onSuccessListener(
                                totalEmpty = false,
                                downloadFile = downloadFile
                            )
                        }
                    }
                }
            }

        }
    }

    fun saveDoubts(doubtList: ArrayList<Doubt>, listener: FirebaseCallback) {
        GlobalScope.launch(Dispatchers.Main) {
            val stats =
                userCollection.document(firebaseAuth.uid!!).collection("STATS")
                    .document("My Doubts").get().await()
            Log.d(TAG, "${stats.exists()}")
            Log.d(TAG, "${stats.get("doubt_website_link")}")
            Log.d(TAG, "${stats.get("question_code")}")

            if (!stats.exists()) {
                val doubtQueIdList: ArrayList<String> = ArrayList()
                val doubtSolveList: ArrayList<String> = ArrayList()

                doubtList.forEach {
                    doubtQueIdList.add(it.questionId)
                    doubtSolveList.add(it.solveLink)
                }

                val map: MutableMap<String, Any> = HashMap()
                map["type"] = "My Doubts"
                map["question_code"] = doubtQueIdList
                map["doubt_website_link"] = doubtSolveList

                userCollection.document(firebaseAuth.uid!!).collection("STATS")
                    .document("My Doubts").set(map)
                    .addOnSuccessListener {
                        listener.onSuccessListener()
                    }
                    .addOnFailureListener {
                        Log.d(TAG, "failure : $it")
                    }

            } else {
                val doubtQueIdList: ArrayList<String> = ArrayList()
                (stats.get("question_code") as ArrayList<String>?).let {
                    it?.let { it1 ->
                        doubtQueIdList.addAll(it1)
                    }
                }

                val doubtSolveList: ArrayList<String> = ArrayList()
                (stats.get("doubt_website_link") as ArrayList<String>?).let {
                    it?.let { it1 ->
                        doubtSolveList.addAll(it1)
                    }
                }

                doubtList.forEach {
                    doubtQueIdList.add(it.questionId)
                    doubtSolveList.add(it.solveLink)
                }

                val map: MutableMap<String, Any> = HashMap()
                map["type"] = "My Doubts"
                map["question_code"] = doubtQueIdList
                map["doubt_website_link"] = doubtSolveList

                userCollection.document(firebaseAuth.uid!!).collection("STATS")
                    .document("My Doubts").set(map)
                    .addOnSuccessListener {
                        listener.onSuccessListener()
                    }
                    .addOnFailureListener {
                        Log.d(TAG, "failure : $it")
                    }

            }
        }
    }

    fun loadDoubts(listener: DoubtCallback) {
        GlobalScope.launch(Dispatchers.Main) {
            val stats = userCollection.document(firebaseAuth.uid!!).collection("STATS").document("My Doubts").get().await()
            Log.d(TAG, "${stats.exists()}")
            Log.d(TAG, "${stats.get("doubt_website_link")}")
            Log.d(TAG, "${stats.get("question_code")}")

            if (!stats.exists()) {
                listener.onSuccessListener(totalEmpty = true, null)
            } else {
                val doubtsIdList: List<String>? = stats.get("question_code") as List<String>?
                val doubtQueIdSolveList: List<String>? = stats.get("doubt_website_link") as List<String>?

                val doubtsList: ArrayList<Doubt> = ArrayList()
                doubtsIdList?.forEachIndexed { index, s ->
                    doubtsList.add(
                        Doubt(
                            questionId = s,
                            solveLink = doubtQueIdSolveList?.get(index).toString()
                        )
                    )
                }

                listener.onSuccessListener(totalEmpty = false, doubtList = doubtsList)
            }
        }
    }

    fun getRecapData(subsId: String, listener: RecapCallback) {
//        userCollection.document(firebaseAuth.uid!!).collection("Recap").document("Recap").get()
        userCollection.document(firebaseAuth.uid!!).collection(subsId).get()
            .addOnSuccessListener {
                if (it.isEmpty) {
                    listener.onSuccessListener(null)
                    return@addOnSuccessListener
                }
                var recap: Recap? = null
                val arrayList: ArrayList<QuestionByUser> = ArrayList()

                it.documents.forEach { snapshot ->
                    if (snapshot.getBoolean("recap") == true) {
                        recap = snapshot.toObject(Recap::class.java)
                    } else {
                        arrayList.add(snapshot.toObject(QuestionByUser::class.java)!!)
                    }
                }

                listener.onSuccessListener(recap, arrayList)
            }
            .addOnFailureListener {
                listener.onFailureListener(it)
            }
    }

    fun saveRecapData(subscriptionId: String, recap: Recap, questionList: ArrayList<QuestionByUser>, listener: FirebaseCallback) {
        GlobalScope.launch(Dispatchers.Main) {
            val collection = userCollection.document(firebaseAuth.uid!!).collection(subscriptionId)
            val recapCollection = collection.get().await()
            recapCollection.forEach {
                it.reference.delete().await()
            }

            questionList.forEachIndexed { index, questionByUser ->
                val queIndex = index + 1
                val queNo = if (queIndex < 10) {
                    "0$queIndex"
                } else {
                    queIndex.toString()
                }

                collection.document("Ques $queNo").set(questionByUser).await()
            }

            collection.document("Recap").set(recap)
                .addOnSuccessListener {
                    listener.onSuccessListener()
                }
                .addOnFailureListener {
                    listener.onFailureListener(it)
                }
        }
    }

}
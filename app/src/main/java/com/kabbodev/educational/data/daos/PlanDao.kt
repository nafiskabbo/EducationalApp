package com.kabbodev.educational.data.daos

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kabbodev.educational.data.model.Plan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PlanDao {

    private val TAG = "plan"
    private val firebaseFirestore = Firebase.firestore
    private val plansCollection: CollectionReference = firebaseFirestore.collection("Plans")

    fun loadAllPlansList(plansList: ArrayList<Plan>, liveData: MutableLiveData<ArrayList<Plan>>, className: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val task = plansCollection.get().await()

            plansList.clear()
            task.documents.forEach { document ->
                Log.d(TAG, "class ${task.documents.size}")
                if (document.getString("class_name") == className) {
                    plansList.add(document.toObject(Plan::class.java)!!)
                }
            }
            liveData.postValue(plansList)
        }
    }

}
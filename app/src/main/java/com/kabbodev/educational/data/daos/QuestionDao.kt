package com.kabbodev.educational.data.daos

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kabbodev.educational.data.model.Question
import com.kabbodev.educational.ui.interfaces.QuestionCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class QuestionDao {

    private val TAG = "questions"
    private val firebaseFirestore = Firebase.firestore
    private val questionsCollection: CollectionReference = firebaseFirestore.collection("Questions")

    private fun getQuestionsById(queSetId: String): Task<QuerySnapshot> =
        questionsCollection.document(queSetId).collection("Ques").get()

    fun loadQuestions(selectedChapterIds: ArrayList<String>, selectedQuestionsCount: Int, listener: QuestionCallback) {
        GlobalScope.launch(Dispatchers.IO) {
            val quePerChapter: Int = selectedQuestionsCount / selectedChapterIds.size
            Log.d(TAG, "selectedQuestionsCount $selectedQuestionsCount")
            Log.d(TAG, "size $selectedChapterIds.size")
            Log.d(TAG, "que $quePerChapter")

            val limitedList: ArrayList<Question> = ArrayList()

            selectedChapterIds.forEach { chapterId ->

                val list: ArrayList<Question> = ArrayList()

                getQuestionsById(chapterId).await().documents.forEach { snap ->
                    snap?.let { document ->
                        val queSet = document.toObject(Question::class.java)
                        list.add(queSet!!)
                    }
                }

                if (list.size > 0) {
                    for (i in 0 until quePerChapter) {
                        limitedList.add(list.random())
                    }
                }

            }

            withContext(Dispatchers.Main) {
                listener.onSuccessListener(limitedList)
            }
        }
    }

}
package com.kabbodev.educational.data.repository

import com.kabbodev.educational.data.daos.QuestionDao
import com.kabbodev.educational.ui.interfaces.QuestionCallback

class QuestionRepository(private val questionDao: QuestionDao) {

    fun loadQuestions(selectedChapterIds: ArrayList<String>, selectedQuestionsCount: Int, listener: QuestionCallback) {
        questionDao.loadQuestions(selectedChapterIds, selectedQuestionsCount, listener)
    }

}
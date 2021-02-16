package com.kabbodev.educational.ui.fragments

import android.app.Dialog
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.navigation.NavOptions
import com.kabbodev.educational.R
import com.kabbodev.educational.data.model.Question
import com.kabbodev.educational.data.model.QuestionByUser
import com.kabbodev.educational.data.model.Recap
import com.kabbodev.educational.data.model.User
import com.kabbodev.educational.databinding.FragmentResultBinding
import com.kabbodev.educational.ui.interfaces.FirebaseCallback
import com.kabbodev.educational.ui.base.BaseFragment
import com.kabbodev.educational.ui.utils.createDialog
import com.kabbodev.educational.ui.utils.snackbar
import com.kabbodev.educational.ui.viewModels.DashboardViewModel
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ResultFragment : BaseFragment<FragmentResultBinding, DashboardViewModel>(), FirebaseCallback {

    private val TAG = "Result"
    private var userQuestionsList: ArrayList<Question>? = null
    private var answersList: ArrayList<String>? = null
    private var studentMark: Int = 0
    private lateinit var loadingDialog: Dialog

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentResultBinding.inflate(inflater, container, false)

    override fun getViewModel() = DashboardViewModel::class.java

    override fun setupTheme() {
        loadingDialog = createDialog(requireContext(), R.layout.loading_progress_dialog, R.drawable.progress_circle, false)
        if (arguments?.getBoolean("recap_from_db") == true) {
            val recapData = viewModel.getRecapData()
            binding.totalMark.text = viewModel.getQuestionsListByRecap().size.toString()
            studentMark = recapData.studentMark.toInt()
            binding.studentMark.text = studentMark.toString()
        } else {
            loadingDialog.show()
            viewModel.getUserQueList().observe(viewLifecycleOwner, {
                it?.let { list ->
                    userQuestionsList = list
                    updateUI(userQuestionsList!!)
                }
            })
        }
    }

    override fun setupClickListeners() {
        binding.recapBtn.setOnClickListener {
            if (arguments?.getBoolean("recap_from_db") == true) {
                navController.navigate(
                    R.id.action_resultFragment_to_questionsFragment,
                    bundleOf("recap_from_db" to true)
                )
            } else {
                navController.navigate(
                    R.id.action_resultFragment_to_questionsFragment,
                    bundleOf("recap" to "yes", "recap_mark" to studentMark.toString())
                )
            }
        }
        binding.infoBtn.setOnClickListener {
            onInfoBtnClick()
        }
        binding.nextBtn.setOnClickListener {
            val navOptions = NavOptions.Builder()
                .setEnterAnim(R.anim.slide_in_right)
                .setExitAnim(R.anim.slide_out_left)
                .setPopEnterAnim(R.anim.slide_in_left)
                .setPopExitAnim(R.anim.slide_out_right)
                .setPopUpTo(R.id.navigation_home, true)
                .build()
            navController.navigate(R.id.action_global_homeFragment, null, navOptions)
        }
    }

    private fun updateUI(list: ArrayList<Question>) {
        binding.totalMark.text = list.size.toString()

        if (arguments?.getString("recap_mark") == null) {
            answersList = viewModel.getAnswerList()
            answersList!!.forEachIndexed { index, ans ->
                if (ans == list[index].answer) {
                    Log.d(TAG, "ans 1! $ans")
                    Log.d(TAG, "ans 2! ${list[index].answer}")
                    Log.d(TAG, "adding! $studentMark")
                    studentMark++
                }
            }

            val listOfRecap: ArrayList<QuestionByUser> = ArrayList()
            userQuestionsList!!.forEachIndexed { index, question ->
                listOfRecap.add(
                    QuestionByUser(
                        answer = question.answer,
                        correctAnswer = answersList!![index],
                        question = question.question,
                        question_id = question.question_id,
                        question_img = question.question_img,
                        option_1 = question.option_1,
                        option_1_img = question.option_1_img,
                        option_2 = question.option_2,
                        option_2_img = question.option_2_img,
                        option_3 = question.option_3,
                        option_3_img = question.option_3_img,
                        option_4 = question.option_4,
                        option_4_img = question.option_4_img,
                        solve_link = question.solve_link
                    )
                )
            }

            val calendar = Calendar.getInstance()
            val date = calendar.time
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())

            val recap = Recap(
                date = dateFormat.format(date).toString(),
                studentMark = studentMark.toString(),
                recap = true
            )
            viewModel.saveRecapData(arguments?.getString("subscription_id").toString(), recap, listOfRecap, this)

        } else {
            loadingDialog.dismiss()
            studentMark = arguments?.getString("recap_mark")!!.toInt()
        }

        binding.studentMark.text = studentMark.toString()
    }

    private fun onInfoBtnClick() {
        val clickListener: DialogInterface.OnClickListener =
            DialogInterface.OnClickListener { dialog, _ ->
                dialog.dismiss()
            }

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.alert))
            .setMessage(getString(R.string.doubt_added))
            .setPositiveButton(getString(R.string.ok), clickListener)
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    override fun onSuccessListener(user: User?) {
        loadingDialog.dismiss()
    }

    override fun onFailureListener(e: Exception) {
        Log.d(TAG, "Error ${e.message}")
        binding.root.snackbar("Error! ${e.message}")
    }

}
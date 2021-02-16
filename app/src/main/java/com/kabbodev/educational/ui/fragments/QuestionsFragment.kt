package com.kabbodev.educational.ui.fragments

import android.animation.Animator
import android.app.Dialog
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.kabbodev.educational.R
import com.kabbodev.educational.data.model.Doubt
import com.kabbodev.educational.data.model.Question
import com.kabbodev.educational.data.model.QuestionByUser
import com.kabbodev.educational.data.model.User
import com.kabbodev.educational.databinding.FragmentQuestionsBinding
import com.kabbodev.educational.ui.base.BaseFragment
import com.kabbodev.educational.ui.interfaces.FirebaseCallback
import com.kabbodev.educational.ui.utils.createDialog
import com.kabbodev.educational.ui.utils.snackbar
import com.kabbodev.educational.ui.viewModels.DashboardViewModel

class QuestionsFragment : BaseFragment<FragmentQuestionsBinding, DashboardViewModel>(), FirebaseCallback {

    private val TAG = "Questions"
    private var queList: ArrayList<Question> = ArrayList()
    private var queListRecap: ArrayList<QuestionByUser> = ArrayList()
    private val doubtList: ArrayList<Int> = ArrayList()
    private val doubtQuestionList: ArrayList<Doubt> = ArrayList()
    private var queSize: Int? = null
    private var selectedOption: Int? = null
    private var currentQuesPos: Int = 0
    private lateinit var loadingDialog: Dialog

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentQuestionsBinding.inflate(inflater, container, false)

    override fun getViewModel() = DashboardViewModel::class.java

    override fun setupTheme() {
        loadingDialog = createDialog(requireContext(), R.layout.loading_progress_dialog, R.drawable.progress_circle, false)

        if (arguments?.getBoolean("recap_from_db") == true) {
            queListRecap = viewModel.getQuestionsListByRecap()
            Log.d(TAG, "list $queListRecap")

            updateUIRecap(queListRecap)

        } else {
            viewModel.getUserQueList().observe(viewLifecycleOwner, {
                it?.let { list ->
                    Log.d(TAG, "list $list")
                    queList = list
                    updateUI(list)
                }
            })
        }
    }

    override fun setupClickListeners() {
        if (arguments?.getBoolean("recap_from_db") == true) {

        } else {
            if (arguments?.getString("recap") != null && arguments?.getString("recap") == "yes") {

            } else {
                binding.option1.queContainer.setOnClickListener {
                    setAnswerLayout(newAns = 1)
                }
                binding.option2.queContainer.setOnClickListener {
                    setAnswerLayout(newAns = 2)
                }
                binding.option3.queContainer.setOnClickListener {
                    setAnswerLayout(newAns = 3)
                }
                binding.option4.queContainer.setOnClickListener {
                    setAnswerLayout(newAns = 4)
                }
            }
        }

        binding.infoBtn.setOnClickListener {
            if (doubtList.contains(currentQuesPos)) {
                binding.rootLayout.snackbar("You have already added it to the doubts list!")
            } else {
                onInfoBtnClick()
            }
        }
        binding.previousBtn.setOnClickListener {
            goToPreviousQues()
        }
        binding.nextBtn.setOnClickListener {
            onNextBtnClick()
        }
    }

    private fun onInfoBtnClick() {
        val clickListener: DialogInterface.OnClickListener =
            DialogInterface.OnClickListener { _, _ ->
                doubtList.add(currentQuesPos)
                if (arguments?.getBoolean("recap_from_db") == true) {
                    doubtQuestionList.add(
                        Doubt(
                            questionId = queListRecap[currentQuesPos].question_id!!,
                            solveLink = queListRecap[currentQuesPos].solve_link!!
                        )
                    )
                } else {
                    doubtQuestionList.add(
                        Doubt(
                            questionId = queList[currentQuesPos].question_id!!,
                            solveLink = queList[currentQuesPos].solve_link!!
                        )
                    )
                }
            }

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.alert))
            .setMessage(getString(R.string.doubt_alert))
            .setPositiveButton(getString(R.string.ok), clickListener)
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun onNextBtnClick() {
        if ((currentQuesPos + 1) == queSize) {
            updateAns()

            if (arguments?.getBoolean("recap_from_db") == true) {
                loadingDialog.show()
                viewModel.saveDoubts(doubtList = doubtQuestionList, listener = this)
            } else {
                if (arguments?.getString("recap_mark") == null) {
                    loadingDialog.show()
                    viewModel.saveDoubts(doubtList = doubtQuestionList, listener = this)
                } else {
                    navController.navigate(
                        R.id.action_questionsFragment_to_resultFragment,
                        bundleOf("recap_mark" to arguments?.getString("recap_mark"))
                    )
                }
            }
        } else {
            goToNextQues()
        }
    }

    private fun goToPreviousQues() {
        updateAns()
        currentQuesPos--
        resetAnsLayout()
        if (arguments?.getBoolean("recap_from_db") == true) {
            updateUIRecap(queListRecap)
        } else {
            updateUI(queList)
        }
    }

    private fun goToNextQues() {
        updateAns()
        currentQuesPos++
        resetAnsLayout()
        if (arguments?.getBoolean("recap_from_db") == true) {
            updateUIRecap(queListRecap)
        } else {
            updateUI(queList)
        }
    }

    private fun updateAns() {
        Log.d(TAG, "selectedOption $selectedOption")
        if (selectedOption == null) {
            viewModel.updateUserAnsList(currentQuesPos, "")
        } else {
            viewModel.updateUserAnsList(currentQuesPos, selectedOption.toString())
        }
        Log.d(TAG, "op ${viewModel.getUserQueList().value?.get(currentQuesPos)?.answer}")
    }

    private fun setAnswerLayout(newAns: Int) {
        if (selectedOption != newAns) {
            resetAnsLayout()
//            updateAnsImage(R.drawable.circle_box)
            selectedOption = newAns
            updateAnsImage(R.drawable.orange_tick)
        }
    }

    private fun updateAnsImage(drawable: Int) {
        when (selectedOption) {
            1 -> {
                binding.option1.optionImg.setImageResource(drawable)
            }
            2 -> {
                binding.option2.optionImg.setImageResource(drawable)
            }
            3 -> {
                binding.option3.optionImg.setImageResource(drawable)
            }
            4 -> {
                binding.option4.optionImg.setImageResource(drawable)
            }
        }
    }

    private fun resetAnsLayout() {
        binding.option1.optionImg.setImageResource(R.drawable.circle_box)
        binding.option2.optionImg.setImageResource(R.drawable.circle_box)
        binding.option3.optionImg.setImageResource(R.drawable.circle_box)
        binding.option4.optionImg.setImageResource(R.drawable.circle_box)
        selectedOption = null
    }

    private fun updateUI(list: ArrayList<Question>) {
        queSize = list.size
        Log.d(TAG, "current ques no: $currentQuesPos")

        binding.root.animate().setDuration(500).alpha(0f)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) {
                    Log.d(TAG, "anim started")
                }

                override fun onAnimationEnd(animation: Animator?) {
                    Log.d(TAG, "anim ended")
                    loadQuestions(list)
                }

                override fun onAnimationCancel(animation: Animator?) {
                    Log.d(TAG, "anim cancelled")
                }

                override fun onAnimationRepeat(animation: Animator?) {
                    Log.d(TAG, "anim repeated")
                }
            })
    }

    private fun updateUIRecap(list: ArrayList<QuestionByUser>) {
        queSize = list.size
        Log.d(TAG, "current ques no: $currentQuesPos")

        binding.root.animate().setDuration(500).alpha(0f)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) {
                    Log.d(TAG, "anim started")
                }

                override fun onAnimationEnd(animation: Animator?) {
                    Log.d(TAG, "anim ended")
                    loadQuestionsRecap(list)
                }

                override fun onAnimationCancel(animation: Animator?) {
                    Log.d(TAG, "anim cancelled")
                }

                override fun onAnimationRepeat(animation: Animator?) {
                    Log.d(TAG, "anim repeated")
                }
            })
    }

    private fun loadQuestions(list: ArrayList<Question>) {
        val listPos = list[currentQuesPos]

        if (currentQuesPos == 0) {
            binding.previousBtn.visibility = View.GONE
        } else {
            binding.previousBtn.visibility = View.VISIBLE
        }

        binding.questionId.text = String.format(getString(R.string.question_code), listPos.question_id)

        if (listPos.question_img == "") {
            binding.questionImage.visibility = View.GONE
        } else {
            binding.questionImage.visibility = View.VISIBLE
            Glide
                .with(requireContext())
                .load(listPos.question_img)
                .into(binding.questionImage)
        }

        binding.question.text = String.format(getString(R.string.question), (currentQuesPos + 1).toString(), listPos.question)

        updateOption(listPos = listPos, option = 1)
        updateOption(listPos = listPos, option = 2)
        updateOption(listPos = listPos, option = 3)
        updateOption(listPos = listPos, option = 4)

        resetAnsLayout()
        if (listPos.answer.isNullOrEmpty()) {
            Log.d(TAG, "Not answered!")
            resetAnsLayout()
        } else {
            setAnswerLayout(listPos.answer!!.toInt())
        }
        binding.rootLayout.alpha = 1f

        if (arguments?.getString("recap") != null && arguments?.getString("recap") == "yes") {
            val answerList = viewModel.getAnswerList()

            when (answerList[currentQuesPos].toInt()) {
                1 -> {
                    with(binding.option1.optionImg) {
                        setImageResource(R.drawable.orange_tick)
                        imageTintList = ContextCompat.getColorStateList(requireContext(), R.color.green)
                    }
                }
                2 -> {
                    with(binding.option1.optionImg) {
                        setImageResource(R.drawable.orange_tick)
                        imageTintList = ContextCompat.getColorStateList(requireContext(), R.color.green)
                    }
                }
                3 -> {
                    with(binding.option1.optionImg) {
                        setImageResource(R.drawable.orange_tick)
                        imageTintList = ContextCompat.getColorStateList(requireContext(), R.color.green)
                    }
                }
                4 -> {
                    with(binding.option1.optionImg) {
                        setImageResource(R.drawable.orange_tick)
                        imageTintList = ContextCompat.getColorStateList(requireContext(), R.color.green)
                    }
                }
            }
        }
    }

    private fun loadQuestionsRecap(list: ArrayList<QuestionByUser>) {
        val listPos = list[currentQuesPos]

        if (currentQuesPos == 0) {
            binding.previousBtn.visibility = View.GONE
        } else {
            binding.previousBtn.visibility = View.VISIBLE
        }

        binding.questionId.text = String.format(getString(R.string.question_code), listPos.question_id)

        if (listPos.question_img == "") {
            binding.questionImage.visibility = View.GONE
        } else {
            binding.questionImage.visibility = View.VISIBLE
            Glide
                .with(requireContext())
                .load(listPos.question_img)
                .into(binding.questionImage)
        }

        binding.question.text = String.format(getString(R.string.question), (currentQuesPos + 1).toString(), listPos.question)

        updateOptionRecap(listPos = listPos, option = 1)
        updateOptionRecap(listPos = listPos, option = 2)
        updateOptionRecap(listPos = listPos, option = 3)
        updateOptionRecap(listPos = listPos, option = 4)

        resetAnsLayout()
        if (listPos.answer.isNullOrEmpty()) {
            Log.d(TAG, "Not answered!")
            resetAnsLayout()
        } else {
            setAnswerLayout(listPos.answer!!.toInt())
        }

        binding.rootLayout.alpha = 1f

        when (list[currentQuesPos].correctAnswer!!.toInt()) {
            1 -> {
                binding.option1.optionImg.setImageResource(R.drawable.green_circle)
            }
            2 -> {
                binding.option2.optionImg.setImageResource(R.drawable.green_circle)
            }
            3 -> {
                binding.option3.optionImg.setImageResource(R.drawable.green_circle)
            }
            4 -> {
                binding.option4.optionImg.setImageResource(R.drawable.green_circle)
            }
        }
    }

    private fun updateOption(listPos: Question, option: Int) {
        val listPosOption: String?
        val listPosImage: String?
        val optionTextView: TextView?
        val optionImageView: ImageView?

        when (option) {
            1 -> {
                listPosOption = listPos.option_1
                listPosImage = listPos.option_1_img
                optionTextView = binding.option1.option
                optionImageView = binding.option1.image
            }
            2 -> {
                listPosOption = listPos.option_2
                listPosImage = listPos.option_2_img
                optionTextView = binding.option2.option
                optionImageView = binding.option2.image
            }
            3 -> {
                listPosOption = listPos.option_3
                listPosImage = listPos.option_3_img
                optionTextView = binding.option3.option
                optionImageView = binding.option3.image
            }
            else -> {
                listPosOption = listPos.option_4
                listPosImage = listPos.option_4_img
                optionTextView = binding.option4.option
                optionImageView = binding.option4.image
            }
        }


        if (listPosOption == "") {
            optionTextView.visibility = View.GONE
            optionImageView.visibility = View.VISIBLE

            Glide
                .with(requireContext())
                .load(listPosImage)
                .into(optionImageView)

        } else {
            optionTextView.visibility = View.VISIBLE
            optionTextView.text = listPosOption

            if (listPosImage == "") {
                optionImageView.visibility = View.GONE
            } else {
                optionImageView.visibility = View.VISIBLE
                Glide
                    .with(requireContext())
                    .load(listPosImage)
                    .into(optionImageView)
            }
        }
    }

    private fun updateOptionRecap(listPos: QuestionByUser, option: Int) {
        val listPosOption: String?
        val listPosImage: String?
        val optionTextView: TextView?
        val optionImageView: ImageView?

        when (option) {
            1 -> {
                listPosOption = listPos.option_1
                listPosImage = listPos.option_1_img
                optionTextView = binding.option1.option
                optionImageView = binding.option1.image
            }
            2 -> {
                listPosOption = listPos.option_2
                listPosImage = listPos.option_2_img
                optionTextView = binding.option2.option
                optionImageView = binding.option2.image
            }
            3 -> {
                listPosOption = listPos.option_3
                listPosImage = listPos.option_3_img
                optionTextView = binding.option3.option
                optionImageView = binding.option3.image
            }
            else -> {
                listPosOption = listPos.option_4
                listPosImage = listPos.option_4_img
                optionTextView = binding.option4.option
                optionImageView = binding.option4.image
            }
        }


        if (listPosOption == "") {
            optionTextView.visibility = View.GONE
            optionImageView.visibility = View.VISIBLE

            Glide
                .with(requireContext())
                .load(listPosImage)
                .into(optionImageView)

        } else {
            optionTextView.visibility = View.VISIBLE
            optionTextView.text = listPosOption

            if (listPosImage == "") {
                optionImageView.visibility = View.GONE
            } else {
                optionImageView.visibility = View.VISIBLE
                Glide
                    .with(requireContext())
                    .load(listPosImage)
                    .into(optionImageView)
            }
        }
    }

    override fun onSuccessListener(user: User?) {
        loadingDialog.dismiss()

        if (arguments?.getBoolean("recap_from_db") == true) {
            navController.navigate(R.id.action_questionsFragment_to_resultFragment, bundleOf("recap_from_db" to true))
        } else {
            navController.navigate(
                R.id.action_questionsFragment_to_resultFragment,
                bundleOf("subscription_id" to arguments?.getString("subscription_id"))
            )
        }
    }

    override fun onFailureListener(e: Exception) {
        loadingDialog.dismiss()
        binding.rootLayout.snackbar("Error! ${e.message}")
        Log.d(TAG, "e: ${e.message}")
    }

}
package com.kabbodev.educational.ui.fragments

import android.animation.Animator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.kabbodev.educational.R
import com.kabbodev.educational.data.model.Question
import com.kabbodev.educational.databinding.FragmentQuestionsBinding
import com.kabbodev.educational.ui.activities.MainActivity
import com.kabbodev.educational.ui.base.BaseFragment
import com.kabbodev.educational.ui.viewModels.DashboardViewModel

class QuestionsFragment : BaseFragment<FragmentQuestionsBinding, DashboardViewModel>() {

    private val TAG = "Questions"
    private lateinit var navController: NavController
    private var queSize: Int? = null
    private var selectedOption: Int? = null
    private var currentQuesPos: Int = 0

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentQuestionsBinding.inflate(inflater, container, false)

    override fun getViewModel() = DashboardViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        setupTheme()
        setupClickListeners()
    }

    private fun setupTheme() {
        viewModel.getUserQueList().observe(viewLifecycleOwner, {
            it?.let { list ->
                if (MainActivity.onResult) {
                    currentQuesPos = list.size - 1
                }
                updateUI(list)
            }
        })
    }

    private fun setupClickListeners() {
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

        binding.infoBtn.setOnClickListener {

        }
        binding.previousBtn.setOnClickListener {
            goToPreviousQues()
        }
        binding.nextBtn.setOnClickListener {
            if ((currentQuesPos + 1) == queSize) {
                updateAns()
                if (arguments?.getString("recap_mark") == null) {
                    navController.navigate(R.id.action_questionsFragment_to_resultFragment)
                } else {
                    navController.navigate(
                        R.id.action_questionsFragment_to_resultFragment,
                        bundleOf("recap_mark" to arguments?.getString("recap_mark"))
                    )
                }
            } else {
                goToNextQues()
            }
        }
    }

    private fun goToPreviousQues() {
        updateAns()
        currentQuesPos--
    }

    private fun goToNextQues() {
        updateAns()
        currentQuesPos++
    }

    private fun updateAns() {
        if (selectedOption == null) {
            viewModel.updateUserAnsList(currentQuesPos, "")
        } else {
            viewModel.updateUserAnsList(currentQuesPos, selectedOption.toString())
        }
    }

    private fun setAnswerLayout(newAns: Int) {
        if (selectedOption != newAns) {
            updateAnsImage(R.drawable.circle_box)
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
        updateAnsImage(R.drawable.circle_box)
        selectedOption = null
    }

    private fun updateUI(list: ArrayList<Question>) {
        queSize = list.size
        Log.d(TAG, "current ques no: $currentQuesPos")

        binding.rootLayout.animate().setDuration(500).alpha(0f)
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

    private fun loadQuestions(list: ArrayList<Question>) {
        val listPos = list[currentQuesPos]

        if (currentQuesPos == 0) {
            binding.previousBtn.visibility = View.GONE
        } else {
            binding.previousBtn.visibility = View.VISIBLE
        }

        binding.questionId.text =
            String.format(getString(R.string.question_code), listPos.question_id)

        if (listPos.question_img == "") {
            binding.questionImage.visibility = View.GONE
        } else {
            binding.questionImage.visibility = View.VISIBLE
            Glide
                .with(requireContext())
                .load(listPos.question_img)
                .into(binding.questionImage)
        }

        binding.question.text = String.format(
            getString(R.string.question),
            (currentQuesPos + 1).toString(),
            listPos.question
        )

        updateOption(listPos = listPos, option = 1)
        updateOption(listPos = listPos, option = 2)
        updateOption(listPos = listPos, option = 3)
        updateOption(listPos = listPos, option = 4)

        if (listPos.answer.isNullOrEmpty()) {
            Log.d(TAG, "Not answered!")
            resetAnsLayout()
        } else {
            setAnswerLayout(listPos.answer!!.toInt())
        }
        binding.rootLayout.alpha = 1f

        if (MainActivity.onResult) {
            MainActivity.onResult = false
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


}
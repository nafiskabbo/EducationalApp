package com.kabbodev.educational.ui.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidbuts.multispinnerfilter.KeyPairBoolData
import com.androidbuts.multispinnerfilter.MultiSpinnerListener
import com.androidbuts.multispinnerfilter.MultiSpinnerSearch
import com.google.android.material.button.MaterialButton
import com.kabbodev.educational.R
import com.kabbodev.educational.data.model.*
import com.kabbodev.educational.databinding.FragmentHomeBinding
import com.kabbodev.educational.ui.adapters.SubscriptionAdapter
import com.kabbodev.educational.ui.adapters.SubscriptionInterface
import com.kabbodev.educational.ui.base.BaseFragment
import com.kabbodev.educational.ui.interfaces.QuestionCallback
import com.kabbodev.educational.ui.utils.createDialog
import com.kabbodev.educational.ui.utils.snackbar
import com.kabbodev.educational.ui.viewModels.DashboardViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : BaseFragment<FragmentHomeBinding, DashboardViewModel>(), SubscriptionInterface, QuestionCallback {

    private val TAG = "Home"
    private var selectedPosOfSubs: Int = -1
    private lateinit var adapter: SubscriptionAdapter
    private var selectedChapterIds: ArrayList<String> = ArrayList()
    private var selectedChapterNames: ArrayList<String> = ArrayList()
    private var selectedQuestionsCount: Int? = null

    private lateinit var loadingDialog: Dialog
    private lateinit var settingsDialog: Dialog
    private var reset: Boolean = true

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentHomeBinding.inflate(inflater, container, false)

    override fun getViewModel() = DashboardViewModel::class.java

    override fun setupTheme() {
        loadingDialog = createDialog(requireContext(), R.layout.loading_progress_dialog, R.drawable.progress_circle, false)
        settingsDialog = createDialog(requireContext(), R.layout.dialog_open_subscription, R.drawable.slider_background, true)
        initRecyclerView()
        viewModel.getUser().observe(viewLifecycleOwner, { user ->
            user?.let {
                Log.d(TAG, "User $it")
                updateUI(it)
            }
        })

//        for (i in 1 until 12) {
//            for (k in 1 until 21) {
//
//                val min = 1
//                val max = 4
//                val random = Random.nextInt(max - min + 1) + min
//
//                val question = Question(
//                    answer = random.toString(),
//                    question = "Question is here? Do you know what is write 4 + 4?",
//                    question_id = UUID.randomUUID().toString(),
//                    question_img = "",
//                    option_1 = "√5a",
//                    option_1_img = "",
//                    option_2 = "√3a + π",
//                    option_2_img = "",
//                    option_3 = "5½",
//                    option_3_img = "",
//                    option_4 = "√2a",
//                    option_4_img = "",
//                    solve_link = "https://www.youtube.com/watch?v=OmJ-4B-mS-Y"
//                )
//
//                if (i < 10) {
//
//                    val queNo = if (k < 10) {
//                        "0$k"
//                    } else {
//                        k.toString()
//                    }
//                    Firebase.firestore.collection("Questions")
//                        .document("class_8_chapter_0$i").collection("Ques").document("Ques $queNo").set(question)
//
//                } else {
//
//                    val queNo = if (k < 10) {
//                        "0$k"
//                    } else {
//                        k.toString()
//                    }
//
//                    Firebase.firestore.collection("Questions")
//                        .document("class_8_chapter_$i").collection("Ques").document("Ques $queNo").set(question)
//                }
//
//            }
//        }

    }

    override fun setupClickListeners() {}

    private fun initRecyclerView() {
        with(binding.recyclerView) {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
        }
        adapter = SubscriptionAdapter(this, viewModel)
        binding.recyclerView.adapter = adapter
    }

    private fun updateUI(user: User) {
        if (user.subscriptions.isEmpty()) {
            binding.illustration.visibility = View.VISIBLE
            binding.noSubscriptions.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.illustration.visibility = View.GONE
            binding.noSubscriptions.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE

            viewModel.loadPlanDetail(user.subscriptions).observe(viewLifecycleOwner, { list ->
                list?.let {
                    adapter.updateList(it)
                }
            })
        }
    }

    override fun onStartBtnClick(itemPos: Int) {
        selectedPosOfSubs = itemPos

        if (adapter.subscriptionsList[itemPos].plan?.type == "Join") {
            navController.navigate(
                R.id.action_navigation_home_to_doubtViewFragment,
                bundleOf("que_solve_link" to adapter.subscriptionsList[itemPos].plan?.join_class_link)
            )
        } else {
            onSettingsBtnClick(itemPos)
        }
    }

    override fun onRecapBtnClick(itemPos: Int, recap: Recap, questionsList: ArrayList<QuestionByUser>) {
        viewModel.setRecapData(recap)
        viewModel.setQuestionsListByRecap(questionsList)

        navController.navigate(R.id.action_navigation_home_to_questionsFragment, bundleOf("recap_from_db" to true))
    }

    private fun onSettingsBtnClick(itemPos: Int) {
        loadingDialog.show()
        val subs = adapter.subscriptionsList[itemPos]
        settingsDialog.findViewById<TextView>(R.id.subs_title).text = subs.plan?.title

        updateDialog(subs.chapter as ArrayList<Chapter>)

        settingsDialog.findViewById<MaterialButton>(R.id.ok_btn).setOnClickListener {
            settingsDialog.dismiss()
            if (selectedChapterIds.size == 0) {
                binding.root.snackbar("Please select a chapter!")
            } else {
                loadQuestions()
            }
        }
    }

    private fun updateDialog(chapters: ArrayList<Chapter>) {
        val chapterArray: ArrayList<KeyPairBoolData> = ArrayList()

        lifecycleScope.launch(Dispatchers.IO) {
            val list: Set<String>? = userPreferences.selectedChapters.first()
            var arrayList: ArrayList<String>? = null

            withContext(Dispatchers.Main) {
                list?.let {
                    Log.d(TAG, "list : $it")
                    arrayList = ArrayList(it)
                    Log.d(TAG, "list : $arrayList")
                }
                chapters.forEachIndexed { index, chapter ->
                    val data = KeyPairBoolData()
                    data.id = index.toLong()
                    data.name = chapter.name!!.toString()
                    chapterArray.add(data)

                    arrayList?.let {
                        if (it.contains(data.name)) {
                            data.isSelected = true
                        }
                    }
                }

                val listener = MultiSpinnerListener { items ->
                    selectedChapterIds.clear()
                    selectedChapterNames.clear()
                    for (i in 0 until items.size) {
                        if (items[i].isSelected) {
                            Log.i(TAG, i.toString() + " : " + items[i].name + " : " + items[i].isSelected)
                            val itemName = items[i].name.toInt()
                            selectedChapterIds.add(chapters[itemName - 1].chapterId!!)
                            selectedChapterNames.add(itemName.toString())
                        }
                    }
                    val listSize = selectedChapterIds.size
                    settingsDialog.findViewById<TextView>(R.id.daily_que_num_spinner).text = listSize.toString()
                    selectedQuestionsCount = listSize
                }

                settingsDialog.findViewById<MultiSpinnerSearch>(R.id.multipleItemSelectionSpinner).setItems(chapterArray, listener)
                settingsDialog.findViewById<TextView>(R.id.daily_que_num_spinner).text = selectedChapterIds.size.toString()

                settingsDialog.findViewById<TextView>(R.id.daily_que_num_spinner).setOnClickListener {
                    if (selectedChapterIds.size == 0) {
                        binding.root.snackbar("Please select a chapter first!")
                    } else {
                        val optionsArray: Array<String> = if (selectedChapterIds.size < 7) {
                            arrayOf(
                                selectedChapterIds.size.toString(),
                                (selectedChapterIds.size * 2).toString(),
                                (selectedChapterIds.size * 3).toString()
                            )
                        } else {
                            arrayOf(selectedChapterIds.size.toString(), (selectedChapterIds.size * 2).toString())
                        }
                        showAlertDialog(optionsArray)
                    }
                }

                loadingDialog.dismiss()
                settingsDialog.show()
            }
        }
    }

    private fun showAlertDialog(optionsArray: Array<String>) {
        val onClick = DialogInterface.OnClickListener { dialog, which ->
            selectedQuestionsCount = optionsArray[which].toInt()
            settingsDialog.findViewById<TextView>(R.id.daily_que_num_spinner).text = selectedQuestionsCount.toString()
            dialog.dismiss()
        }

        var selectedPos = 0
        optionsArray.forEachIndexed { index, s ->
            if (s == settingsDialog.findViewById<TextView>(R.id.daily_que_num_spinner).text.toString()) {
                selectedPos = index
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Select question amount:")
            .setSingleChoiceItems(optionsArray, selectedPos, onClick)
            .setCancelable(true)
            .create()
            .show()
    }

    private fun loadQuestions() {
        viewModel.loadQuestionsList(selectedChapterIds = selectedChapterIds, selectedQuestionsCount!!, this)
    }

    private fun goToNextScreen(limitedList: ArrayList<Question>) {
        val ansList: ArrayList<String> = ArrayList()

        limitedList.forEach { que ->
            ansList.add(que.answer!!)
            Log.d(TAG, "ans new: ${que.answer}")
            que.answer = ""
        }

        viewModel.setUserQueList(limitedList)
        viewModel.setAnswerList(ansList)

        navController.navigate(
            R.id.action_navigation_home_to_questionsFragment,
            bundleOf("subscription_id" to adapter.subscriptionsList[selectedPosOfSubs].plan!!.id)
        )
    }

    override fun onSuccessListener(limitedList: ArrayList<Question>) {
        Log.d(TAG, "1. list $limitedList")
        Log.d(TAG, "2 set $selectedChapterNames")
        Log.d(TAG, "3 set ${selectedChapterNames.toSet()}")

        lifecycleScope.launch(Dispatchers.Main) {
            userPreferences.saveSelectedChapters(selectedChapterNames.toSet())
            goToNextScreen(limitedList)
        }
    }

    override fun onFailureListener(e: Exception) {
        Log.d(TAG, "Error ${e.message}")
        binding.root.snackbar("Error! ${e.message}")
    }

}
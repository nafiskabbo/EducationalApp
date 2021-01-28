package com.kabbodev.educational.ui.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.kabbodev.educational.R
import com.kabbodev.educational.data.model.Chapter
import com.kabbodev.educational.data.model.Question
import com.kabbodev.educational.data.model.User
import com.kabbodev.educational.databinding.FragmentHomeBinding
import com.kabbodev.educational.ui.adapters.SubscriptionAdapter
import com.kabbodev.educational.ui.adapters.SubscriptionInterface
import com.kabbodev.educational.ui.base.BaseFragment
import com.kabbodev.educational.ui.utils.GetTimeUtil
import com.kabbodev.educational.ui.utils.Listener
import com.kabbodev.educational.ui.utils.createDialog
import com.kabbodev.educational.ui.utils.snackbar
import com.kabbodev.educational.ui.viewModels.DashboardViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList

class HomeFragment : BaseFragment<FragmentHomeBinding, DashboardViewModel>(),
    SubscriptionInterface {

    private val TAG = "Home"
    private lateinit var navController: NavController
    private lateinit var adapter: SubscriptionAdapter
    private var selectedChapterId: String? = null
    private lateinit var loadingDialog: Dialog
    private lateinit var settingsDialog: Dialog
    private var reset: Boolean = true

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentHomeBinding.inflate(inflater, container, false)

    override fun getViewModel() = DashboardViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        setupTheme()
        setupClickListeners()
    }

    private fun setupTheme() {
        loadingDialog = createDialog(
            requireContext(),
            R.layout.loading_progress_dialog,
            R.drawable.slider_background,
            false
        )
        settingsDialog = createDialog(
            requireContext(),
            R.layout.dialog_open_subscription,
            R.drawable.slider_background,
            true
        )
        initRecyclerView()
        viewModel.getUser().observe(viewLifecycleOwner, { user ->
            user?.let {
                Log.d(TAG, "User $it")
                updateUI(it)
            }
        })


        val timeListener = object : Listener {
            override fun onTimeReceived(isAfterTime: Boolean?) {
                Log.d("time", "received: $isAfterTime")

                GlobalScope.launch(Dispatchers.Main) {
                    if (isAfterTime!!) {

                    } else {

                    }
                }
            }

            override fun onError(ex: Exception?) {
                Log.d("error", "error ${ex.toString()}")
            }
        }

        GetTimeUtil.getDate(timeListener)
    }

    private fun setupClickListeners() {

//        for (i in 1 until 21) {
//            val quesNo = if (i <= 9) {
//                "Ques 0$i"
//            } else {
//                "Ques $i"
//            }
//
//            val id = UUID.randomUUID().toString().substring(0, 8).replace("-", "0")
//            val queMap: MutableMap<String, String> = HashMap()
//            queMap["question"] = quesNo
//            queMap["answer"] = "2"
//            queMap["question_id"] = id
//            queMap["question_img"] = ""
//
//            queMap["option_1"] = "Option 1"
//            queMap["option_1_img"] = ""
//            queMap["option_2"] = "Option 2"
//            queMap["option_2_img"] = ""
//            queMap["option_3"] = "Option 3"
//            queMap["option_3_img"] = ""
//            queMap["option_4"] = "Option 4"
//            queMap["option_4_img"] = ""
//
//
//
//            Firebase.firestore.collection("Questions").document("1J7W2rc09BZFFoZNHNhl")
//                .collection("Ques")
//                .document(quesNo)
//                .set(queMap)
//                .addOnSuccessListener {
//                    binding.illustration.snackbar("Success!")
//                }
//        }


    }

    private fun initRecyclerView() {
        with(binding.recyclerView) {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
        }
        adapter = SubscriptionAdapter(this)
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

//    override fun onInfoBtnClick(itemPos: Int) {
//
//    }

    private fun onSettingsBtnClick(itemPos: Int) {
        loadingDialog.show()
        val subs = adapter.subscriptionsList[itemPos]
        settingsDialog.findViewById<TextView>(R.id.subs_title).text = subs.plan?.title

        updateDialog(subs.chapter as ArrayList<Chapter>)

        settingsDialog.findViewById<MaterialButton>(R.id.ok_btn).setOnClickListener {
            settingsDialog.dismiss()
            if (selectedChapterId == null) {
                binding.root.snackbar("Please select a chapter!")
            } else {
                loadQuestions()
            }
        }
    }

    private fun updateDialog(chapters: ArrayList<Chapter>) {
        val chapterArray: ArrayList<String> = ArrayList()
        chapters.forEach { chapter ->
            chapterArray.add(chapter.name!!.toString())
        }

        val arrayAdapter: ArrayAdapter<String> =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, chapterArray)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        with(settingsDialog.findViewById<Spinner>(R.id.spinner)) {
            adapter = arrayAdapter
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedChapterId = chapters[position].chapterId
                    Log.d(TAG, "selectedChapterId $selectedChapterId")
                    Log.d(TAG, "position $position")
                    Log.d(TAG, "count ${parent?.getItemAtPosition(position)}")
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Log.d(TAG, "onNothingSelected $selectedChapterId")
                }
            }
        }

//        val dailyQueArrayAdapter: ArrayAdapter<String> =
//            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, chapterArray)
//        dailyQueArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        with(settingsDialog.findViewById<Spinner>(R.id.daily_que_num_spinner)) {
//            adapter = dailyQueArrayAdapter
//            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//                override fun onItemSelected(
//                    parent: AdapterView<*>?,
//                    view: View?,
//                    position: Int,
//                    id: Long
//                ) {
////                    selectedChapterId = parent?.getItemAtPosition(position).toString()
////                    Log.d(TAG, "selectedChapterId $selectedChapterId")
//                }
//
//                override fun onNothingSelected(parent: AdapterView<*>?) {
//                    Log.d(TAG, "onNothingSelected $selectedChapterId")
//                }
//            }
//        }

        loadingDialog.dismiss()
        settingsDialog.show()
    }

    override fun onStartBtnClick(itemPos: Int) {
        Log.d(TAG, "des1 ${navController.currentDestination}")
        onSettingsBtnClick(itemPos)
    }

    private fun loadQuestions() {
        Log.d(TAG, "des2 ${navController.currentDestination}")
        viewModel.loadQuestionsList(chapterId = selectedChapterId!!)
            .observe(viewLifecycleOwner, {
                it?.let { list ->
                    goToNextScreen(list)
                }
            })
    }

    private fun goToNextScreen(list: ArrayList<Question>) {
        if (list.size > 0) {
            Log.d(TAG, "des3 ${navController.currentDestination}")
            val ansList: ArrayList<String> = ArrayList()

            list.forEach { que ->
                ansList.add(que.answer!!)
                Log.d(TAG, "ans new: ${que.answer}")
                que.answer = ""
            }

            viewModel.setAnswerList(ansList)

            Log.d(TAG, "des4 ${navController.currentDestination}")
            navController.navigate(R.id.action_navigation_home_to_questionsFragment)
        }
    }

}
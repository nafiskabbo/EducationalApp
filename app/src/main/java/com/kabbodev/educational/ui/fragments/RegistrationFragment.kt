package com.kabbodev.educational.ui.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.NavOptions
import com.kabbodev.educational.R
import com.kabbodev.educational.data.model.User
import com.kabbodev.educational.databinding.FragmentRegistrationBinding
import com.kabbodev.educational.ui.interfaces.FirebaseCallback
import com.kabbodev.educational.ui.base.BaseFragment
import com.kabbodev.educational.ui.utils.snackbar
import com.kabbodev.educational.ui.viewModels.LoginViewModel

class RegistrationFragment : BaseFragment<FragmentRegistrationBinding, LoginViewModel>(), FirebaseCallback {

    private var checkedClass = 0
    private var checkedClassValueStr: String? = null
    private var checkedBoard = 0
    private var checkedBoardValueStr: String? = null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentRegistrationBinding.inflate(inflater, container, false)

    override fun getViewModel() = LoginViewModel::class.java

    override fun setupTheme() {
        val textWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkInputs()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        binding.fullNameEt.editText?.addTextChangedListener(textWatcher)
        binding.emailEt.editText?.addTextChangedListener(textWatcher)
        binding.boardEt.editText?.addTextChangedListener(textWatcher)
        binding.classEt.editText?.addTextChangedListener(textWatcher)
        binding.passwordEt.editText?.addTextChangedListener(textWatcher)
        binding.confirmPasswordEt.editText?.addTextChangedListener(textWatcher)

        checkedBoardValueStr = getString(R.string.cbse)
        checkedClassValueStr = "6"

        binding.boardEt.editText?.setText(checkedBoardValueStr.toString())
        binding.classEt.editText?.setText(checkedClassValueStr.toString())

        binding.boardEt.editText?.inputType = InputType.TYPE_NULL
        binding.classEt.editText?.inputType = InputType.TYPE_NULL
    }

    override fun setupClickListeners() {
        binding.classClick.setOnClickListener {
            showClassAlertDialog()
        }
        binding.boardClick.setOnClickListener {
            showBoardAlertDialog()
        }
        binding.alreadyHaveAcc.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.registerBtn.setOnClickListener {
            checkEmailAndPassword()
        }
    }

    private fun checkInputs() {
        if (binding.fullNameEt.editText?.text!!.isNotEmpty() &&
            binding.emailEt.editText?.text!!.isNotEmpty() &&
            binding.boardEt.editText?.text!!.isNotEmpty() &&
            binding.classEt.editText?.text!!.isNotEmpty() &&
            binding.passwordEt.editText?.text!!.isNotEmpty() &&
            binding.passwordEt.editText?.text!!.length >= 8 &&
            binding.confirmPasswordEt.editText?.text!!.isNotEmpty()
        ) {
            binding.registerBtn.isEnabled = true
            binding.registerBtn.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
            return
        }
        binding.registerBtn.isEnabled = false
        binding.registerBtn.setTextColor(Color.argb(50, 255, 255, 255))
    }

    private fun showBoardAlertDialog() {
        val onClick = DialogInterface.OnClickListener { dialog, which ->
            if (checkedBoard != which) {
                checkedBoard = which
                when (which) {
                    0 -> {
                        checkedBoardValueStr = getString(R.string.cbse)
                    }
                    1 -> {
                        checkedBoardValueStr = getString(R.string.icse)
                    }
                }
                binding.boardEt.editText?.setText(checkedBoardValueStr)
            }
            dialog.dismiss()
        }

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.choose_board))
            .setSingleChoiceItems(R.array.boardArr, checkedBoard, onClick)
            .setCancelable(true)
            .create()
            .show()
    }

    private fun showClassAlertDialog() {
        val onClick = DialogInterface.OnClickListener { dialog, which ->
            if (checkedClass != which) {
                checkedClass = which
                when (which) {
                    0 -> {
                        checkedClassValueStr = getString(R.string.six)
                    }
                    1 -> {
                        checkedClassValueStr = getString(R.string.seven)
                    }
                    2 -> {
                        checkedClassValueStr = getString(R.string.eight)
                    }
                    3 -> {
                        checkedClassValueStr = getString(R.string.nine)
                    }
                    4 -> {
                        checkedClassValueStr = getString(R.string.ten)
                    }
                    5 -> {
                        checkedClassValueStr = getString(R.string.eleven)
                    }
                    6 -> {
                        checkedClassValueStr = getString(R.string.twelve)
                    }
                }
                binding.classEt.editText?.setText(checkedClassValueStr)
            }
            dialog.dismiss()
        }

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.choose_class))
            .setSingleChoiceItems(R.array.classArr, checkedClass, onClick)
            .setCancelable(true)
            .create()
            .show()
    }

    private fun checkEmailAndPassword() {
        if (Patterns.EMAIL_ADDRESS.matcher(binding.emailEt.editText?.text.toString()).matches()) {
            binding.emailEt.isErrorEnabled = false

            if (binding.passwordEt.editText?.text.toString() == binding.confirmPasswordEt.editText?.text.toString()) {
                binding.confirmPasswordEt.isErrorEnabled = false

                val user = User(
                    fullName = binding.fullNameEt.editText?.text.toString(),
                    email = binding.emailEt.editText?.text.toString(),
                    board = checkedBoardValueStr,
                    class_ = checkedClassValueStr,
                    password = binding.passwordEt.editText?.text.toString()
                )
                binding.registerBtn.startAnimation {
                    viewModel.registerUser(user, this)
                }

            } else {
                with(binding.confirmPasswordEt) {
                    isErrorEnabled = true
                    error = getString(R.string.password_doesnt_match)
                }
            }
        } else {
            with(binding.emailEt) {
                isErrorEnabled = true
                error = getString(R.string.invalid_email)
            }
        }
    }

    override fun onSuccessListener(user: User?) {
        binding.registerBtn.revertAnimation()

        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.slide_in_right)
            .setExitAnim(R.anim.slide_out_left)
            .setPopEnterAnim(R.anim.slide_in_left)
            .setPopExitAnim(R.anim.slide_out_right)
            .setPopUpTo(R.id.loginFragment, true)
            .build()

        navController.navigate(R.id.action_global_homeFragment, null, navOptions)
    }

    override fun onFailureListener(e: Exception) {
        binding.registerBtn.revertAnimation()
        binding.rootLayout.snackbar("Something went wrong! Error: ${e.message}")
    }

}
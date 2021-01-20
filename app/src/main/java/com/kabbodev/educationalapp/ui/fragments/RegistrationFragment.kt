package com.kabbodev.educationalapp.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.kabbodev.educationalapp.R
import com.kabbodev.educationalapp.data.model.User
import com.kabbodev.educationalapp.databinding.FragmentRegistrationBinding
import com.kabbodev.educationalapp.ui.`interface`.FirebaseCallback
import com.kabbodev.educationalapp.ui.base.BaseFragment
import com.kabbodev.educationalapp.ui.utils.snackbar
import com.kabbodev.educationalapp.ui.viewModels.LoginViewModel

class RegistrationFragment : BaseFragment<FragmentRegistrationBinding, LoginViewModel>(),
    FirebaseCallback {

    private lateinit var navController: NavController

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentRegistrationBinding.inflate(inflater, container, false)

    override fun getViewModel() = LoginViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        setupTheme()
        setupClickListeners()
    }

    private fun setupTheme() {
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
    }

    private fun setupClickListeners() {
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

    private fun checkEmailAndPassword() {
        val customErrorIcon = ContextCompat.getDrawable(requireContext(), R.drawable.error_icon)
        customErrorIcon!!.setBounds(-16, 0, customErrorIcon.intrinsicWidth - 16, customErrorIcon.intrinsicHeight)

        if (Patterns.EMAIL_ADDRESS.matcher(binding.emailEt.editText?.text.toString()).matches()) {
            binding.emailEt.isErrorEnabled = false

            if (binding.passwordEt.editText?.text.toString() == binding.confirmPasswordEt.editText?.text.toString()) {
                binding.confirmPasswordEt.isErrorEnabled = false

                val user = User(
                    fullName = binding.fullNameEt.editText?.text.toString(),
                    email = binding.emailEt.editText?.text.toString(),
                    board = binding.boardEt.editText?.text.toString(),
                    class_ = binding.classEt.editText?.text.toString(),
                    password = binding.passwordEt.editText?.text.toString()
                )
                binding.registerBtn.startAnimation {
                    viewModel.registerUser(user, this)
                }

            } else {
                binding.confirmPasswordEt.isErrorEnabled = true
                binding.confirmPasswordEt.error = getString(R.string.password_doesnt_match)
                binding.confirmPasswordEt.errorIconDrawable = customErrorIcon
            }
        } else {
            binding.emailEt.isErrorEnabled = true
            binding.emailEt.error = getString(R.string.invalid_email)
            binding.emailEt.errorIconDrawable = customErrorIcon
        }
    }

    override fun onSuccessListener() {
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
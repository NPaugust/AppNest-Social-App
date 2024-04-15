package com.avgust.appnest.auth

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.avgust.appnest.MainActivity
import com.avgust.appnest.R
import com.avgust.appnest.models.User
import com.avgust.appnest.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterFragment : Fragment() {

    companion object {
        const val TAG = "RegisterFragment"
    }

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginTextView.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.auth_fragmentContainer, LoginFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.btnSignup.setOnClickListener {
            val email = binding.emailText.editText?.text.toString()
            val name = binding.nameText.editText?.text.toString()
            val password = binding.passwordText.editText?.text.toString()
            val confirmPassword = binding.confirmPasswordText.editText?.text.toString()


            binding.emailText.error = null
            binding.nameText.error = null
            binding.passwordText.error = null
            binding.confirmPasswordText.error = null


            if (TextUtils.isEmpty(email)) {
                binding.emailText.error = "Email is required."
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.emailText.error = "Enter a valid email address"
                return@setOnClickListener
            }


            if (TextUtils.isEmpty(name)) {
                binding.nameText.error = "Name is required."
                return@setOnClickListener
            }


            if (TextUtils.isEmpty(password)) {
                binding.passwordText.error = "Password is required."
                return@setOnClickListener
            }

            if (!password.matches(passwordRegex)) {
                binding.passwordText.error =
                    "Password should contains minimum 8 character, at least one uppercase letter, one lowercase letter and one number:"
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(confirmPassword)) {
                binding.confirmPasswordText.error = "Confirm Password is required"
                return@setOnClickListener
            }


            if (password != confirmPassword) {
                binding.confirmPasswordText.error = "Password do not match"
                return@setOnClickListener
            }

            binding.signupProgressBar.visibility = View.VISIBLE



            val auth = FirebaseAuth.getInstance()


            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = User(auth.currentUser?.uid!!, name, email)
                        val firestore = FirebaseFirestore.getInstance().collection("Users")
                        firestore.document(auth.currentUser?.uid!!).set(user)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    startActivity(Intent(activity, MainActivity::class.java))
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Something went wrong, Please Try again.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    Log.d(TAG, it.exception.toString())
                                }
                            }
                        binding.signupProgressBar.visibility = View.GONE
                    } else {
                        binding.signupProgressBar.visibility = View.GONE
                        Toast.makeText(
                            context,
                            "Something went wrong, Please Try again.",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.d(TAG, task.exception.toString())
                    }
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
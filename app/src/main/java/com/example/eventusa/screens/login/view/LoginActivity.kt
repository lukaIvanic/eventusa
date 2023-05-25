package com.example.eventusa.screens.login.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.eventusa.R
import com.example.eventusa.app.EventusaApplication
import com.example.eventusa.caching.sharedprefs.LocalStorageManager
import com.example.eventusa.network.ResultOf
import com.example.eventusa.screens.events.view.EventsActivity
import com.example.eventusa.screens.login.viewmodel.LoginViewModel
import com.example.eventusa.screens.login.viewmodel.LoginViewModelFactory
import com.example.eventusa.utils.extensions.doIfFailure
import com.example.eventusa.utils.extensions.doIfSucces
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    lateinit var loginActivityLayout: ConstraintLayout

    lateinit var usernameEditText: EditText
    lateinit var passwordEditText: EditText

    lateinit var rememberMeCheckBox: CheckBox

    lateinit var loginButton: Button
    lateinit var progressIndicator: ProgressBar


    val viewmodel: LoginViewModel by viewModels {
        LoginViewModelFactory((application as EventusaApplication).userRepository)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginActivityLayout = findViewById(R.id.loginActivityLayout)

        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)

        rememberMeCheckBox = findViewById(R.id.rememberMeCheckBox)

        loginButton = findViewById(R.id.loginButton)
        progressIndicator = findViewById(R.id.progressIndicator)

        loginButton.setOnClickListener {
            handleLogin()
            hideKeyboard()
        }

        val user = LocalStorageManager.readUsername()
        val pass = LocalStorageManager.readPassword()

        val rememberMeEnabled = LocalStorageManager.readRememberMe()
        rememberMeCheckBox.isChecked = rememberMeEnabled

        if (true) {
//        if (LocalStorageManager.readRememberMe() && !user.isNullOrEmpty() && !pass.isNullOrEmpty()) {
            gotoEventsScreen()
            finish()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewmodel.loginState.collect { loginResult ->

                    if (loginResult is ResultOf.Loading) {
                        hideLoginText()
                        disableRememberMeCheckBox()
                        showProgressBar()
                        disableLoginButton()
                    }

                    loginResult.doIfFailure {
                        showLoginText()
                        enableRememberMeCheckBox()
                        hideProgressBar()
                        enableLoginButton()
                        showError(it.localizedMessage ?: "An error occured, please try again.")
                    }

                    loginResult.doIfSucces {

                        LocalStorageManager.saveUserAndPass(
                            usernameEditText.text.toString(),
                            passwordEditText.text.toString()
                        )

                        gotoEventsScreen()
                        finish()

                    }


                }
            }
        }


    }

    private fun handleLogin() {

        val username = usernameEditText.text.toString()
        val password = passwordEditText.text.toString()

        if (username.isEmpty() || password.isEmpty()) {
            Snackbar.make(
                loginActivityLayout,
                "Username and password are required.",
                Snackbar.LENGTH_SHORT
            ).show()
            return
        }

        if (rememberMeCheckBox.isChecked) {
            LocalStorageManager.turnOnRememberMe()
        } else {
            LocalStorageManager.turnOffRememberMe()
        }

        viewmodel.login(username, password)
    }


    private fun showError(message: String) {
        Snackbar.make(
            loginActivityLayout,
            message,
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun hideKeyboard(){
        this.currentFocus?.let{
            val manager: InputMethodManager = getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager
            manager
                .hideSoftInputFromWindow(
                    it.windowToken, 0
                )
        }
    }

    private fun enableRememberMeCheckBox(){
        rememberMeCheckBox.isEnabled = true
    }

    private fun disableRememberMeCheckBox(){
        rememberMeCheckBox.isEnabled = false
    }

    private fun showProgressBar() {
        progressIndicator.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        progressIndicator.visibility = View.INVISIBLE
    }

    private fun showLoginText() {
        loginButton.text = "Login"
    }

    private fun hideLoginText() {
        loginButton.text = ""
    }

    private fun enableLoginButton() {
        loginButton.isEnabled = true
    }

    private fun disableLoginButton() {
        loginButton.isEnabled = false
    }

    private fun gotoEventsScreen() {
        val intent = Intent(this@LoginActivity, EventsActivity::class.java)
        startActivity(intent)
    }

}
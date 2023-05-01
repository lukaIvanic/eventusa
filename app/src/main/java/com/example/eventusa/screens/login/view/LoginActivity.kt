package com.example.eventusa.screens.login.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {


    lateinit var usernameEditText: EditText
    lateinit var passwordEditText: EditText

    lateinit var rememberMeCheckBox: CheckBox

    lateinit var loginButton: Button
    lateinit var progressIndicator: ProgressBar

    lateinit var guestLoginButton: Button


    val viewmodel: LoginViewModel by viewModels {
        LoginViewModelFactory((application as EventusaApplication).userRepository)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)

        rememberMeCheckBox = findViewById(R.id.rememberMeCheckBox)

        loginButton = findViewById(R.id.loginButton)
        progressIndicator = findViewById(R.id.progressIndicator)

        guestLoginButton = findViewById(R.id.guestLoginButton)

        loginButton.setOnClickListener {
            handleLogin()
        }

        guestLoginButton.setOnClickListener {
            gotoEventsScreen()
            finish()
        }

        val rememberMeEnabled = LocalStorageManager.readRememberMe()
        rememberMeCheckBox.isChecked = rememberMeEnabled


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewmodel.loginState.collect { loginResult ->

                    if (loginResult is ResultOf.Loading) {
                        hideLoginText()
                        showProgressBar()
                        disableLoginButton()
                    }

                    loginResult.doIfFailure {
                        showLoginText()
                        hideProgressBar()
                        enableLoginButton()
                        showError(it.localizedMessage)
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

        automaticLogin()

    }

    private fun handleLogin() {

        val username = usernameEditText.text.toString()
        val password = passwordEditText.text.toString()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(
                this@LoginActivity,
                "Username and password are required.",
                Toast.LENGTH_LONG
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

    private fun automaticLogin() {
        if (!LocalStorageManager.readRememberMe()) return

        guestLoginButton.callOnClick()
        return

        val user = LocalStorageManager.readUsername()
        val pass = LocalStorageManager.readPassword()

        usernameEditText.setText(user)
        passwordEditText.setText(pass)

        loginButton.callOnClick()
    }

    private fun showError(message: String?) {
        Toast.makeText(
            this@LoginActivity,
            message ?: "An error occured, please try again.",
            Toast.LENGTH_LONG
        ).show()
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


//    private fun gotoEventsScreen() {
//        val intent = Intent(this@LoginActivity, EventsActivity::class.java)
//        startActivity(intent)
//    }

    private fun gotoEventsScreen() {
        val intent = Intent(this@LoginActivity, EventsActivity::class.java)
        startActivity(intent)
    }

}
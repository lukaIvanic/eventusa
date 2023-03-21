package com.example.eventusa.screens.login.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.eventusa.R
import com.example.eventusa.app.EventusaApplication
import com.example.eventusa.screens.events.view.EventsActivity
import com.example.eventusa.screens.login.viewmodel.LoginViewModel
import com.example.eventusa.screens.login.viewmodel.LoginViewModelFactory

class LoginActivity : AppCompatActivity() {


    lateinit var loginButton: Button
    lateinit var usernameEditText: EditText
    lateinit var passwordEditText: EditText
    lateinit var progressIndicator: ProgressBar


    val viewmodel: LoginViewModel by viewModels {
        LoginViewModelFactory((application as EventusaApplication).userRepository)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginButton = findViewById(R.id.loginButton)
        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        progressIndicator = findViewById(R.id.progressIndicator)

        loginButton.setOnClickListener {
            handleLogin()
        }

        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)


//
//
//        lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                viewmodel.loginState.collect { loginResult ->
//
//                    if (loginResult is ResultOf.Loading) {
//                        hideLoginText()
//                        showProgressBar()
//                        disableLoginButton()
//                    } else {
//                        showLoginText()
//                        hideProgressBar()
//                        enableLoginButton()
//                    }
//
//                    loginResult.doIfFailure {
//                        showError(it.localizedMessage)
//                    }
//
//                    loginResult.doIfSucces {
//
//                        gotoEventsScreen()
//                        // TODO: I DONT THINK THIS FINISHES THEA CTIVITY, RESEARCH THIS
//                        finish()
//
//                    }
//
//
//                }
//            }
//        }


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


    private fun handleLogin() {

        gotoEventsScreen()
        return

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


        viewmodel.login(username, password)
    }

    private fun gotoEventsScreen() {
        val intent = Intent(this@LoginActivity, EventsActivity::class.java)
        startActivity(intent)
    }

}
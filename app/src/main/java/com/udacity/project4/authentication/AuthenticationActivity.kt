package com.udacity.project4.authentication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig.EmailBuilder
import com.firebase.ui.auth.AuthUI.IdpConfig.GoogleBuilder
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityAuthenticationBinding
import com.udacity.project4.locationreminders.RemindersActivity


/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthenticationBinding

    private lateinit var sharedPreference: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)

        sharedPreference = getSharedPreferences("User", Context.MODE_PRIVATE)!!

        if (sharedPreference.getInt("status", 0) == 1) {
            startReminderActivity()
            Toast.makeText(
                applicationContext,
                "Welcome Back " + sharedPreference.getString("name", null),
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.loginButton.setOnClickListener {
            startSignIn()
        }

        val view = binding.root
        setContentView(view)
    }

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { result ->
        Log.i("Authentication", "Result: ${result}!")
        this.onSignInResult(result)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
//        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            val user = FirebaseAuth.getInstance().currentUser

            var editor = sharedPreference.edit()
            editor.putInt("status", 1)
            editor.putString("name", user?.displayName)
            editor.commit()

            Toast.makeText(this, "Successfully signed in", Toast.LENGTH_SHORT).show()
            startReminderActivity()

        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            Toast.makeText(this, "Sign in unsuccessful", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startSignIn() {
        var signInIntent =
            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(
                listOf(
                    GoogleBuilder().build(),
                    EmailBuilder().build()
                )
            ).setTheme(R.style.AppTheme).build()
        signInLauncher.launch(signInIntent)
    }

    private fun startReminderActivity() {
        finish()
        startActivity(Intent(this, RemindersActivity::class.java))
    }

}

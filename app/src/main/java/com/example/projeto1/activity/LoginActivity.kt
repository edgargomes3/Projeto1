package com.example.projeto1.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.projeto1.R
import com.example.projeto1.retrofit.LoginEndPoints
import com.example.projeto1.retrofit.LoginOutput
import com.example.projeto1.retrofit.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.MessageDigest
import android.content.Context
import android.content.SharedPreferences
import android.widget.CheckBox
import com.example.projeto1.retrofit.UserProfileEndPoints


class LoginActivity : AppCompatActivity() {

    private lateinit var editLoginEmail: EditText
    private lateinit var editLoginPassword: EditText
    private lateinit var autoLoginCheckbox: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        editLoginEmail = findViewById(R.id.editLoginEmail)
        editLoginPassword = findViewById(R.id.editLoginPassword)
        autoLoginCheckbox = findViewById(R.id.autoLoginCheckbox)

        val sharedPref: SharedPreferences = getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE )

        val userAutoLogin = sharedPref.getBoolean(getString(R.string.userAutoLogin), false)
        if( userAutoLogin ) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun login( view: View ) {
        val email = editLoginEmail.text.toString()
        val password = editLoginPassword.text.toString()

        if ( !email.isValidEmail() ) {
            return Toast.makeText(this@LoginActivity, R.string.emailEmptyLabel, Toast.LENGTH_LONG).show()
        }
        else if ( !password.isValidPassword() ) {
            return Toast.makeText(this@LoginActivity, R.string.passwordEmptyLabel, Toast.LENGTH_LONG).show()
        }
        else {

            val request = ServiceBuilder.buildService(LoginEndPoints::class.java)
            val call = request.postLogin(
                email,
                password.sha256()
            )

            call.enqueue(object : Callback<LoginOutput> {
                override fun onResponse(
                    call: Call<LoginOutput>,
                    response: Response<LoginOutput>
                ) {
                    if (response.isSuccessful) {
                        val c: LoginOutput = response.body()!!

                        if (c.success) {
                            Toast.makeText(
                                this@LoginActivity,
                                R.string.successLoginLabel,
                                Toast.LENGTH_SHORT
                            ).show()

                            val sharedPref: SharedPreferences = getSharedPreferences(
                                getString(R.string.preference_file_key), Context.MODE_PRIVATE )
                            with ( sharedPref.edit() ) {
                                putInt(getString(R.string.userProfileId), c.id )
                                if( autoLoginCheckbox.isChecked ) {
                                    putBoolean(getString(R.string.userAutoLogin), true)
                                }
                                commit()
                            }

                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else Toast.makeText(
                            this@LoginActivity,
                            R.string.noSuccessLoginLabel,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<LoginOutput>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    fun register( view: View ) {
        val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun String.isEmpty(): Boolean {
        return TextUtils.isEmpty(this)
    }

    private fun String.isValidEmail(): Boolean {
        return !this.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }

    private fun String.isValidPassword(): Boolean {
        return !this.isEmpty()
    }

    private fun String.sha256(): String {
        return hashString(this)
    }

    private fun hashString(input: String): String {
        return MessageDigest
            .getInstance("SHA-256")
            .digest(input.toByteArray())
            .fold("", { str, it -> str + "%02x".format(it) })
    }
}
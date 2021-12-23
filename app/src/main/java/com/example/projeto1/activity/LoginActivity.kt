package com.example.projeto1.activity

import android.R.attr
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
import com.example.projeto1.retrofit.LoginOutputPost
import com.example.projeto1.retrofit.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.MessageDigest
import android.R.attr.password
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import java.util.regex.Matcher
import java.util.regex.Pattern


class LoginActivity : AppCompatActivity() {

    private lateinit var editLoginEmail: EditText
    private lateinit var editLoginPassword: EditText

    private val INTERNET_PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        editLoginEmail = findViewById(R.id.editLoginEmail)
        editLoginPassword = findViewById(R.id.editLoginPassword)
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

            call.enqueue(object : Callback<LoginOutputPost> {
                override fun onResponse(
                    call: Call<LoginOutputPost>,
                    response: Response<LoginOutputPost>
                ) {
                    if (response.isSuccessful) {
                        val c: LoginOutputPost = response.body()!!

                        if (c.success) {
                            Toast.makeText(
                                this@LoginActivity,
                                R.string.successLoginLabel,
                                Toast.LENGTH_SHORT
                            ).show()

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

                override fun onFailure(call: Call<LoginOutputPost>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    fun register( view: View ) {
        val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
        startActivity(intent)
        finish()
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
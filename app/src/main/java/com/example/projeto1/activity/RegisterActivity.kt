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

class RegisterActivity : AppCompatActivity() {

    private lateinit var editRegisterEmail: EditText
    private lateinit var editRegisterPassword: EditText
    private lateinit var editRegisterConfirmPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        editRegisterEmail = findViewById(R.id.editRegisterEmail)
        editRegisterPassword = findViewById(R.id.editRegisterPassword)
        editRegisterConfirmPassword = findViewById(R.id.editRegisterConfirmPassword)
    }

    fun register( view: View) {
        val email = editRegisterEmail.text.toString()
        val password = editRegisterPassword.text.toString()
        val cpassword = editRegisterConfirmPassword.text.toString()

        if ( !email.isValidEmail() ) {
            return Toast.makeText(this@RegisterActivity, R.string.emailEmptyLabel, Toast.LENGTH_LONG).show()
        }
        else if ( !password.isValidPassword() || !cpassword.isValidPassword() ) {
            return Toast.makeText(this@RegisterActivity, R.string.passwordEmptyLabel, Toast.LENGTH_LONG).show()
        }
        else if ( !TextUtils.equals( password, cpassword ) ) {
            return Toast.makeText(this@RegisterActivity, R.string.passwordMatch, Toast.LENGTH_LONG).show()
        }
        else {

            val request = ServiceBuilder.buildService(LoginEndPoints::class.java)
            val call = request.postRegister(
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
                                this@RegisterActivity,
                                R.string.successRegisterLabel,
                                Toast.LENGTH_SHORT
                            ).show()

                            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else Toast.makeText(
                            this@RegisterActivity,
                            R.string.noSuccessRegisterLabel,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<LoginOutput>, t: Throwable) {
                    Toast.makeText(this@RegisterActivity, "${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
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
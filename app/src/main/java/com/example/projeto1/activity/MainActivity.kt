package com.example.projeto1.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.example.projeto1.R
import com.example.projeto1.retrofit.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.maintenance)

        val sharedPref: SharedPreferences = getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE )

        val userProfileId = sharedPref.getInt(getString(R.string.userProfileId), -1)
        if( userProfileId != -1 ) {
            val request = ServiceBuilder.buildService(UserProfileEndPoints::class.java)
            val call = request.checkEmptyUserProfile(
                userProfileId
            )

            call.enqueue(object : Callback<UserProfileOutput> {
                override fun onResponse(
                    call: Call<UserProfileOutput>,
                    response: Response<UserProfileOutput>
                ) {
                    if (response.isSuccessful) {
                        val c: UserProfileOutput = response.body()!!

                        if (c.success) {
                            val intent = Intent(this@MainActivity, EditUserProfileActivity::class.java)
                            intent.putExtra( EditUserProfileActivity.EXTRA_EMPTY, true);
                            startActivity(intent)
                            finish()
                        }
                    }
                }

                override fun onFailure(call: Call<UserProfileOutput>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    fun edit( view: View ) {
        val intent = Intent(this@MainActivity, EditUserProfileActivity::class.java)
        startActivity(intent)
        finish()
    }
}
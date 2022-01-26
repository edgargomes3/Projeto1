package com.example.projeto1.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_main_btn -> {
                true
            }
            R.id.icr_main_btn -> {
                true
            }
            R.id.isf_main_btn -> {
                true
            }
            R.id.profile_main_btn -> {
                val intent = Intent(this@MainActivity, EditUserProfileActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.logout_main_btn -> {
                val sharedPref: SharedPreferences = getSharedPreferences(
                    getString(R.string.preference_file_key), Context.MODE_PRIVATE )
                with ( sharedPref.edit() ) {
                    putInt(getString(R.string.userProfileId), -1 )
                    putBoolean(getString(R.string.userProfileId), false)
                    commit()
                }

                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
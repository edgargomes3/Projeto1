package com.example.projeto1.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.*
import com.example.projeto1.R
import com.example.projeto1.retrofit.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private var icr: Int = 0
    private var isf: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.maintenance)

        val sharedPref: SharedPreferences = getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE )

        val userProfileId = sharedPref.getInt(getString(R.string.userProfileId), -1)
        if( userProfileId != -1 ) {
            checkUserProfile( userProfileId )
            getICR( userProfileId )
            getISF( userProfileId )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.bolus_main_btn -> {
                val intent = Intent(this@MainActivity, BolusActivity::class.java)
                startActivity(intent)
                finish()
                true
            }
            R.id.glicemia_main_btn -> {
                true
            }
            R.id.icr_main_btn -> {
                showCustomDialog( "ICR" )
                true
            }
            R.id.isf_main_btn -> {
                showCustomDialog( "ISF" )
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
                    putBoolean(getString(R.string.userAutoLogin), false)
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

    fun showCustomDialog( title: String ) {
        var dialog = Dialog(this@MainActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.custom_dialog)

        val dialogTitle = dialog.findViewById<TextView>(R.id.dialogTitle)
        val dialogEditText = dialog.findViewById<EditText>(R.id.dialogEditText)
        val dialog_btn = dialog.findViewById<Button>(R.id.dialog_btn)

        dialogTitle.text = "Submeter ${title}"
        if( TextUtils.equals( "ICR", title ) ) {
            if( icr != 0 ) dialogEditText.hint = "Valor Atual: $icr"
        }
        else {
            if( isf != 0 ) dialogEditText.hint = "Valor Atual: $isf"
        }

        dialog_btn.setOnClickListener{
            var valor = dialogEditText.text.toString()

            if ( TextUtils.isEmpty( valor ) ) {
                Toast.makeText(this@MainActivity, R.string.valueEmptyLabel, Toast.LENGTH_SHORT).show()
            }
            else {
                val valorInt = valor.toInt()
                var call: Call<ValuesOutput>

                val sharedPref: SharedPreferences = getSharedPreferences(
                    getString(R.string.preference_file_key), Context.MODE_PRIVATE )

                val userProfileId = sharedPref.getInt(getString(R.string.userProfileId), -1)

                val request = ServiceBuilder.buildService(ValuesEndPoints::class.java)
                if( TextUtils.equals( "ICR", title ) ) {
                    call = request.postICR(
                        userProfileId,
                        valorInt
                    )
                }
                else{
                    call = request.postISF(
                        userProfileId,
                        valorInt
                    )
                }

                call.enqueue(object: Callback<ValuesOutput> {
                    override fun onResponse(call: Call<ValuesOutput>, response: Response<ValuesOutput>) {
                        if (response.isSuccessful) {
                            val c: ValuesOutput = response.body()!!

                            if (c.success) {
                                Toast.makeText(this@MainActivity, R.string.valueSavedLabel, Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                            }
                            else {
                                Toast.makeText(this@MainActivity, R.string.errorOcurredLabel, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<ValuesOutput>, t: Throwable) {
                        Toast.makeText(this@MainActivity, "${t.message}", Toast.LENGTH_SHORT).show()
                    }

                })
            }
        }

        dialog.show()
    }

    fun checkUserProfile( identifier: Int ) {
        val request = ServiceBuilder.buildService(UserProfileEndPoints::class.java)
        val call = request.checkEmptyUserProfile(
            identifier
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

    fun getICR( identifier: Int ) {
        val request = ServiceBuilder.buildService(ValuesEndPoints::class.java)
        val call = request.getICR(
            identifier
        )

        call.enqueue(object : Callback<ICROutput> {
            override fun onResponse(call: Call<ICROutput>, response: Response<ICROutput>) {
                if (response.isSuccessful) {
                    val c: ICROutput = response.body()!!

                    if (c.icr_value != 0) {
                        icr = c.icr_value
                    }
                }
            }

            override fun onFailure(call: Call<ICROutput>, t: Throwable) {
                Toast.makeText(this@MainActivity, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun getISF( identifier: Int ) {
        val request = ServiceBuilder.buildService(ValuesEndPoints::class.java)
        val call = request.getISF(
            identifier
        )

        call.enqueue(object : Callback<ISFOutput> {
            override fun onResponse(
                call: Call<ISFOutput>,
                response: Response<ISFOutput>
            ) {
                if (response.isSuccessful) {
                    val c: ISFOutput = response.body()!!

                    if (c.isf_value != 0) {
                        isf = c.isf_value
                    }
                }
            }

            override fun onFailure(call: Call<ISFOutput>, t: Throwable) {
                Toast.makeText(this@MainActivity, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
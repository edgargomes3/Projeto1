package com.example.projeto1.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.example.projeto1.R
import com.example.projeto1.retrofit.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BolusActivity : AppCompatActivity() {

    private lateinit var maxCHOErrorTextView: TextView
    private lateinit var CHOEditText: EditText
    private lateinit var GlicemiaEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bolus)

        maxCHOErrorTextView = findViewById(R.id.maxCHOErrorTextView)
        CHOEditText = findViewById(R.id.CHOEditText)
        GlicemiaEditText = findViewById(R.id.GlicemiaEditText)

        val sharedPref: SharedPreferences = getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE )

        val userProfileId = sharedPref.getInt(getString(R.string.userProfileId), -1)
        if( userProfileId != -1 ) {
            getErrorCHO( userProfileId )
            maxCHOErrorTextView.tag = userProfileId
        }
    }

    fun getErrorCHO( identifier: Int ) {
        val request = ServiceBuilder.buildService(ValuesEndPoints::class.java)
        val call = request.getErrorCHO(
            identifier
        )

        call.enqueue(object : Callback<ErrorCHOOutput> {
            override fun onResponse(
                call: Call<ErrorCHOOutput>,
                response: Response<ErrorCHOOutput>
            ) {
                if (response.isSuccessful) {
                    val c: ErrorCHOOutput = response.body()!!

                    if (c.success) {
                        maxCHOErrorTextView.text = c.value.toString()
                    }
                }
            }

            override fun onFailure(call: Call<ErrorCHOOutput>, t: Throwable) {
                Toast.makeText(this@BolusActivity, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun calculateBolus( view: View ) {
        if ( TextUtils.isEmpty(CHOEditText.text) ) {
            Toast.makeText(this@BolusActivity, R.string.emptyCHOLabel, Toast.LENGTH_LONG).show()
            return
        }
        else if ( TextUtils.isEmpty(GlicemiaEditText.text) ) {
            Toast.makeText(this@BolusActivity, R.string.emptyGlicemiaLabel, Toast.LENGTH_LONG).show()
            return
        }
        else {
            val request = ServiceBuilder.buildService(ValuesEndPoints::class.java)
            val call = request.postBolus(
                maxCHOErrorTextView.tag.toString().toInt(),
                CHOEditText.text.toString().toInt(),
                GlicemiaEditText.text.toString().toInt(),
                maxCHOErrorTextView.text.toString().toFloat()
            )

            call.enqueue(object : Callback<BolusOutput> {
                override fun onResponse(
                    call: Call<BolusOutput>,
                    response: Response<BolusOutput>
                ) {
                    if (response.isSuccessful) {
                        val c: BolusOutput = response.body()!!

                        if (c.success) {
                            Toast.makeText( this@BolusActivity, "Bolus: ${c.value}", Toast.LENGTH_SHORT ).show()

                            val intent = Intent(this@BolusActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }

                override fun onFailure(call: Call<BolusOutput>, t: Throwable) {
                    Toast.makeText(this@BolusActivity, "${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
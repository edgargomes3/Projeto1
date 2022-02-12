package com.example.projeto1.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
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

    private var userID: Int = -1

    private var limit_inf: Int = -1
    private var limit_sup: Int = -1
    private var target: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bolus)

        maxCHOErrorTextView = findViewById(R.id.maxCHOErrorTextView)
        CHOEditText = findViewById(R.id.CHOEditText)
        GlicemiaEditText = findViewById(R.id.GlicemiaEditText)

        val intent = intent
        limit_inf = intent.getIntExtra(EXTRA_INF, -1)
        limit_sup = intent.getIntExtra(EXTRA_SUP, -1)
        target = intent.getIntExtra(EXTRA_TARGET, -1)

        val sharedPref: SharedPreferences = getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE )

        userID = sharedPref.getInt(getString(R.string.userProfileId), -1)
        if( userID != -1 ) {
            getErrorCHO( userID )
        }
    }

    fun getErrorCHO( identifier: Int ) {
        val request = ServiceBuilder.buildService(ValuesEndPoints::class.java)
        val call = request.getErrorCHO(
            identifier,
            limit_inf,
            limit_sup,
            target
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
                userID,
                CHOEditText.text.toString().toInt(),
                GlicemiaEditText.text.toString().toInt(),
                target,
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

    companion object {
        const val EXTRA_INF = "com.example.projeto1.activity.bolusactivity.EXTRA_INF"
        const val EXTRA_SUP = "com.example.projeto1.activity.bolusactivity.EXTRA_SUP"
        const val EXTRA_TARGET = "com.example.projeto1.activity.bolusactivity.EXTRA_TARGET"
    }
}
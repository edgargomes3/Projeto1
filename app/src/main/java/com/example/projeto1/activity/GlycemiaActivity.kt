package com.example.projeto1.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.projeto1.R
import com.example.projeto1.retrofit.BolusOutput
import com.example.projeto1.retrofit.ServiceBuilder
import com.example.projeto1.retrofit.ValuesEndPoints
import com.example.projeto1.retrofit.ValuesOutput
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GlycemiaActivity : AppCompatActivity() {

    private lateinit var glycemiaValueEditText: EditText

    private var userID: Int = -1

    private var limit_inf: Int = -1
    private var limit_sup: Int = -1
    private var target: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_glycemia)

        glycemiaValueEditText = findViewById(R.id.glycemiaValueEditText)

        val intent = intent
        limit_inf = intent.getIntExtra(EXTRA_INF, -1)
        target = intent.getIntExtra(EXTRA_TARGET, -1)
        limit_sup = intent.getIntExtra(EXTRA_SUP, -1)

        val sharedPref: SharedPreferences = getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE )

        userID = sharedPref.getInt(getString(R.string.userProfileId), -1)
    }

    fun saveGlicemia( view: View ) {
        if ( TextUtils.isEmpty(glycemiaValueEditText.text) ) {
            Toast.makeText(this@GlycemiaActivity, R.string.emptyCHOLabel, Toast.LENGTH_LONG).show()
            return
        }
        else {
            val value = glycemiaValueEditText.text.toString().toInt()

            val request = ServiceBuilder.buildService(ValuesEndPoints::class.java)
            val call = request.postGlicemia(
                userID,
                value,
                limit_inf,
                limit_sup,
                target,
            )

            call.enqueue(object : Callback<ValuesOutput> {
                override fun onResponse(call: Call<ValuesOutput>, response: Response<ValuesOutput>) {
                    if (response.isSuccessful) {
                        val c: ValuesOutput = response.body()!!

                        if (c.success) {
                            Toast.makeText( this@GlycemiaActivity, "Valor glicémico guardado.", Toast.LENGTH_SHORT ).show()

                            if( value < 70 ) Toast.makeText( this@GlycemiaActivity, "[AVISO] Hypoglicemia", Toast.LENGTH_SHORT ).show()
                            if( value > 180 ) Toast.makeText( this@GlycemiaActivity, "[AVISO] Hyperglicemia", Toast.LENGTH_SHORT ).show()

                            val intent = Intent(this@GlycemiaActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }

                override fun onFailure(call: Call<ValuesOutput>, t: Throwable) {
                    Toast.makeText(this@GlycemiaActivity, "${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    companion object {
        const val EXTRA_INF = "com.example.projeto1.activity.glycemiaactivity.EXTRA_INF"
        const val EXTRA_SUP = "com.example.projeto1.activity.glycemiaactivity.EXTRA_SUP"
        const val EXTRA_TARGET = "com.example.projeto1.activity.glycemiaactivity.EXTRA_TARGET"
    }
}
package com.example.projeto1.activity

import android.app.TimePickerDialog
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
import java.text.SimpleDateFormat
import java.util.*

class TimeActivity : AppCompatActivity() {
    private var userProfileId: Int = -1

    private lateinit var CHOPAEditText: EditText
    private lateinit var HourPAEditText: EditText
    private lateinit var CHOAEditText: EditText
    private lateinit var HourAEditText: EditText
    private lateinit var CHOLEditText: EditText
    private lateinit var HourLEditText: EditText
    private lateinit var CHOJEditText: EditText
    private lateinit var HourJEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time)

        CHOPAEditText = findViewById(R.id.CHOPAEditText)
        HourPAEditText = findViewById(R.id.HourPAEditText)
        CHOAEditText = findViewById(R.id.CHOAEditText)
        HourAEditText = findViewById(R.id.HourAEditText)
        CHOLEditText = findViewById(R.id.CHOLEditText)
        HourLEditText = findViewById(R.id.HourLEditText)
        CHOJEditText = findViewById(R.id.CHOJEditText)
        HourJEditText = findViewById(R.id.HourJEditText)

        val sharedPref: SharedPreferences = getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE )

        userProfileId = sharedPref.getInt(getString(R.string.userProfileId), -1)
    }

    fun chooseHour( view: View) {
        val field = findViewById<EditText>( view.id )

        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            field.setText( SimpleDateFormat("HH:mm").format(cal.time) )
        }
        TimePickerDialog(this, timeSetListener, 0, 0, true).show()
    }

    fun saveTime( view: View ) {
        val CHOPAText = CHOPAEditText.text.toString()
        val HourPAText = HourPAEditText.text.toString()
        val CHOAText = CHOAEditText.text.toString()
        val HourAText = HourAEditText.text.toString()
        val CHOLText = CHOLEditText.text.toString()
        val HourLText = HourLEditText.text.toString()
        val CHOJText = CHOJEditText.text.toString()
        val HourJText = HourJEditText.text.toString()

        if ( CHOPAText.isEmpty() || HourPAText.isEmpty() || CHOAText.isEmpty() || HourAText.isEmpty() || CHOLText.isEmpty() || HourLText.isEmpty() || CHOJText.isEmpty() || HourJText.isEmpty()) {
            Toast.makeText(this@TimeActivity, R.string.allParameterNotEmptyLabel, Toast.LENGTH_LONG).show()
            return
        }
        else {
            val request = ServiceBuilder.buildService(ValuesEndPoints::class.java)
            val call = request.postTime(
                userProfileId,
                CHOPAText.toInt(),
                HourPAText,
                CHOAText.toInt(),
                HourAText,
                CHOLText.toInt(),
                HourLText,
                CHOJText.toInt(),
                HourJText
            )

            call.enqueue(object : Callback<ValuesOutput> {
                override fun onResponse(
                    call: Call<ValuesOutput>,
                    response: Response<ValuesOutput>
                ) {
                    if (response.isSuccessful) {
                        val c: ValuesOutput = response.body()!!

                        if (c.success) {
                            Toast.makeText( this@TimeActivity, R.string.successTimeSaveLabel, Toast.LENGTH_SHORT ).show()

                            val intent = Intent(this@TimeActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }

                override fun onFailure(call: Call<ValuesOutput>, t: Throwable) {
                    Toast.makeText(this@TimeActivity, "${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun String.isEmpty(): Boolean {
        return TextUtils.isEmpty(this)
    }
}
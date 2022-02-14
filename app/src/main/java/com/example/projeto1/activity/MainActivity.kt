package com.example.projeto1.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.constraintlayout.motion.utils.Easing
import com.example.projeto1.R
import com.example.projeto1.dataclass.Glicemia
import com.example.projeto1.retrofit.*
import com.github.mikephil.charting.animation.Easing.EaseInExpo
import com.github.mikephil.charting.animation.Easing.EaseInSine
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private var userProfileId: Int = -1

    private lateinit var lineChart: LineChart

    private var icr: Int = 0
    private var isf: Int = 0

    private var limit_inf: Int = 70
    private var limit_sup: Int = 180
    private var target: Int = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPref: SharedPreferences = getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE )

        lineChart = findViewById(R.id.lineChart)

        userProfileId = sharedPref.getInt(getString(R.string.userProfileId), -1)
        val val_limit_inf = sharedPref.getInt(getString(R.string.limit_inf), -1)
        val val_limit_sup = sharedPref.getInt(getString(R.string.limit_sup), -1)
        val val_target = sharedPref.getInt(getString(R.string.target), -1)

        if( val_limit_inf != -1 ) limit_inf = val_limit_inf
        if( val_limit_sup != -1 ) limit_sup = val_limit_sup
        if( val_target != -1 ) target = val_target

        if( userProfileId != -1 ) {
            checkUserProfile( userProfileId )

            getICR( userProfileId )
            getISF( userProfileId )

            getChartValues()
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
                intent.putExtra(BolusActivity.EXTRA_INF, limit_inf)
                intent.putExtra(BolusActivity.EXTRA_SUP, limit_sup)
                intent.putExtra(BolusActivity.EXTRA_TARGET, target)
                startActivity(intent)
                true
            }
            R.id.glicemia_main_btn -> {
                val intent = Intent(this@MainActivity, GlycemiaActivity::class.java)
                intent.putExtra(GlycemiaActivity.EXTRA_INF, limit_inf)
                intent.putExtra(GlycemiaActivity.EXTRA_SUP, limit_sup)
                intent.putExtra(GlycemiaActivity.EXTRA_TARGET, target)
                startActivity(intent)
                true
            }
            R.id.glicemiapar_main_btn -> {
                showCustomDialog2()
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
                    putInt(getString(R.string.limit_inf), -1 )
                    putInt(getString(R.string.limit_sup), -1 )
                    putInt(getString(R.string.target), -1 )
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

            if ( valor.isEmpty() ) {
                Toast.makeText(this@MainActivity, R.string.valueEmptyLabel, Toast.LENGTH_SHORT).show()
            }
            else {
                val valorInt = valor.toInt()
                var call: Call<ValuesOutput>

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

    fun showCustomDialog2( ) {
        var dialog = Dialog(this@MainActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.custom_dialog2)

        val dialogGlycemiaLimitInfEditText = dialog.findViewById<EditText>(R.id.glycemiaLimitInfEditText)
        val dialogGlycemiaLimitSupEditText = dialog.findViewById<EditText>(R.id.glycemiaLimitSupEditText)
        val dialogGlycemiaTargetEditText = dialog.findViewById<EditText>(R.id.glycemiaTargetEditText)
        val dialog2_btn = dialog.findViewById<Button>(R.id.dialog2_btn)

        dialogGlycemiaLimitInfEditText.hint = "Valor Atual: $limit_inf"
        dialogGlycemiaLimitSupEditText.hint = "Valor Atual: $limit_sup"
        dialogGlycemiaTargetEditText.hint = "Valor Atual: $target"


        dialog2_btn.setOnClickListener{
            var valor_limit_inf = dialogGlycemiaLimitInfEditText.text.toString()
            var valor_limit_sup = dialogGlycemiaLimitSupEditText.text.toString()
            var valor_target = dialogGlycemiaTargetEditText.text.toString()

            if ( valor_limit_inf.isEmpty() && valor_limit_sup.isEmpty() && valor_target.isEmpty() ) {
                Toast.makeText(this@MainActivity, R.string.allParameterEmptyLabel, Toast.LENGTH_SHORT).show()
            }
            else {
                val sharedPref: SharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE )
                with ( sharedPref.edit() ) {

                    if( !valor_limit_inf.isEmpty() ) {
                        limit_inf = valor_limit_inf.toInt()
                        putInt(getString(R.string.limit_inf), limit_inf )
                    }
                    if( !valor_limit_sup.isEmpty() ) {
                        limit_sup = valor_limit_sup.toInt()
                        putInt(getString(R.string.limit_sup), limit_sup )
                    }
                    if( !valor_target.isEmpty() ) {
                        target = valor_target.toInt()
                        putInt(getString(R.string.target), target )
                    }

                    commit()
                    dialog.dismiss()
                }
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

                    if (c.icr_value > 0) {
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

                    if (c.isf_value > 0) {
                        isf = c.isf_value
                    }
                }
            }

            override fun onFailure(call: Call<ISFOutput>, t: Throwable) {
                Toast.makeText(this@MainActivity, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // LINECHART DATA
    private fun setData( entries1: ArrayList<Entry>, entries2: ArrayList<Entry>, entries3: ArrayList<Entry>, entries4: ArrayList<Entry> ) {
        val v1 = LineDataSet(entries1, "Valor")
        val v2 = LineDataSet(entries2, "Alvo")
        val v3 = LineDataSet(entries3, "Limite Inferior")
        val v4 = LineDataSet(entries4, "Limite Superior")

        v1.setDrawValues(true)
        v1.lineWidth = 3f
        v1.valueTextSize = 10F
        v1.setColor( Color.CYAN )
        v1.valueTextColor = Color.CYAN

        v2.setDrawValues(true)
        v2.lineWidth = 3f
        v2.valueTextSize = 10F
        v2.setColor( Color.BLUE )
        v2.valueTextColor = Color.BLUE

        v3.setDrawValues(true)
        v3.lineWidth = 3f
        v3.valueTextSize = 10F
        v3.setColor( Color.GREEN )
        v3.valueTextColor = Color.GREEN

        v4.setDrawValues(true)
        v4.lineWidth = 3f
        v4.valueTextSize = 10F
        v4.valueTextColor = Color.RED
        v4.setColor( Color.RED )

        lineChart.data = LineData(v1, v2, v3, v4)

        lineChart.xAxis.axisMaximum = 259200f
        lineChart.xAxis.axisMinimum = 0f
        lineChart.xAxis.textSize = 12f
        lineChart.xAxis.labelRotationAngle = 0f
        lineChart.xAxis.granularity = 1800f
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM

        lineChart.xAxis.valueFormatter = MyValueFormatter()

        lineChart.axisLeft.granularity = 1f
        lineChart.axisLeft.textSize = 12f
        lineChart.axisRight.isEnabled = false

        lineChart.legend.textSize = 12F

        lineChart.setTouchEnabled(true)
        lineChart.setPinchZoom(true)

        lineChart.setNoDataText("No Data Available!")
        lineChart.description.isEnabled = false

        lineChart.animateXY(1500, 1500)
    }

    private fun getChartValues() {
        val entries1 = ArrayList<Entry>()
        val entries2 = ArrayList<Entry>()
        val entries3 = ArrayList<Entry>()
        val entries4 = ArrayList<Entry>()

        val request = ServiceBuilder.buildService(ValuesEndPoints::class.java)
        val call = request.getGlicemia(
            userProfileId
        )

        call.enqueue(object : Callback<List<GlicemiaOutput>> {
            override fun onResponse(
                call: Call<List<GlicemiaOutput>>,
                response: Response<List<GlicemiaOutput>>
            ) {
                if (response.isSuccessful) {
                    val c: List<GlicemiaOutput> = response.body()!!

                    if (c != null) {
                        for (glicemia in c) {
                            entries1.add( Entry( glicemia.glycemia_datems.toFloat(), glicemia.glycemia_value.toFloat() ) )
                            entries2.add( Entry( glicemia.glycemia_datems.toFloat(), glicemia.glycemia_target.toFloat() ) )
                            entries3.add( Entry( glicemia.glycemia_datems.toFloat(), glicemia.glycemia_limit_inf.toFloat() ) )
                            entries4.add( Entry( glicemia.glycemia_datems.toFloat(), glicemia.glycemia_limit_sup.toFloat() ) )

                        }

                        setData( entries1, entries2, entries3, entries4 )
                    }
                }
            }

            override fun onFailure(call: Call<List<GlicemiaOutput>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    class MyValueFormatter : ValueFormatter() {

        val calendar = Calendar.getInstance()

        override fun getFormattedValue(value: Float): String {
            return value.toString()
        }

        override fun getAxisLabel(value: Float, axis: AxisBase): String {
            val valor = value.toInt()

            val datetime = calendar.timeInMillis-259200000

            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm")

            if ( valor >= 0 && valor.rem(1800) == 0 ) {
                val time = datetime + valor*1000
                val dateString = simpleDateFormat.format( time )
                return dateString
            }
            else {
                return ""
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
}
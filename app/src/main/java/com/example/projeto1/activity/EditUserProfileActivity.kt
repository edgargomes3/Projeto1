package com.example.projeto1.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.example.projeto1.R
import com.example.projeto1.dataclass.Educacao
import com.example.projeto1.retrofit.ServiceBuilder
import com.example.projeto1.retrofit.UserProfileEndPoints
import com.hbb20.CountryCodePicker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.content.SharedPreferences
import androidx.core.view.get
import com.example.projeto1.retrofit.UserProfileOutput

import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class EditUserProfileActivity : AppCompatActivity(), CountryCodePicker.OnCountryChangeListener {

    private var ccp:CountryCodePicker?=null
    private var countryCode:String?=null
    private var countryName:String?=null
    private lateinit var nomeEditText: EditText
    private lateinit var dataNascimentoEditText: EditText
    private lateinit var alturaEditText: EditText
    private lateinit var generoSpinner: Spinner
    private lateinit var codpostalEditText: EditText
    private lateinit var educacaoSpinner: Spinner
    private lateinit var tipoDiabetesSpinner: Spinner
    private lateinit var anoDiagEditText: EditText
    private lateinit var caregiverCheckBox: CheckBox
    val myCalendar: Calendar = Calendar.getInstance()

    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edituserprofile)

        ccp = findViewById(R.id.ccp)
        ccp!!.setOnCountryChangeListener(this)

        nomeEditText = findViewById(R.id.nomeEditText)
        dataNascimentoEditText = findViewById(R.id.dataNascimentoEditText)
        alturaEditText = findViewById(R.id.alturaEditText)
        generoSpinner = findViewById(R.id.generoSpinner)
        codpostalEditText = findViewById(R.id.codpostalEditText)
        educacaoSpinner = findViewById(R.id.educacaoSpinner)
        tipoDiabetesSpinner = findViewById(R.id.tipoDiabetesSpinner)
        anoDiagEditText = findViewById(R.id.anoDiagEditText)
        caregiverCheckBox = findViewById(R.id.caregiverCheckBox)

        var educacao = ArrayList<String>()
        educacao.add( "<Escolha uma Opção>" )

        var genero = ArrayList<String>()
        genero.add( "<Escolha uma Opção>" )
        genero.add( "Masculino" )
        genero.add( "Feminino" )
        adapter = ArrayAdapter(this@EditUserProfileActivity, android.R.layout.simple_spinner_item, genero )
        generoSpinner.adapter = adapter

        var tipoDiabetes = ArrayList<String>()
        tipoDiabetes.add( "<Escolha uma Opção>" )
        tipoDiabetes.add( "Diabetes Tipo #1" )
        tipoDiabetes.add( "Diabetes Tipo #2" )
        adapter = ArrayAdapter(this@EditUserProfileActivity, android.R.layout.simple_spinner_item, tipoDiabetes )
        tipoDiabetesSpinner.adapter = adapter

        val date =
            OnDateSetListener { view, year, month, day ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, day)
                updateLabel()
            }

        dataNascimentoEditText.setOnClickListener(View.OnClickListener {
            DatePickerDialog(
                this@EditUserProfileActivity,
                date,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        })

        ccp!!.setDefaultCountryUsingNameCode("PT")

        val request = ServiceBuilder.buildService(UserProfileEndPoints::class.java)
        val call = request.getEducacoes()

        call.enqueue(object : Callback<List<Educacao>> {
            override fun onResponse(call: Call<List<Educacao>>, response: Response<List<Educacao>>) {
                if (response.isSuccessful) {
                    val c = response.body()!!
                    //var tipoProblemaIndex = -1

                    for( result in c ) {
                        educacao.add("${result.description} - ${result.id_education}")
                        /*if( educacaoEditText.tag != null ) {
                            if (TextUtils.equals(tipo.tipo.toString(), educacaoEditText.tag.toString())) {
                                tipoProblemaIndex = educacao.lastIndex
                            }
                        }*/
                    }

                    adapter = ArrayAdapter(this@EditUserProfileActivity, android.R.layout.simple_spinner_item, educacao )
                    educacaoSpinner.adapter = adapter

                    //if( tipoProblemaIndex > -1 ) educacaoEditText.setSelection(tipoProblemaIndex)
                }
            }

            override fun onFailure(call: Call<List<Educacao>>, t: Throwable) {
                Toast.makeText(this@EditUserProfileActivity, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateLabel() {
        val myFormat = "dd/MM/yy"
        val dateFormat = SimpleDateFormat(myFormat, Locale.US)
        dataNascimentoEditText.setText(dateFormat.format(myCalendar.time))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.edituserprofile_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save_edituserprofile_btn -> {
                save()
                true
            }
            R.id.return_edituserprofile_btn -> {
                val intent = Intent(this@EditUserProfileActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun save() {
        if ( TextUtils.isEmpty(nomeEditText.text) ) {
            Toast.makeText(this@EditUserProfileActivity, R.string.emptyNameLabel, Toast.LENGTH_LONG).show()
            return
        }
        else if ( TextUtils.isEmpty(dataNascimentoEditText.text) ) {
            Toast.makeText(this@EditUserProfileActivity, R.string.emptyDataNascimentoLabel, Toast.LENGTH_LONG).show()
            return
        }
        else if ( TextUtils.isEmpty(alturaEditText.text) ) {
            Toast.makeText(this@EditUserProfileActivity, R.string.emptyAlturaLabel, Toast.LENGTH_LONG).show()
            return
        }
        else if ( generoSpinner.selectedItemId <= 0 ) {
            Toast.makeText(this@EditUserProfileActivity, R.string.emptyGeneroLabel, Toast.LENGTH_LONG).show()
            return
        }
        else if ( TextUtils.isEmpty(codpostalEditText.text) ) {
            Toast.makeText(this@EditUserProfileActivity, R.string.emptyCodPostalLabel, Toast.LENGTH_LONG).show()
            return
        }
        else if ( educacaoSpinner.selectedItemId <= 0 ) {
            Toast.makeText(this@EditUserProfileActivity, R.string.emptyEducacaoLabel, Toast.LENGTH_LONG).show()
            return
        }
        else if ( tipoDiabetesSpinner.selectedItemId <= 0 ) {
            Toast.makeText(this@EditUserProfileActivity, R.string.emptyTipoDiabetesLabel, Toast.LENGTH_LONG).show()
            return
        }
        else if ( TextUtils.isEmpty(anoDiagEditText.text) ) {
            Toast.makeText(this@EditUserProfileActivity, R.string.emptyAnoDiagLabel, Toast.LENGTH_LONG).show()
            return
        }
        else {
            val sharedPref: SharedPreferences = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE )

            val userProfileId = sharedPref.getInt(getString(R.string.userProfileId), -1)

            val request = ServiceBuilder.buildService(UserProfileEndPoints::class.java)
            val call = request.saveProfile(
                userProfileId,
                nomeEditText.text.toString(),
                dataNascimentoEditText.text.toString(),
                alturaEditText.text.toString().toInt(),
                generoSpinner.selectedItem.toString(),
                codpostalEditText.text.toString(),
                countryCode.toString(),
                educacaoSpinner.selectedItemPosition-1,
                tipoDiabetesSpinner.selectedItem.toString(),
                anoDiagEditText.text.toString().toInt(),
                caregiverCheckBox.isChecked()
            )

            call.enqueue(object : Callback<UserProfileOutput> {
                override fun onResponse(call: Call<UserProfileOutput>, response: Response<UserProfileOutput>) {
                    if (response.isSuccessful) {
                        val c = response.body()!!

                        if( c.success ) {
                            Toast.makeText(this@EditUserProfileActivity, R.string.successEditUserProfileSaveLabel, Toast.LENGTH_SHORT ).show()

                            intent = Intent(this@EditUserProfileActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        else Toast.makeText(this@EditUserProfileActivity, R.string.failedEditUserProfileSaveLabel, Toast.LENGTH_SHORT ).show()
                    }
                }

                override fun onFailure(call: Call<UserProfileOutput>, t: Throwable) {
                    Toast.makeText(this@EditUserProfileActivity, "${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
            return
        }
    }

    override fun onCountrySelected() {
        countryCode=ccp!!.selectedCountryNameCode
        countryName=ccp!!.selectedCountryName
    }
}
package com.example.helpmiga.ui.activity


import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.telephony.SmsManager
import android.text.InputType
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.helpmiga.R
import com.example.helpmiga.databinding.ActivityHelpBinding
import com.example.helpmiga.service.MyIntentService
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.ktx.Firebase
import java.time.Period
import java.util.*
import kotlin.concurrent.schedule


class HelpActivity : AppCompatActivity() {


    private lateinit var latLong: LatLng
    private var googleSignInClient: GoogleSignInClient? = null
    private lateinit var binding: ActivityHelpBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var m_text = ""
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val _binding = ActivityHelpBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        binding = _binding
        auth = Firebase.auth
        var toolbar = findViewById<Toolbar>(R.id.logo_toolbar)
        setSupportActionBar(toolbar)
        latLong = LatLng(0.0, 0.0)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        googleAuth()
        configurarBotoes()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {

            R.id.menuAgenda -> {
                val intent = Intent(this, ActivityContatos::class.java)
                startActivity(intent)
            }
            R.id.menuMapa -> {
//                val newFragment = CustomDialogCode()
//                newFragment.show(supportFragmentManager, "codigo")
//                val intent = Intent(this, MapaActivity::class.java)
//                startActivity(intent)
                abrirDialog()
            }

            R.id.menuLogout -> {
                revokeAccess()
            }
            R.id.menuCancel -> {
                MyIntentService.stopService()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun abrirDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Digite o código enviado por SMS")


        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)
        builder.setIcon(R.drawable.ic_baseline_add_24)
        builder.setPositiveButton("ATIVAR", DialogInterface.OnClickListener { dialog, which ->
            m_text = input.text.toString()
            val intent = Intent(this, MapaActivity::class.java)
            intent.putExtra("codigo",m_text)
            startActivity(intent)
        })
        builder.setNegativeButton("CANCELAR", DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

        builder.show()
    }

    fun enviarHelp(instanceId : String){


        var message = "HelpMiga!\nVocê é o contato de emergência de ${auth.currentUser?.displayName}, coloque esse código na opção de mapa do app : ${instanceId} para começar a busca.\n "
        val sms = SmsManager.getDefault()
        val parts = sms.divideMessage(message)
        sms.sendMultipartTextMessage("014997818811", null, parts, null, null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == 1) {
            val codigo = data as String
            val intent = Intent(this, MapaActivity::class.java)
            intent.putExtra("codigo", codigo)
            startActivity(intent)
        }
    }

    fun googleAuth() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    fun configurarBotoes() {
        binding.imgHelp.setOnLongClickListener {
            acionarBotao()
            return@setOnLongClickListener true
        }
    }

    fun acionarBotao() {
        var instanceId = FirebaseInstallations.getInstance().getId()
        Intent(this, MyIntentService::class.java).also {
            Timer("SettingUp", false).schedule(5000) {
                enviarHelp(instanceId.result)
            }
            startService(it)
        }
//        criarNosBD()
        Toast.makeText(this, "Botão emergencia acionado", Toast.LENGTH_LONG).show()
    }


    private fun revokeAccess() {
        FirebaseAuth.getInstance().signOut()
        googleSignInClient?.revokeAccess()
            ?.addOnCompleteListener(this, OnCompleteListener<Void?> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            })
    }
}
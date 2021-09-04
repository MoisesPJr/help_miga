package com.mjtech.helpmiga.ui.activity


import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.telephony.SmsManager
import android.text.InputType
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.mjtech.helpmiga.ContatoApplication
import com.mjtech.helpmiga.R
import com.mjtech.helpmiga.data.viewModel.ContatoViewModel
import com.mjtech.helpmiga.data.viewModel.ContatoViewModelFactory
import com.mjtech.helpmiga.databinding.ActivityHelpBinding
import com.mjtech.helpmiga.service.MyIntentService
import com.mjtech.helpmiga.ui.SharedPreferences
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.ktx.Firebase
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.util.*
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.SettingsClickListener


class HelpActivity : AppCompatActivity() {

    companion object {
        private const val SEND_SMS_PERMISSION_CODE = 1
    }

    private lateinit var latLong: LatLng
    private var googleSignInClient: GoogleSignInClient? = null
    private lateinit var binding: ActivityHelpBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var m_text = ""
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    var instanceId = FirebaseInstallations.getInstance().getId()
    private  var currentUser : FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val _binding = ActivityHelpBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        binding = _binding
        auth = Firebase.auth
         currentUser = auth.currentUser
        var toolbar = findViewById<Toolbar>(R.id.logo_toolbar)
        setSupportActionBar(toolbar)
        googleAuth()
        configurarBotoes()
        sharedPreferences = SharedPreferences(this)
        sharedPreferences.salvarCodigo(instanceId.result)
        checkPermission()

    }

    private val contatoViewModel: ContatoViewModel by viewModels {
        ContatoViewModelFactory((application as com.mjtech.helpmiga.ContatoApplication).repository)
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
                if (MyIntentService.isRunning) {
                    MyIntentService.stopService(applicationContext)
                }
                revokeAccess()
            }
            R.id.menuCancel -> {
                if (MyIntentService.isRunning) {
                    MyIntentService.stopService(applicationContext)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
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
            intent.putExtra("codigo", m_text)
            startActivity(intent)
        })
        builder.setNegativeButton("CANCELAR", DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

        builder.show()
    }

    fun enviarHelp() {

        val listaContatos = contatoViewModel.listaContatos()

        var message =
            "HelpMiga!\nVocê é o contato de emergência de ${currentUser?.displayName}, coloque esse código na opção de mapa do app : ${sharedPreferences.getCodigo()} para começar a busca.\n "
        val sms = SmsManager.getDefault()
        val parts = sms.divideMessage(message)

        listaContatos.forEach {
            sms.sendMultipartTextMessage(it.telefoneContato, null, parts, null, null)
        }
    }

    fun alert() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Lista de contatos vazia")
        builder.setMessage("Para utilizar a função de enviar Help você precisa adicionar pelo menos um contato na lista")
        builder.setPositiveButton("ADICIONAR", DialogInterface.OnClickListener { dialog, which ->
            val intent = Intent(this, ActivityContatos::class.java)
            startActivity(intent)
        })
        builder.setNegativeButton("CANCELAR", DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
        builder.show()
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

        val listaContatos = contatoViewModel.listaContatos()
        if (!listaContatos.isEmpty()) {
            Intent(this, MyIntentService::class.java).also {
                startService(it)
                enviarHelp()
            }
        } else {
            alert()
        }
    }


    private fun revokeAccess() {
        FirebaseAuth.getInstance().signOut()
        googleSignInClient?.revokeAccess()
            ?.addOnCompleteListener(this, OnCompleteListener<Void?> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            })
    }

    private fun checkPermission() {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.SEND_SMS,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) { /* ... */
                    if(report.isAnyPermissionPermanentlyDenied){
                        requestPermissionDenied()
                        return
                    }
                }

                override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest?>?, token: PermissionToken?) { /* ... */

                }
            }).check()

    }

    fun requestPermissionDenied(){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Permissões negadas")
        builder.setMessage("Para utilizar o aplicativo você deve aceitar as permissões de localização e envio de SMS.\nFique tranquilo, a sua localização será usada apenas quando utilizar a função HELP!")
        builder.setPositiveButton("ACEITAR", DialogInterface.OnClickListener { dialog, which ->
            abrirSettings()
        })
        builder.setNegativeButton("NÃO ACEITAR", DialogInterface.OnClickListener { dialog, which -> finish() })
        builder.setCancelable(false)
        builder.show()
    }

    fun abrirSettings(){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Passo a passo")
        builder.setMessage("Você será redirecionado para as configurações.\nPara aceitar as permissões clique em PERMISSÕES e e permita a utilização de SMS e Localização.")
        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
            var intent = Intent()
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            var uri = Uri.fromParts("package",packageName,null)
            intent.setData(uri)
            startActivity(intent)
        })
        builder.setCancelable(false)
        builder.show()
    }

    fun closeApp(){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Permissões negadas")
        builder.setMessage("Para utilizar o aplicativo você deve aceitar as permissões de localização e envio de SMS.")
        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
            finish()
        })
        builder.setCancelable(false)
        builder.show()
    }



}
package com.example.helpmiga.ui.activity


import android.Manifest
import android.app.IntentService
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.app.JobIntentService
import com.example.helpmiga.R
import com.example.helpmiga.data.model.Requisicao
import com.example.helpmiga.databinding.ActivityHelpBinding
import com.example.helpmiga.service.MyIntentService
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.installations.FirebaseInstallations
import java.util.*
import kotlin.concurrent.schedule


class HelpActivity : AppCompatActivity() {


    private var dataBaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference.root
    private lateinit var latLong: LatLng
    private var googleSignInClient: GoogleSignInClient? = null
    private lateinit var binding: ActivityHelpBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val _binding = ActivityHelpBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        binding = _binding
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
                val intent = Intent(this, MapaActivity::class.java)
                startActivity(intent)
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

//    fun configuraSafe(){
//        if(MyIntentService.isRunning){
//
//        }
//    }




    fun acionarBotao() {
        Intent(this,MyIntentService::class.java).also {
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
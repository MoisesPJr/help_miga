package com.mjtech.helpmiga.service

import android.Manifest
import android.app.Activity
import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mjtech.helpmiga.data.model.Requisicao
import com.mjtech.helpmiga.ui.SharedPreferences

class MyIntentService : IntentService("MyIntentService") {

    lateinit var fusedLocationClient: FusedLocationProviderClient
    private var dataBaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference.root

    lateinit var locationCallback: LocationCallback

    companion object {
        private lateinit var instance: MyIntentService
        var isRunning = false
        var lat = 0.0
        var long = 0.0


        fun stopService(context: Context) {
            isRunning = false
            instance.stopSelf()
            val sharedPreferences = context.let { SharedPreferences(it) }
            var dataBaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference.root
            sharedPreferences.getCodigo()?.let { dataBaseReference.child(it).child("status").setValue("B") }
        }
    }

    init {
        instance = this
    }

    fun criarNosBD() {
        isRunning = true
        val sharedPreferences = application.let { SharedPreferences(it) }
        getLocalizacaoUsuario()
        var requisicao = sharedPreferences.getCodigo()?.let { Requisicao(2, lat, long, it, "A") }
        dataBaseReference.child(requisicao!!.codigoRequisicao).setValue(requisicao)
    }


    private fun getLocalizacaoUsuario() {
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        var locationRequest = LocationRequest.create()
        locationRequest.setInterval(10000)
        locationRequest.setFastestInterval(5000)
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)

        var builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        var settingsClient = LocationServices.getSettingsClient(this)
        settingsClient.checkLocationSettings(builder.build()).addOnSuccessListener {
            OnSuccessListener<LocationSettingsResponse>() {
                Log.i("TESTE", "${it.locationSettingsStates.isNetworkLocationPresent}")
            }
        }
            .addOnFailureListener(OnFailureListener {
                when (it) {
                    is ResolvableApiException -> {
                        try {
                            var resolvable = it
                            resolvable.startResolutionForResult(Activity(), 10)
                        } catch (e: Exception) {

                        }
                    }

                }
            })

        if (isRunning) {

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    locationResult ?: return
                    for (location in locationResult.locations) {
                        lat = location.latitude
                        long = location.longitude
                    }
                }
            }
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {

                } else {
                    showSettingsAlert()                }
                // Got last known location. In some rare situations this can be null.
            }


        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())

    }
    fun showSettingsAlert() {
        val alertDialog = android.app.AlertDialog.Builder(this)

        // Titulo do dialogo
        alertDialog.setTitle("GPS")

        // Mensagem do dialogo
        alertDialog.setMessage("GPS não está habilitado. Deseja configurar?")

        // botao ajustar configuracao
        alertDialog.setPositiveButton("Configurar") { dialog, which ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }

        // botao cancelar
        alertDialog.setNegativeButton("Cancelar") { dialog, which -> dialog.cancel() }

        // visualizacao do dialogo
        alertDialog.show()
    }

    override fun onHandleIntent(intent: Intent?) {
        try {
            isRunning = true
            while (isRunning) {
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                criarNosBD()
                Thread.sleep(10000)
            }
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }
}

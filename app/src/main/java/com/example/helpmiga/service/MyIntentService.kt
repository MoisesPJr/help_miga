package com.example.helpmiga.service

import android.Manifest
import android.app.Activity
import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.JobIntentService
import com.example.helpmiga.data.model.Requisicao
import com.example.helpmiga.ui.activity.MainActivity
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.installations.FirebaseInstallations
import java.lang.Exception
import java.util.*
import kotlin.concurrent.schedule
import com.google.android.gms.common.api.ResolvableApiException as ResolvableApiException

class MyIntentService : IntentService("MyIntentService") {

    lateinit var fusedLocationClient: FusedLocationProviderClient
    private var dataBaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference.root

     lateinit var locationCallback: LocationCallback

    companion object {
        private lateinit var instance: MyIntentService
        var isRunning = true
         var lat = 0.0
         var long = 0.0
        fun stopService() {
            isRunning = false
            instance.stopSelf()
            var instanceId = FirebaseInstallations.getInstance().getId()
            var dataBaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference.root
            try {
                dataBaseReference.child(instanceId.result).child("status").setValue("B")
            }catch (e:Exception){
                Thread.sleep(2000)
                dataBaseReference.child(instanceId.result).child("status").setValue("B")
                Log.i("STATUS",e.toString())
            }
        }
    }

    init {
        instance = this
    }

    fun criarNosBD() {
        getLocalizacaoUsuario()
        var intanceId = FirebaseInstallations.getInstance().getId()
        Timer("SettingUp", false).schedule(5000) {
            var requisicao = Requisicao(2, lat, long, intanceId.result, "A")
            dataBaseReference.child(requisicao.codigoRequisicao).setValue(requisicao)
        }
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
                Log.i("TESE","${it.locationSettingsStates.isNetworkLocationPresent}" )
            }
        }
            .addOnFailureListener(OnFailureListener {
                when(it){
                    is ResolvableApiException ->{
                        try {
                            var resolvable = it
                            resolvable.startResolutionForResult(Activity(), 10)
                        }catch (e: Exception){

                        }
                    }

                }
            })

        if(isRunning) {

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    locationResult ?: return
                    for (location in locationResult.locations) {
                        Log.i("Localizacao", "Latitude: ${location.latitude} e Longitude: ${location.longitude}")
                        lat = location.latitude
                        long = location.longitude
                    }
                }
            }
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {

                    Toast.makeText(applicationContext, "Latitude:${lat} Longitude:${long}", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(applicationContext, "Impossível pegar localização", Toast.LENGTH_LONG).show()
                }
                // Got last known location. In some rare situations this can be null.
            }


        fusedLocationClient.requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper())

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

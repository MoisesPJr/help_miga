package com.example.helpmiga.ui.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.helpmiga.R
import com.example.helpmiga.data.model.Requisicao
import com.example.helpmiga.service.MyIntentService
import com.example.helpmiga.utils.Permissoes
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.*


/**
 * An activity that displays a Google map with a marker (pin) to indicate a particular location.
 */
class MapaActivity : AppCompatActivity(), OnMapReadyCallback {


    private var permissoes = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS)
    private lateinit var dataBaseReference: DatabaseReference
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var locationCallback: LocationCallback
    private  var markerHelp: Marker? = null
    private  var markerUser: Marker? = null

    private var latitude = 0.0
    private var longitude = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_mapa)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        // Get the SupportMapFragment and request notification when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap?) {
        val codigo: String = intent.getStringExtra("codigo").toString()
        recuperarDadosLocaliacaoHelp(googleMap, codigo)
        getLocalizacaoUsuario(googleMap)
    }




    fun recuperarDadosLocaliacaoHelp(googleMap: GoogleMap?, codigo: String) {
        dataBaseReference = FirebaseDatabase.getInstance().reference.child(codigo) // TODO pegar eventos do Firebase

        val requisicaoListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val requisicao: Requisicao? = dataSnapshot.getValue(Requisicao::class.java)
                if (requisicao?.status.equals("A")) {
                    Log.i("Localizacao2", "Latitude: ${requisicao!!.latitude} e Longitude: ${requisicao!!.longitude}")
                    getLocalizacaoHelp(googleMap, requisicao!!.latitude, requisicao!!.longitude)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("Teste", "loadPost:onCancelled", databaseError.toException())
            }
        }

        dataBaseReference.addValueEventListener(requisicaoListener)


    }


    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun getLocalizacaoHelp(googleMap: GoogleMap?, latitude: Double, longitude: Double) {
        googleMap?.apply {
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
            val casa = LatLng(latitude, longitude)
            markerHelp?.remove()
            addMarkersHelp(googleMap,casa)
        }
    }

    private fun getLocalizacaoUsuario(googleMap: GoogleMap?) {
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
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY

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
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    Log.i("Localizacao", "Latitude: ${location.latitude} e Longitude: ${location.longitude}")
                    mudaLocalizacao(googleMap, location.latitude, location.longitude, locationRequest)
                }
            }
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    Toast.makeText(applicationContext, "Latitude:${MyIntentService.lat} Longitude:${MyIntentService.long}", Toast.LENGTH_LONG).show()
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

    fun mudaLocalizacao(googleMap: GoogleMap?, latitude: Double, longitude: Double, locationRequest: LocationRequest) {
        googleMap?.apply {
            markerUser?.remove()
            getLastLocation(latitude, longitude, googleMap, locationRequest)
        }
    }

    private fun getLastLocation(
        latitude: Double,
        longitude: Double,
        googleMap: GoogleMap?,
        locationRequest: LocationRequest
    ) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val casa = LatLng(latitude, longitude)
                    addMarkersUser(googleMap, casa)
                } else {
                    Toast.makeText(applicationContext, "Impossível pegar localização", Toast.LENGTH_LONG).show()
                }
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
                // Got last known location. In some rare situations this can be null.
            }
    }

    fun addMarkersHelp(googleMap: GoogleMap?, casa: LatLng) {

        if (markerHelp != null) {
            markerHelp!!.remove();
        }
       markerHelp = googleMap?.addMarker(
           MarkerOptions()
               .position(casa)
               .title("Casa")
               .icon(BitmapDescriptorFactory.fromResource(R.drawable.icone_alert))
       )!!

        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(casa, 18F))
    }

    fun addMarkersUser(googleMap: GoogleMap?, casa: LatLng) {
        if (markerUser != null) {
            markerUser!!.remove();
        }

       markerUser =  googleMap?.addMarker(
            MarkerOptions()
                .position(casa)
                .title("Casa")
        )
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(casa, 18F))
    }

}

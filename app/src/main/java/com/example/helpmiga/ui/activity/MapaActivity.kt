package com.example.helpmiga.ui.activity

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.example.helpmiga.R
import com.example.helpmiga.data.model.Requisicao
import com.example.helpmiga.data.model.Usuario
import com.example.helpmiga.utils.Permissoes
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*


/**
 * An activity that displays a Google map with a marker (pin) to indicate a particular location.
 */
class MapaActivity : FragmentActivity(), OnMapReadyCallback {


    private var permissoes = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
private lateinit var   dataBaseReference : DatabaseReference
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var latitude  = 0.0
    private var longitude  = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_mapa)


        //Validar Permissoes
        Permissoes.validarPermissoes(permissoes,this,1)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        // Get the SupportMapFragment and request notification when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap?) {
        recuperarDadosLocaliacao(googleMap)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        for(permissaoResult : Int in grantResults){
            if(permissaoResult == PackageManager.PERMISSION_DENIED){
                //Alerta
                alertaValidacaoPermissao()
            }else{

            }
        }
    }


    fun recuperarDadosLocaliacao(googleMap: GoogleMap?){
         dataBaseReference = FirebaseDatabase.getInstance().reference.child("ecxlFztlSt2iUmI4uEAFIa") // TODO pegar eventos do Firebase

        val requisicaoListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val requisicao : Requisicao? = dataSnapshot.getValue(Requisicao::class.java)
                if(requisicao?.status.equals("A")){
                    getLocalizacaoUsuario(googleMap)
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
        dataBaseReference.onDisconnect()
    }

    private fun getLocalizacaoUsuario(googleMap: GoogleMap?){
        googleMap?.apply {
            googleMap.clear()

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
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val casa = LatLng(location.latitude, location.longitude)
                        addMarker(
                            MarkerOptions()
                                .position(casa)
                                .title("Casa")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icone_alert))
                        )
                        moveCamera(CameraUpdateFactory.newLatLngZoom(casa, 18F))
                    } else {
                        Toast.makeText(applicationContext, "Impossível pegar localização", Toast.LENGTH_LONG).show()
                    }
                    // Got last known location. In some rare situations this can be null.
                }
        }

    }




    fun alertaValidacaoPermissao(){
        var builder : AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Permissoes Negadas")
        builder.setMessage("Para utilizar o app, é necessário aceitar as permissões")
        builder.setCancelable(false)
        builder.setPositiveButton("Confirmar",DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    finish()
                }
            }
        })

        var dialog : AlertDialog = builder.create()
        dialog.show()
    }



}

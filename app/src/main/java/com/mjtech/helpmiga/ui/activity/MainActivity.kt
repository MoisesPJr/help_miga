package com.mjtech.helpmiga.ui.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mjtech.helpmiga.utils.Permissoes

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var permissoes = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    private  var user: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       Permissoes.validarPermissoes(permissoes,this,1)
        auth = Firebase.auth
        user = auth.currentUser
        if (user != null) {
            toHelpActivity()
        } else {
            toLoginActivity()
        }
    }

    fun toHelpActivity() {
        val intent = Intent(this, HelpActivity::class.java)
        startActivity(intent)
    }

    fun toLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}
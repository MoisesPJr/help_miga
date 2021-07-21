package com.example.helpmiga.ui.activity


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.helpmiga.R
import com.example.helpmiga.databinding.ActivityMainBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class MainActivity : AppCompatActivity() {


    private var dataBaseReference :DatabaseReference = FirebaseDatabase.getInstance().reference.root

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        binding = _binding
        var toolbar = findViewById<Toolbar>(R.id.logo_toolbar)
        setSupportActionBar(toolbar)
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
                val intent = Intent(this, ActivityContatos::class.java )
                startActivity(intent)
            }
            R.id.menuMapa ->{
                val intent = Intent(this, MapaActivity::class.java )
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun configurarBotoes(){
        binding.imgHelp.setOnLongClickListener{
            acionarBotao()
            return@setOnLongClickListener true
        }
    }


    fun acionarBotao() {
//        val smsManager: SmsManager = SmsManager.getDefault()
//        smsManager.sendTextMessage("014997818811", null, "HelpMiga!! Botão alerta acionado.  ", null, null)
//        smsManager.sendTextMessage("014997818811", null, "Fique alerta, algo pode acontecer a qualquer momento. ", null, null)
//        smsManager.sendTextMessage("014997818811", null, "Qualquer coisa", null, null)

        Toast.makeText(this, "Botão emergencia acionado", Toast.LENGTH_LONG).show()
    }

    fun criarNosBD(){
        dataBaseReference.child("")
    }
}
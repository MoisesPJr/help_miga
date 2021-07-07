package com.example.helpmiga.ui


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.renderscript.ScriptGroup
import android.view.*
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.helpmiga.R
import com.example.helpmiga.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
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
                val intent = Intent(this,ActivityContatos::class.java )
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
}
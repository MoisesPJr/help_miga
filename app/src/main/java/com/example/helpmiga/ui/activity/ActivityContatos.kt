package com.example.helpmiga.ui.activity

import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.InputType
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.helpmiga.ContatoApplication
import com.example.helpmiga.R
import com.example.helpmiga.data.model.Contato
import com.example.helpmiga.data.viewModel.ContatoViewModel
import com.example.helpmiga.data.viewModel.ContatoViewModelFactory
import com.example.helpmiga.databinding.ActivityContatosBinding
import com.example.helpmiga.ui.adapter.AdapterContatos






class ActivityContatos : AppCompatActivity() {
    private lateinit var binding: ActivityContatosBinding
    private var listaContatos = listOf<Contato>()
    val REQUEST_SELECT_CONTACT = 1
    var isEnableMenu = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val _binding = ActivityContatosBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        binding = _binding
        listaContato()
        initView()
    }


    fun initView(){
        binding.buttonAdd.setOnClickListener {
            abrirContatos()
        }

        binding.emptyDataParent.buttonAdd.setOnClickListener {
            abrirContatos()
        }
    }

    private val adapter by lazy {
        AdapterContatos(
            mutableListOf(),
            {apagarContato(it)}
        )
    }

    private val contatoViewModel: ContatoViewModel by viewModels {
        ContatoViewModelFactory((application as ContatoApplication).repository)
    }

    fun listaContato() {
        listaContatos = contatoViewModel.listaContatos()
        adapter.listaContatos.clear()
        adapter.listaContatos.addAll(listaContatos)
        //Configurar RecyclerViewdapter
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext)
        binding.recyclerContatos.setLayoutManager(layoutManager)
        binding.recyclerContatos.setHasFixedSize(true)
        binding.recyclerContatos.addItemDecoration(DividerItemDecoration(applicationContext, LinearLayoutManager.VERTICAL))
        binding.recyclerContatos.setAdapter(adapter)
        if(listaContatos.isEmpty()){
            binding.layoutAdd.visibility = View.GONE
        }else{
                habilitaBotaoAdd()
        }
        // Here
        val emptyDataObserver = EmptyDataObserver(binding.recyclerContatos, binding.emptyDataParent.root)
        adapter.registerAdapterDataObserver(emptyDataObserver)

    }



    fun habilitaBotaoAdd(){
        val quantidade = contatoViewModel.getQtdContatos()
        if(quantidade < 3) {
            binding.layoutAdd.visibility = View.VISIBLE
        }else{
            binding.layoutAdd.visibility = View.GONE
        }
    }

    fun abrirContatos() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
        startActivityForResult(intent, REQUEST_SELECT_CONTACT)
    }

    fun apagarContato(contatos: Contato) {
        contatoViewModel.apagarContato(contatos)
        listaContato()
    }

    override fun onActivityResult(codigo: Int, resultado: Int, intent: Intent?) {
        super.onActivityResult(codigo, resultado, intent)
        val contatos = Contato()
        if (codigo == REQUEST_SELECT_CONTACT && resultado == RESULT_OK) {
            if (intent != null) {
                selecionaContato(contatos, intent)
                listaContato()
            }
        }
    }

    fun selecionaContato(contatos: Contato, intent: Intent) {
        val contactUri: Uri? = intent.data
        val cursor: Cursor? = contactUri?.let { contentResolver.query(it, null, null, null, null) }
        val indexName: Int = cursor?.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)!!
        val indexTelefone: Int = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
        cursor.moveToFirst()
        contatos.nomeContato = cursor?.getString(indexName)
        contatos.telefoneContato = cursor?.getString(indexTelefone)
        try {
            var listContato = contatoViewModel.listaContatos()
            listContato.forEach{
                if(contatos.telefoneContato == it.telefoneContato && contatos.nomeContato == it.nomeContato){
                    abrirDialog()
                    return
                }
            }

            contatoViewModel.insert(contatos)
            habilitaBotaoAdd()
            Log.i("INFO - NOME", "${contatos.nomeContato}")
            Log.i("INFO - Telefone", "${contatos.telefoneContato}")
            Toast.makeText(applicationContext, "Sucesso ao salvar contato", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(applicationContext, "Erro ao salvar contato", Toast.LENGTH_LONG).show()
            Log.i("INFO - Contato", "Erro ao salvar contato" + e.message)
        }

    }

    fun abrirDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Contato Duplicado!")
        builder.setMessage("Esse contato já está na sua lista.")
        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
        })
        builder.show()
    }

}
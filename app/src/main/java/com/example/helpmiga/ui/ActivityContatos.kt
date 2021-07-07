package com.example.helpmiga.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.helpmiga.ContatoApplication
import com.example.helpmiga.data.viewModel.ContatoViewModel
import com.example.helpmiga.data.viewModel.ContatoViewModelFactory
import com.example.helpmiga.databinding.ActivityContatosBinding


class ActivityContatos : AppCompatActivity() {
    private lateinit var binding: ActivityContatosBinding
    private var listaContatos = mutableListOf<Contato>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val _binding = ActivityContatosBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        binding = _binding
        setView()
        carregarContatos()
    }

    private val contatoViewModel: ContatoViewModel by viewModels {
        ContatoViewModelFactory((application as ContatoApplication).repository)
    }


      fun carregarContatos() {
//        val contatosDAO = ContatosDAO(applicationContext)
        listaContatos = contatoViewModel.listContato()
        //Configurar adapter
        val adapter = AdapterContatos(listaContatos)
        //Configurar RecyclerViewdapter
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext)
        binding.recyclerContatos.setLayoutManager(layoutManager)
        binding.recyclerContatos.setHasFixedSize(true)
        binding.recyclerContatos.addItemDecoration(DividerItemDecoration(applicationContext, LinearLayoutManager.VERTICAL))
        binding.recyclerContatos.setAdapter(adapter)
    }

    fun setView() {
        binding.buttonAdd.setOnClickListener{
            criaContato()
        }
    }


    fun criaContato() {
        var listaCont: MutableList<Contato> = mutableListOf()

        var contato = Contato()
        contato.nomeContato = "Moisés"
        contato.telefoneContato = "14997818811"
        contato.idBotao = 1L
        listaCont.add(contato)
        contatoViewModel.insert(contato)
    }

}
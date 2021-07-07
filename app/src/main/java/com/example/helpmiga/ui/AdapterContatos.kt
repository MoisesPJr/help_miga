package com.example.helpmiga.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.helpmiga.R


class AdapterContatos(listaContatos: List<Contato>) : RecyclerView.Adapter<AdapterContatos.MyViewHolder>() {
    private val listaContatos: List<Contato>
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val itemContato: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_contato, viewGroup, false)
        return MyViewHolder(itemContato)
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, i: Int) {
        val contatos: Contato = listaContatos[i]
        myViewHolder.nome.setText(contatos.nomeContato)
        myViewHolder.telefone.setText(contatos.telefoneContato)
    }

    override fun getItemCount(): Int {
        return listaContatos.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nome: TextView
        var telefone: TextView

        init {
            nome = itemView.findViewById(R.id.textNome)
            telefone = itemView.findViewById(R.id.textTelefone)
        }
    }

    init {
        this.listaContatos = listaContatos
    }
}


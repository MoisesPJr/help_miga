package com.example.helpmiga.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.helpmiga.R
import com.example.helpmiga.data.model.Contato


class AdapterContatos(var listaContatos: MutableList<Contato>, private val onClickType: (Contato) -> Unit) : RecyclerView.Adapter<AdapterContatos.MyViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val itemContato: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_contato, viewGroup, false)
        return MyViewHolder(itemContato)
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, i: Int) {
        val contatos: Contato = listaContatos[i]
        myViewHolder.nome.setText(contatos.nomeContato)
        myViewHolder.telefone.setText(contatos.telefoneContato)

        if(listaContatos.size == 1){
            myViewHolder.btnDell.visibility = View.GONE
        }else{
            myViewHolder.btnDell.visibility = View.VISIBLE
        }

        myViewHolder.btnDell.setOnClickListener {
            onClickType.invoke(contatos)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return listaContatos.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nome: TextView
        var telefone: TextView
        var btnDell: Button

        init {
            nome = itemView.findViewById(R.id.textNome)
            telefone = itemView.findViewById(R.id.textTelefone)
            btnDell = itemView.findViewById(R.id.btn_del)
        }
    }

    init {
        this.listaContatos = listaContatos
    }
}


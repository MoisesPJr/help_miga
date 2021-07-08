package com.example.helpmiga.data.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.helpmiga.data.db.ContatoDao
import com.example.helpmiga.ui.Contato
import kotlinx.coroutines.flow.Flow

class ContatoRepository(private val contatoDao: ContatoDao) {

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(contato: Contato) {
        contatoDao.insert(contato)
    }



       fun getallContatos(): List<Contato> {
        return contatoDao.listContato()
    }

}
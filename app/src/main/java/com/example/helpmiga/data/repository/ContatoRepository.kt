package com.example.helpmiga.data.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.helpmiga.data.db.ContatoDao
import com.example.helpmiga.ui.Contato

class ContatoRepository(private val contatoDao: ContatoDao) {

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(contato: Contato) {
        contatoDao.insert(contato)
    }


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun listContato() : MutableList<Contato> {
        return contatoDao.listContato()
    }

}
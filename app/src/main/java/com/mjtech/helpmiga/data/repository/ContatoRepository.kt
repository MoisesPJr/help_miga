package com.mjtech.helpmiga.data.repository

import androidx.annotation.WorkerThread

import com.mjtech.helpmiga.data.db.ContatoDao
import com.mjtech.helpmiga.data.model.Contato

class ContatoRepository(private val contatoDao: ContatoDao) {

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(contato: Contato) {
        contatoDao.insert(contato)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun apagarContato(contato: Contato) {
        contatoDao.apagarContato(contato)
    }


       fun getallContatos(): List<Contato> {
        return contatoDao.listContato()
    }

    fun getQtdContatos(): Int {
        return contatoDao.getQtdContato()
    }

}
package com.example.helpmiga.data.repository

import androidx.annotation.WorkerThread
import com.example.helpmiga.data.db.UsuarioDao
import com.example.helpmiga.data.model.Contato
import com.example.helpmiga.data.model.Usuario

class UsuarioRepository(private val usuarioDao: UsuarioDao) {

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(usuario: Usuario) {
        usuarioDao.insert(usuario)

    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun apagarUsuario(usuario: Usuario) {
        usuarioDao.apagarUsuario(usuario)
    }

}
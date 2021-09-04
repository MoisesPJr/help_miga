package com.mjtech.helpmiga.data.repository

import androidx.annotation.WorkerThread
import com.mjtech.helpmiga.data.db.UsuarioDao
import com.mjtech.helpmiga.data.model.Usuario

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
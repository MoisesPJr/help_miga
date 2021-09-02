package com.example.helpmiga.data.db

import androidx.room.*
import com.example.helpmiga.data.model.Usuario

@Dao
interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(usuario: Usuario)


    @Delete
    fun apagarUsuario(usuario: Usuario)


}
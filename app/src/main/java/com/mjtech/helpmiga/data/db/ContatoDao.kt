package com.mjtech.helpmiga.data.db

import androidx.room.*
import com.mjtech.helpmiga.data.model.Contato

@Dao
interface ContatoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(contato: Contato)

    @Query("SELECT * FROM contato")
    fun listContato(): List<Contato>

    @Delete
    fun apagarContato(contato: Contato)

    @Query("SELECT COUNT(id) from contato")
    fun getQtdContato(): Int
}
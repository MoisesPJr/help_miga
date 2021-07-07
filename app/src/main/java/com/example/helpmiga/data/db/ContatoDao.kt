package com.example.helpmiga.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.helpmiga.ui.Contato

@Dao
interface ContatoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(contato: Contato)

    @Query("SELECT * FROM CONTATO")
    suspend fun listContato() : MutableList<Contato>

}
package com.example.helpmiga.data.db

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.example.helpmiga.ui.Contato

@Dao
interface ContatoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(contato: Contato)

    @Query("SELECT * FROM contato")
      fun listContato() : List<Contato>

      @Delete
      fun apagarContato(contato: Contato)

      @Query("SELECT COUNT(id) from contato")
      fun getQtdContato() : Int
}
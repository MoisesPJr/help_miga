package com.example.helpmiga

import android.app.Application
import com.example.helpmiga.data.db.ContatoDao
import com.example.helpmiga.data.db.ContatoDataBase
import com.example.helpmiga.data.repository.ContatoRepository

class ContatoApplication : Application() {

    val database by lazy { ContatoDataBase.getDatabase(this) }
    val repository by lazy { ContatoRepository(database.contatoDao()) }


}
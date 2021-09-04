package com.mjtech.helpmiga

import android.app.Application
import com.mjtech.helpmiga.data.db.ContatoDataBase
import com.mjtech.helpmiga.data.repository.ContatoRepository

class ContatoApplication : Application() {

    val database by lazy { ContatoDataBase.getDatabase(this) }
    val repository by lazy { ContatoRepository(database.contatoDao()) }


}
package com.example.helpmiga.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.helpmiga.ui.Contato

@Database(entities = arrayOf(Contato::class), version = 1, exportSchema = false)
abstract class ContatoDataBase : RoomDatabase(){

    abstract fun contatoDao(): ContatoDao

    companion object {

        @Volatile
        private var INSTANCE: ContatoDataBase? = null

        fun getDatabase(context: Context): ContatoDataBase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ContatoDataBase::class.java,
                    "contato_database")
                    .allowMainThreadQueries()
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
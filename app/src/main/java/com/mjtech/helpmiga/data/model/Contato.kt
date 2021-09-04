package com.mjtech.helpmiga.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contato")
class Contato {
     @PrimaryKey @ColumnInfo(name = "id")
     var id: Int? = null
     var nomeContato: String? = null
     var telefoneContato: String? = null
     var idBotao: Long? = null
     var latitude = 0.0
     var longitude = 0.0

}
package com.example.helpmiga.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Usuario(
    @PrimaryKey
    val id: Int,
    val nome: String,
    val email: String
):Parcelable

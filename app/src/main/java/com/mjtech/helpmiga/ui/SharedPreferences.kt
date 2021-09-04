package com.mjtech.helpmiga.ui

import android.content.Context
import android.content.SharedPreferences
import com.mjtech.helpmiga.data.model.Usuario
import com.google.gson.Gson

class SharedPreferences(context: Context) {

    companion object {
        const val CODIGO = "PREFS_CODIGO"
        const val USER = "USER"
    }

    val sharedPreferences: SharedPreferences = context.getSharedPreferences("CODIGO", Context.MODE_PRIVATE)


    fun salvarCodigo(codigo: String) {
        sharedPreferences.edit().putString(CODIGO, codigo).commit()
    }

    fun getCodigo(): String? {
        return sharedPreferences.getString(CODIGO, "")
    }

    fun salvarUsuario(usuario: Usuario) {
        sharedPreferences.edit().putString(USER, Gson().toJson(usuario)).commit() //TODO COLOCAR O USUARIO NAS PREFS
    }

    fun getUser(): Usuario {
        val string = sharedPreferences.getString(USER, "")
        val usuario = Gson().fromJson(string, Usuario::class.java)
        return usuario
    }
}
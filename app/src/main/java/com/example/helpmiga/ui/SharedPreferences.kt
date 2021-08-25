package com.example.helpmiga.ui

import android.content.Context
import android.content.SharedPreferences

class SharedPreferences(context: Context) {

    companion object{
        const val CODIGO = "PREFS_CODIGO"
    }

    val sharedPreferences : SharedPreferences =  context.getSharedPreferences("CODIGO", Context.MODE_PRIVATE)


    fun salvarCodigo(codigo: String){
        sharedPreferences.edit().putString(CODIGO, codigo).commit()
    }

    fun getCodigo(): String? {
        return sharedPreferences.getString(CODIGO, "")
    }


}
package com.example.helpmiga.data.model

data class Requisicao(
    val id : Int,
    val latitude : Double,
    val longitude : Double,
    val codigoRequisicao: Int,
    val status : String
)
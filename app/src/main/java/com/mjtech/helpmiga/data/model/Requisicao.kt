package com.mjtech.helpmiga.data.model

data class Requisicao(
    var id : Int = 0,
    var latitude : Double = 0.0,
    var longitude : Double = 0.0,
    var codigoRequisicao: String = "",
    var status : String = ""
)


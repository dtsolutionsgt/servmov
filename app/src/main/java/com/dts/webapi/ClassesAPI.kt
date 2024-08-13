package com.dts.webapi

import kotlinx.serialization.Serializable

class ClassesAPI {

    @Serializable
    data class clsAPIUsers (
        var type : String = "",
        var UserId : Int = 0,
        var Empresa  : Int = 0,
        var UserName : String = "",
        var Password  : String = "",
        var IdNivelLicencia : Int = 0
    )

    @Serializable
    data class clsAPIEstadoOrden (
        var type : String = "",
        var CODIGO_ESTADO : Int = 0,
        var NOMBRE  : String = ""
    )

    @Serializable
    data class clsAPITipoOrden (
        var type : String = "",
        var CODIGO_TIPO : Int = 0,
        var NOMBRE  : String = ""
    )

    @Serializable
    data class clsAPITipoServicio (
        var type : String = "",
        var CODIGO_TIPO : Int = 0,
        var CODIGO_TICKET : Int = 0,
        var NOMBRE  : String = ""
    )

    @Serializable
    data class clsAPIOrdenCliente (
        var type : String = "",
        var CODIGO_CLIENTE : Int = 0,
        var NOMBRE : String = "",
        var DIRECCION : String = "",
        var TELEFONO : String = "",
    )

    @Serializable
    data class clsAPIOrdenContacto (
        var type : String = "",
        var CODIGO_CLIENTE_CONTACTO : Int = 0,
        var CODIGO_CLIENTE : Int = 0,
        var NOMBRE : String = "",
        var DIRECCION : String = "",
        var TELEFONO : String = "",
    )

    @Serializable
    data class clsAPIOrdenDet (
        var type : String = "",
        var CODIGO_ORDEN_SERVICIO_DET : Int = 0,
        var CODIGO_ORDEN_SERVICIO : Int = 0,
        var CODIGO_PRODUCTO  : Int = 0,
        var DESCRIPCION : String = "",
        var REALIZADO  : Int = 0,
        var CANTIDAD : Double = 0.0,
        var ACTIVO  : Int = 0,
    )

    @Serializable
    data class clsAPIOrdenDireccion (
        var type : String = "",
        var CODIGO_DIRECCION : Int = 0,
        var CODIGO_CLIENTE : Int = 0,
        var REFERENCIA : String = "",
        var DIRECCION : String = "",
        var ZONA_ENTREGA : String = "",
        var TELEFONO : String = "",
    )

    @Serializable
    data class clsAPIOrdenEnc (
        var type : String = "",
        var CODIGO_ORDEN_SERVICIO : Int = 0,
        var NUMERO : String = "",
        var FECHA : Long = 0L,
        var FECHA_CIERRE : Long = 0L,
        var HORA_SERVICIO_INI : Long = 0L,
        var HORA_SERVICIO_FIN : Long = 0L,
        var CODIGO_USUARIO_ASIGNADO : Int = 0,
        var CODIGO_ESTADO_ORDEN_SERVICIO : Int = 0,
        var CODIGO_TIPO_ORDEN_SERVICIO : Int = 0,
        var CODIGO_CLIENTE_CONTACTO : Int = 0,
        var CODIGO_DIRECCION : Int = 0,
        var CODIGO_CLIENTE : Int = 0,
        var DESCRIPCION : String = "",
    )


}
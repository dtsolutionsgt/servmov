package com.dts.base


class clsClasses {


    data class clsEstadoorden (
        var id : Int = 0,
        var nombre : String = "",
    )

    data class d_menuitem(
        val mid: Int,
        val nombre: String
    )

    data class clsLocItem(
        var id: Int,
        var fecha: Long,
        var longit: Double,
        var latit: Double,
        var bandera: Long,
    )


    data class clsOrdenenccap (
        var idorden : Int = 0,
        var anulada : Int = 0,
        var activa : Int = 0,
        var cerrada : Int = 0,
        var firmausuario : String = "",
        var firmacliente : String = "",
        var latit : Double = 0.0,
        var longit : Double = 0.0,
        var fechaini : Long = 0L,
        var fechafin : Long = 0L,
        var nota : String = "",
        var recibido : Int = 0,
    )

    data class clsOrdencliente (
        var id : Int = 0,
        var nombre : String = "",
        var dir : String = "",
        var tel : String = "",
    )

    data class clsOrdencont (
        var id : Int = 0,
        var idcliente : Int = 0,
        var nombre : String = "",
        var dir : String = "",
        var tel : String = "",
    )

    data class clsOrdendet (
        var id : Int = 0,
        var idorden : Int = 0,
        var idproducto : Int = 0,
        var descripcion : String = "",
        var realizado : Int = 0,
        var cant : Double = 0.0,
        var activo : Int = 0,
    )

    data class clsOrdendir (
        var id : Int = 0,
        var idcliente : Int = 0,
        var referencia : String = "",
        var dir : String = "",
        var zona : String = "",
        var tel : String = "",
    )


    data class clsOrdenenc (
        var idorden : Int = 0,
        var numero : String = "",
        var fecha : Long = 0L,
        var idusuario : Int = 0,
        var idestado : Int = 0,
        var idtipo : Int = 0,
        var idclicontact : Int = 0,
        var iddir : Int = 0,
        var idcliente : Int = 0,
        var descripcion : String = "",
        var fecha_cierre : Long = 0L,
        var hora_ini : Long = 0L,
        var hora_fin : Long = 0L,
    )

    data class clsOrdenlist (
        var idorden : Int = 0,
        var tarea : String = "",
        var cliente : String = "",
        var fecha : String = "",
        var estado : String = "",
        var idestado : Int = 0,
    )


    // 0 - idempresa, 1 - idusuario
    data class clsSavepos (
        var id : Int = 0,
        var valor : String = "",
    )

    data class clsTipoorden (
        var id : Int = 0,
        var nombre : String = "",
    )

    data class clsTiposervicio (
        var id : Int = 0,
        var idticket : Int = 0,
        var nombre : String = "",
    )

    data class clsUsuario (
        var id : Int = 0,
        var nombre : String = "",
        var clave : String = "",
        var activo : Int = 0,
        var rol : Int = 0,
    )

    data class clsUpdate (
        var SQL : String = ""
    )

    //region AppBase

    data class clsMenu (
        var id:Int = 0,
        var nombre: String? = null
    )

    data class clsParams (
        var id:Int = 0,
        var nombre: String? = null,
        var valor: String? = null
    )

    data class exListDlgItem(
        var idresource: Int,
        var codigo: String ,
        var text: String ,
        var text2: String
    )

    //endregion

}
package com.dts.classes

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.dts.base.BaseDatos
import com.dts.base.clsClasses.clsOrdencliente


class clsOrdenclienteObj {


    var count=0

    var cont: Context? = null
    var Con: BaseDatos? = null
    var db: SQLiteDatabase? = null
    var ins: BaseDatos.Insert? = null
    var upd: BaseDatos.Update? = null
    val clsCls = clsOrdencliente()

    val sel ="SELECT * FROM Ordencliente"
    var sql: String? = null
    var items = ArrayList<clsOrdencliente>()

    constructor(context: Context, dbconnection: BaseDatos, dbase: SQLiteDatabase) {
        cont = context
        Con = dbconnection
        ins = Con?.Ins
        upd = Con?.Upd
        db = dbase
        count = 0
    }

    fun reconnect(dbconnection: BaseDatos, dbase: SQLiteDatabase) {
        Con = dbconnection
        ins = Con!!.Ins
        upd = Con!!.Upd
        db = dbase
    }

    fun add(item: clsOrdencliente?) {
        addItem(item!!)
    }

    fun update(item: clsOrdencliente?) {
        updateItem(item!!)
    }

    fun delete(item: clsOrdencliente?) {
        deleteItem(item!!)
    }

    fun delete(item:Int) {
        deleteItem(item);
    }

    fun fill() {
        fillItems(sel)
    }

    fun fill( specstr: String) {
        fillItems(sel+ " " +specstr)
    }

    fun fillSelect(sq: String) {
        fillItems(sq)
    }

    fun first(): clsOrdencliente?  {
        return items[0]
    }

    //region Private

    private fun addItem(item: clsOrdencliente) {
        ins!!.init("Ordencliente")
        ins!!.add("id", item.id)
        ins!!.add("nombre", item.nombre)
        ins!!.add("dir", item.dir)
        ins!!.add("tel", item.tel)
        db!!.execSQL(ins!!.sql())
    }

    private fun updateItem(item: clsOrdencliente) {
        upd!!.init("Ordencliente")
        upd!!.add("nombre", item.nombre)
        upd!!.add("dir", item.dir)
        upd!!.add("tel", item.tel)
        upd!!.Where("(id=" + item.id + ")")
        db!!.execSQL(upd!!.sql())
    }

    private fun deleteItem(item: clsOrdencliente) {
        sql = "DELETE FROM Ordencliente WHERE (id=" + item.id + ")"
        db!!.execSQL(sql)
    }

    private fun deleteItem(id: Int) {
        sql = "DELETE FROM Ordencliente WHERE id=$id"
        db!!.execSQL(sql)
    }

    private fun fillItems(sq: String) {
        val dt: Cursor
        var item: clsOrdencliente
        items.clear()
        dt = Con!!.OpenDT(sq)
        count = dt.count
        if (dt.count > 0) dt.moveToFirst()
        while (!dt.isAfterLast) {
            item = clsOrdencliente()
            item.id = dt.getInt(0)
            item.nombre = dt.getString(1)
            item.dir = dt.getString(2)
            item.tel = dt.getString(3)
            items.add(item)
            dt.moveToNext()
        }
        if (dt != null) dt.close()
    }

    fun newID(idsql: String?): Int {
        var dt: Cursor? = null
        var nid: Int
        try {
            dt = Con!!.OpenDT(idsql)
            dt.moveToFirst()
            nid = dt.getInt(0) + 1
        } catch (e: Exception) {
            nid = 1
        }
        dt?.close()
        return nid
    }

    fun addItemSql(item: clsOrdencliente): String? {
        ins!!.init("Ordencliente")
        ins!!.add("id", item.id)
        ins!!.add("nombre", item.nombre)
        ins!!.add("dir", item.dir)
        ins!!.add("tel", item.tel)
        return ins!!.sql()
    }

    fun updateItemSql(item: clsOrdencliente): String? {
        upd!!.init("Ordencliente")
        upd!!.add("nombre", item.nombre)
        upd!!.add("dir", item.dir)
        upd!!.add("tel", item.tel)
        upd!!.Where("(id=" + item.id + ")")
        return upd!!.sql()
    }

    //endregion


}
package com.dts.classes

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.dts.base.BaseDatos
import com.dts.base.clsClasses.clsTipoorden


class clsTipoordenObj {

    var count=0

    var cont: Context? = null
    var Con: BaseDatos? = null
    var db: SQLiteDatabase? = null
    var ins: BaseDatos.Insert? = null
    var upd: BaseDatos.Update? = null
    val clsCls = clsTipoorden()

    val sel ="SELECT * FROM Tipoorden"
    var sql: String? = null
    var items = ArrayList<clsTipoorden>()

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

    fun add(item: clsTipoorden?) {
        addItem(item!!)
    }

    fun update(item: clsTipoorden?) {
        updateItem(item!!)
    }

    fun delete(item: clsTipoorden?) {
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

    fun first(): clsTipoorden?  {
        return items[0]
    }

    //region Private

    private fun addItem(item: clsTipoorden) {
        ins!!.init("Tipoorden")
        ins!!.add("id", item.id)
        ins!!.add("nombre", item.nombre)
        db!!.execSQL(ins!!.sql())
    }

    private fun updateItem(item: clsTipoorden) {
        upd!!.init("Tipoorden")
        upd!!.add("nombre", item.nombre)
        upd!!.Where("(id=" + item.id + ")")
        db!!.execSQL(upd!!.sql())
    }

    private fun deleteItem(item: clsTipoorden) {
        sql = "DELETE FROM Tipoorden WHERE (id=" + item.id + ")"
        db!!.execSQL(sql)
    }

    private fun deleteItem(id: Int) {
        sql = "DELETE FROM Tipoorden WHERE id=$id"
        db!!.execSQL(sql)
    }

    private fun fillItems(sq: String) {
        val dt: Cursor
        var item: clsTipoorden
        items.clear()
        dt = Con!!.OpenDT(sq)
        count = dt.count
        if (dt.count > 0) dt.moveToFirst()
        while (!dt.isAfterLast) {
            item = clsTipoorden()
            item.id = dt.getInt(0)
            item.nombre = dt.getString(1)
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

    fun addItemSql(item: clsTipoorden): String? {
        ins!!.init("Tipoorden")
        ins!!.add("id", item.id)
        ins!!.add("nombre", item.nombre)
        return ins!!.sql()
    }

    fun updateItemSql(item: clsTipoorden): String? {
        upd!!.init("Tipoorden")
        upd!!.add("nombre", item.nombre)
        upd!!.Where("(id=" + item.id + ")")
        return upd!!.sql()
    }

    //endregion



}


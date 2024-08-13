package com.dts.classes

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.dts.base.BaseDatos
import com.dts.base.clsClasses
import com.dts.base.clsClasses.clsOrdencont


class clsOrdencontObj {

    var count=0

    var cont: Context? = null
    var Con: BaseDatos? = null
    var db: SQLiteDatabase? = null
    var ins: BaseDatos.Insert? = null
    var upd: BaseDatos.Update? = null
    val clsCls = clsClasses()

    val sel ="SELECT * FROM Ordencont"
    var sql: String? = null
    var items = ArrayList<clsOrdencont>()

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

    fun add(item: clsOrdencont?) {
        addItem(item!!)
    }

    fun update(item: clsOrdencont?) {
        updateItem(item!!)
    }

    fun delete(item: clsOrdencont?) {
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

    fun first(): clsOrdencont?  {
        return items[0]
    }

    //region Private

    private fun addItem(item: clsOrdencont) {
        ins!!.init("Ordencont")
        ins!!.add("id", item.id)
        ins!!.add("idcliente", item.idcliente)
        ins!!.add("nombre", item.nombre)
        ins!!.add("dir", item.dir)
        ins!!.add("tel", item.tel)
        db!!.execSQL(ins!!.sql())
    }

    private fun updateItem(item: clsOrdencont) {
        upd!!.init("Ordencont")
        upd!!.add("idcliente", item.idcliente)
        upd!!.add("nombre", item.nombre)
        upd!!.add("dir", item.dir)
        upd!!.add("tel", item.tel)
        upd!!.Where("(id=" + item.id + ")")
        db!!.execSQL(upd!!.sql())
    }

    private fun deleteItem(item: clsOrdencont) {
        sql = "DELETE FROM Ordencont WHERE (id=" + item.id + ")"
        db!!.execSQL(sql)
    }

    private fun deleteItem(id: Int) {
        sql = "DELETE FROM Ordencont WHERE id=$id"
        db!!.execSQL(sql)
    }

    private fun fillItems(sq: String) {
        val dt: Cursor
        var item: clsOrdencont
        items.clear()
        dt = Con!!.OpenDT(sq)
        count = dt.count
        if (dt.count > 0) dt.moveToFirst()
        while (!dt.isAfterLast) {
            item = clsOrdencont()
            item.id = dt.getInt(0)
            item.idcliente = dt.getInt(1)
            item.nombre = dt.getString(2)
            item.dir = dt.getString(3)
            item.tel = dt.getString(4)
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

    fun addItemSql(item: clsOrdencont): String? {
        ins!!.init("Ordencont")
        ins!!.add("id", item.id)
        ins!!.add("idcliente", item.idcliente)
        ins!!.add("nombre", item.nombre)
        ins!!.add("dir", item.dir)
        ins!!.add("tel", item.tel)
        return ins!!.sql()
    }

    fun updateItemSql(item: clsOrdencont): String? {
        upd!!.init("Ordencont")
        upd!!.add("idcliente", item.idcliente)
        upd!!.add("nombre", item.nombre)
        upd!!.add("dir", item.dir)
        upd!!.add("tel", item.tel)
        upd!!.Where("(id=" + item.id + ")")
        return upd!!.sql()
    }

    //endregion

}
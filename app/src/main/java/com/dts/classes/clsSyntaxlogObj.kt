package com.dts.classes

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.dts.base.BaseDatos
import com.dts.base.clsClasses
import com.dts.base.clsClasses.clsSyntaxlog


class clsSyntaxlogObj {

    var count=0

    var cont: Context? = null
    var Con: BaseDatos? = null
    var db: SQLiteDatabase? = null
    var ins: BaseDatos.Insert? = null
    var upd: BaseDatos.Update? = null
    val clsCls = clsClasses()

    val sel ="SELECT * FROM Syntaxlog"
    var sql: String? = null
    var items = ArrayList<clsSyntaxlog>()

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

    fun add(item: clsSyntaxlog?) {
        addItem(item!!)
    }

    fun update(item: clsSyntaxlog?) {
        updateItem(item!!)
    }

    fun delete(item: clsSyntaxlog?) {
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

    fun first(): clsSyntaxlog?  {
        return items[0]
    }


    //region Private

    private fun addItem(item: clsSyntaxlog) {
        ins!!.init("Syntaxlog")
        ins!!.add("id", item.id)
        ins!!.add("fecha", item.fecha.toDouble())
        ins!!.add("cmd", item.cmd)
        db!!.execSQL(ins!!.sql())
    }

    private fun updateItem(item: clsSyntaxlog) {
        upd!!.init("Syntaxlog")
        upd!!.add("fecha", item.fecha.toDouble())
        upd!!.add("cmd", item.cmd)
        upd!!.Where("(id=" + item.id + ")")
        db!!.execSQL(upd!!.sql())
    }

    private fun deleteItem(item: clsSyntaxlog) {
        sql = "DELETE FROM Syntaxlog WHERE (id=" + item.id + ")"
        db!!.execSQL(sql)
    }

    private fun deleteItem(id: Int) {
        sql = "DELETE FROM Syntaxlog WHERE id=$id"
        db!!.execSQL(sql)
    }

    private fun fillItems(sq: String) {
        val dt: Cursor
        var item: clsSyntaxlog
        items.clear()
        dt = Con!!.OpenDT(sq)
        count = dt.count
        if (dt.count > 0) dt.moveToFirst()
        while (!dt.isAfterLast) {
            item = clsSyntaxlog()
            item.id = dt.getInt(0)
            item.fecha = dt.getLong(1)
            item.cmd = dt.getString(2)
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

    fun addItemSql(item: clsSyntaxlog): String? {
        ins!!.init("Syntaxlog")
        ins!!.add("id", item.id)
        ins!!.add("fecha", item.fecha.toDouble())
        ins!!.add("cmd", item.cmd)
        return ins!!.sql()
    }

    fun updateItemSql(item: clsSyntaxlog): String? {
        upd!!.init("Syntaxlog")
        upd!!.add("fecha", item.fecha.toDouble())
        upd!!.add("cmd", item.cmd)
        upd!!.Where("(id=" + item.id + ")")
        return upd!!.sql()
    }

    //endregion

}
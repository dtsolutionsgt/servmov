package com.dts.classes

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.dts.base.BaseDatos
import com.dts.base.clsClasses
import com.dts.base.clsClasses.clsUpdcmd


class clsUpdcmdObj {

    var count=0

    var cont: Context? = null
    var Con: BaseDatos? = null
    var db: SQLiteDatabase? = null
    var ins: BaseDatos.Insert? = null
    var upd: BaseDatos.Update? = null
    val clsCls = clsClasses()

    val sel ="SELECT * FROM Updcmd"
    var sql: String? = null
    var items = ArrayList<clsUpdcmd>()

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

    fun add(item: clsUpdcmd?) {
        addItem(item!!)
    }

    fun update(item: clsUpdcmd?) {
        updateItem(item!!)
    }

    fun delete(item: clsUpdcmd?) {
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

    fun first(): clsUpdcmd?  {
        return items[0]
    }


    //region Private

    private fun addItem(item: clsUpdcmd) {
        ins!!.init("Updcmd")
        ins!!.add("id", item.id)
        ins!!.add("cmd", item.cmd)
        db!!.execSQL(ins!!.sql())
    }

    private fun updateItem(item: clsUpdcmd) {
        upd!!.init("Updcmd")
        upd!!.add("cmd", item.cmd)
        upd!!.Where("(id=" + item.id + ")")
        db!!.execSQL(upd!!.sql())
    }

    private fun deleteItem(item: clsUpdcmd) {
        sql = "DELETE FROM Updcmd WHERE (id=" + item.id + ")"
        db!!.execSQL(sql)
    }

    private fun deleteItem(id: Int) {
        sql = "DELETE FROM Updcmd WHERE id=$id"
        db!!.execSQL(sql)
    }

    private fun fillItems(sq: String) {
        val dt: Cursor
        var item: clsUpdcmd
        items.clear()
        dt = Con!!.OpenDT(sq)
        count = dt.count
        if (dt.count > 0) dt.moveToFirst()
        while (!dt.isAfterLast) {
            item = clsUpdcmd()
            item.id = dt.getInt(0)
            item.cmd = dt.getString(1)
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

    fun addItemSql(item: clsUpdcmd): String? {
        ins!!.init("Updcmd")
        ins!!.add("id", item.id)
        ins!!.add("cmd", item.cmd)
        return ins!!.sql()
    }

    fun updateItemSql(item: clsUpdcmd): String? {
        upd!!.init("Updcmd")
        upd!!.add("cmd", item.cmd)
        upd!!.Where("(id=" + item.id + ")")
        return upd!!.sql()
    }

    //endregion


}
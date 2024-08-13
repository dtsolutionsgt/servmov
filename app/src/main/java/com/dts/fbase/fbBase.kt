package com.dts.fbase

import android.os.Handler
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

open class fbBase(troot: String?) {

    var fdb: FirebaseDatabase

    var fdt: DatabaseReference

    var callBack: Runnable?
    var value: String? = null

    var root: String
    var errflag = false

    init {
        fdb = FirebaseDatabase.getInstance()
        //fdb.setPersistenceEnabled(true);
        root = troot!!
        fdt = fdb.getReference(root)
        fdt.keepSynced(true)
        callBack = null
    }

    fun runCallBack() {
        if (callBack == null) return
        val cbhandler = Handler()
        cbhandler.postDelayed({ callBack!!.run() }, 50)
    }

    fun removeValue(key: String) {
        fdb.getReference(root+"/"+key).removeValue()
    }

    val key: String?
        get() = fdt.push().key


}
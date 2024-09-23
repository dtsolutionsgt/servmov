package com.dts.fbase

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DataSnapshot
import com.dts.base.clsClasses

class fbLocUlt (troot: String?) : fbBase(troot) {

    var litem: clsClasses.clsCoordUlt? = null
    var items = ArrayList<clsClasses.clsCoordUlt>()

    fun listItems(rnCallback: Runnable) {
        try {
            items.clear()
            fdb.getReference(root).get().addOnCompleteListener(OnCompleteListener<DataSnapshot> { task ->
                if (task.isSuccessful) {

                    items.clear()
                    val res = task.result

                    if (res.exists()) {
                        for (node in res.children) {

                            litem = clsClasses.clsCoordUlt(0,0,0.0,0.0)

                            litem!!.id = node.child("id").getValue(Int::class.java)!!
                            litem!!.fecha = node.child("fecha").getValue(Long::class.java)!!
                            litem!!.longit = node.child("longit").getValue(Double::class.java)!!
                            litem!!.latit = node.child("latit").getValue(Double::class.java)!!

                            items.add(litem!!)
                        }
                    }
                    errflag = false
                } else {
                    value = task.exception!!.message
                    errflag = true
                }
                callBack = rnCallback
                runCallBack()
            })
        } catch (e: Exception) {
            value = e.message
            errflag = true
        }
    }


}
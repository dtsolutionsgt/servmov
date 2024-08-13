package com.dts.fbase

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DataSnapshot
import com.dts.base.clsClasses

class fbLocItem (troot: String?) : fbBase(troot) {

    var litem: clsClasses.clsLocItem? = null
    var items = ArrayList<clsClasses.clsLocItem>()

    fun setItem(item: clsClasses.clsLocItem?) {
        fdt = fdb.getReference(root+"/"+item?.id)
        fdt.setValue(item)
    }

    fun setItem(vroot:String , item: clsClasses.clsLocItem?) {
        fdt = fdb.getReference(vroot+"/"+item?.id)
        fdt.setValue(item)
    }

    fun listItems(rnCallback: Runnable) {
        try {
            items.clear()
            fdb.getReference(root).get().addOnCompleteListener(OnCompleteListener<DataSnapshot> { task ->
                if (task.isSuccessful) {

                    items.clear()
                    val res = task.result

                    if (res.exists()) {
                        for (node in res.children) {

                            litem = clsClasses.clsLocItem(0,0,0.0,0.0,0)

                            litem!!.id = node.child("id").getValue(Int::class.java)!!
                            litem!!.fecha = node.child("fecha").getValue(Long::class.java)!!
                            litem!!.longit = node.child("longit").getValue(Double::class.java)!!
                            litem!!.latit = node.child("latit").getValue(Double::class.java)!!
                            litem!!.bandera = node.child("bandera").getValue(Long::class.java)!!

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
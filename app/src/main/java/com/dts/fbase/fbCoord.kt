package com.dts.fbase

import com.dts.base.clsClasses
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DataSnapshot

class fbCoord (troot: String?) : fbBaseCoord(troot) {

    var litem: clsClasses.clsCoordItem? = null
    var items = ArrayList<clsClasses.clsCoordItem>()
    var keys = ArrayList<String>()

    fun setItem(path:String , item: clsClasses.clsCoordItem?) {
        fdt = fdb.getReference(root+"/"+path+"/"+item?.id)
        fdt.setValue(item)
    }

    fun setItemUlt(item: clsClasses.clsCoordUlt?) {
        fdt = fdb.getReference("coordult/"+item?.id)
        fdt.setValue(item)
    }

    fun listItems(path:String,rnCallback: Runnable) {
        try {
            items.clear()
            fdb.getReference(root+"/"+path).get().addOnCompleteListener(OnCompleteListener<DataSnapshot> { task ->
                if (task.isSuccessful) {

                    items.clear()
                    val res = task.result

                    if (res.exists()) {
                        for (node in res.children) {

                            litem = clsClasses.clsCoordItem(0,0.0,0.0)

                            litem!!.id = node.child("id").getValue(Int::class.java)!!
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

    fun listDates(path:String,dl:Int,rnCallback: Runnable) {
        var kkey=""
        var ikey=0

        try {
            keys.clear()
            fdb.getReference(root+"/"+path).get().addOnCompleteListener(OnCompleteListener<DataSnapshot> { task ->
                if (task.isSuccessful) {

                    keys.clear()
                    val res = task.result

                    if (res.exists()) {
                        for (node in res.children) {
                            kkey=node.key!!
                            try {
                                ikey=kkey.toInt()
                                if (ikey<dl) keys.add(kkey)
                            } catch (e: Exception) {
                            }
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

    fun refreshConn(path:String) {
        try {
            fdb.getReference(root+"/"+path).get().addOnCompleteListener(OnCompleteListener<DataSnapshot> { task ->
                if (task.isSuccessful) {
                    val res = task.result
                }
            })
        } catch (e: Exception) { }
    }


}
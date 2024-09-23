package com.dts.ladapt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dts.sermov.R
import com.dts.base.clsClasses

class LA_TimeAdapter(val itemList: ArrayList<clsClasses.clsTimeItem>) : RecyclerView.Adapter<LA_TimeAdapter.ViewHolder>() {

    var selectedItemPosition: Int = -1
    lateinit var lay: RelativeLayout

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LA_TimeAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.lv_timeitem, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: LA_TimeAdapter.ViewHolder, position: Int) {
        val item = itemList[position]
        val isSelected = position == selectedItemPosition

        holder.bindItems(itemList[position])

        holder.bind(item, isSelected)

        holder.itemView.setOnClickListener {
            val previousSelectedPosition = selectedItemPosition
            selectedItemPosition = position

            notifyItemChanged(previousSelectedPosition)
            notifyItemChanged(position)
        }
    }

    fun setSelectedItem(selpos:Int) {
        val previousSelectedPosition = selectedItemPosition
        selectedItemPosition = selpos

        notifyItemChanged(previousSelectedPosition)
        notifyItemChanged(selpos)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        fun bindItems(mitem: clsClasses.clsTimeItem) {
            val textViewName = itemView.findViewById(R.id.textView1) as TextView
            lay = itemView.findViewById(R.id.rellay) as RelativeLayout

            textViewName.text = mitem.nombre
        }

        fun bind(mitem: clsClasses.clsTimeItem, isSelected: Boolean) {
            lay.setBackgroundResource(if (isSelected)
                R.drawable.frame_btn_sel else R.drawable.frame_btn)
        }

        override fun onClick(p0: View?) {}

    }

}

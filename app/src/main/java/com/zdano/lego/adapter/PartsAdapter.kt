package com.zdano.lego.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.zdano.lego.R
import com.zdano.lego.database.DataBaseHelper
import com.zdano.lego.model.Part

class PartsAdapter(private val context: Context,
                   private var dataSource:
                   ArrayList<Part>): BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private lateinit var db : DataBaseHelper

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rowView = inflater.inflate(R.layout.row_inventory, parent, false)

        val title = rowView.findViewById<TextView>(R.id.part_list_title)
        val subtitle = rowView.findViewById<TextView>(R.id.part_list_subtitle)
        val thumbnail = rowView.findViewById<ImageView>(R.id.part_list_thumbnail)

        val part = getItem(position) as Part
        val resourceId = R.mipmap.ic_launcher
        db = DataBaseHelper(this.context)
        title.text = db.getTitle(part.itemID, part.colorID)
        subtitle.text = part.quantityInSet.toString() + "/" + part.quantityInStore.toString()
        thumbnail.setImageBitmap(db.getImage(part.itemID, part.colorID))

        return rowView
    }


}
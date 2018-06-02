package com.zdano.lego.adapter

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteException
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
        val thumbnails = rowView.findViewById<ImageView>(R.id.part_list_thumbnail)

        val part = getItem(position) as Part

        db = DataBaseHelper(this.context)
        title.text = getTitle(part.itemID, part.colorID)
        subtitle.text = part.quantityInSet.toString() + "/" + part.quantityInStore.toString()


        return rowView
    }

    private fun getTitle(itemId: Int, colorId: Int): String {
        var cursor: Cursor? = null
        var name: String = ""
        try {
            db.openDataBase()
            cursor = db.readableDatabase.query("Parts" , arrayOf("Name"), "Code = $itemId", null, null, null, null)
            if (cursor.moveToFirst()) {
                name = cursor.getString(cursor.getColumnIndex("Name"))
            }
        } catch (e: SQLiteException) {

        } finally {
            cursor?.close()
            db.close()
        }
        return name
    }
}
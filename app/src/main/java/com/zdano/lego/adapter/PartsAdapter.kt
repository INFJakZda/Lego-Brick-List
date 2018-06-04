package com.zdano.lego.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.zdano.lego.R
import com.zdano.lego.model.Part

class PartsAdapter(private val context: Context,
                   private var dataSource:
                   ArrayList<Part>): BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

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
        val view: View
        val holder: ViewHolder

        if(convertView == null) {
            view = inflater.inflate(R.layout.row_inventory, parent, false)

            holder = ViewHolder()
            holder.title = view.findViewById<TextView>(R.id.part_list_title)
            holder.subtitle = view.findViewById<TextView>(R.id.part_list_subtitle)
            holder.thumbnail = view.findViewById<ImageView>(R.id.part_list_thumbnail)

            view.tag = holder
        } else {
            view = convertView
            holder = convertView.tag as ViewHolder
        }

        val title = holder.title
        val subtitle = holder.subtitle
        val thumbnail = holder.thumbnail

        val part = getItem(position) as Part

        title.text = part.title
        subtitle.text = part.quantityInSet.toString() + "/" + part.quantityInStore.toString()
        thumbnail.setImageBitmap(part.image)

        return view
    }

    private class ViewHolder {
        lateinit var title: TextView
        lateinit var subtitle: TextView
        lateinit var thumbnail: ImageView
    }
}
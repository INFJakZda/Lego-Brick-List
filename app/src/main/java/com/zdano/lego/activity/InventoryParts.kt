package com.zdano.lego.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.zdano.lego.R
import com.zdano.lego.adapter.PartsAdapter
import com.zdano.lego.database.DataBaseHelper
import com.zdano.lego.model.Inventory
import kotlinx.android.synthetic.main.activity_inventory_parts.*

class InventoryParts : AppCompatActivity() {

    private lateinit var db : DataBaseHelper
    var projectId: Int = 0

    companion object {
        const val EXTRA_ID = "id"
        const val EXTRA_NAME = "name"

        fun newIntent(context: Context, inventory: Inventory): Intent {
            val detailIntent = Intent(context, InventoryParts::class.java)

            detailIntent.putExtra(EXTRA_ID, inventory.id)
            detailIntent.putExtra(EXTRA_NAME, inventory.name)

            return detailIntent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory_parts)

        db = DataBaseHelper(this)
        title = intent.extras.getString(EXTRA_NAME)
        projectId = intent.extras.getInt(EXTRA_ID)

        showPartList(projectId)
    }

    private fun showPartList(id: Int) {
        var partList = db.getPartList(id)

        val adapter = PartsAdapter(this, partList)
        part_list_view.adapter = adapter
    }
}

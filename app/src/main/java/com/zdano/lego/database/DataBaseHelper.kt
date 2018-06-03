package com.zdano.lego.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.zdano.lego.model.Inventory
import com.zdano.lego.model.Part
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class DataBaseHelper
/**
 * Constructor
 * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
 * @param context
 */
(private val myContext: Context) : SQLiteOpenHelper(myContext, DB_NAME, null, 1) {

    private var myDataBase: SQLiteDatabase? = null

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     */
    @Throws(IOException::class)
    fun createDataBase() {

        val dbExist = checkDataBase()

        if (dbExist) {
            //do nothing - database already exist
        } else {

            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.readableDatabase

            try {

                copyDataBase()

            } catch (e: IOException) {

                throw Error("Error copying database")

            }

        }

    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private fun checkDataBase(): Boolean {

        var checkDB: SQLiteDatabase? = null

        try {
            val myPath = DB_PATH + DB_NAME
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY)

        } catch (e: SQLiteException) {

            //database does't exist yet.

        }

        if (checkDB != null) {

            checkDB.close()

        }

        return if (checkDB != null) true else false
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     */
    @Throws(IOException::class)
    private fun copyDataBase() {

        //Open your local db as the input stream
        val myInput = myContext.assets.open(DB_NAME)

        // Path to the just created empty db
        val outFileName = DB_PATH + DB_NAME

        //Open the empty db as the output stream
        val myOutput = FileOutputStream(outFileName)

        //transfer bytes from the inputfile to the outputfile
        val buffer = ByteArray(1024)
        var length: Int
        length = myInput.read(buffer)
        while (length  > 0) {
            myOutput.write(buffer, 0, length)
            length = myInput.read(buffer)
        }

        //Close the streams
        myOutput.flush()
        myOutput.close()
        myInput.close()

    }

    @Throws(SQLException::class)
    fun openDataBase() {

        //Open the database
        val myPath = DB_PATH + DB_NAME
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY)

    }

    @Synchronized
    override fun close() {

        if (myDataBase != null)
            myDataBase!!.close()

        super.close()

    }

    override fun onCreate(db: SQLiteDatabase) {

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }

    companion object {

        //The Android's default system path of your application database.
        private val DB_PATH = "/data/data/com.zdano.lego/databases/"

        private val DB_NAME = "BrickList.db"
    }

    // Add your public helper methods to access and get content from the database.
    // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
    // to you to create adapters for your views.

    fun getInventoryList(): ArrayList<Inventory> {

        val inventoryList = ArrayList<Inventory>()

        try {
            this.openDataBase()

            var cursor = this.readableDatabase.query("Inventories" , arrayOf("_id, Name, Active, LastAccessed"), null, null, null, null, "LastAccessed DESC")

            if (cursor.moveToFirst()) {
                do {
                    var id = cursor.getInt(cursor.getColumnIndex("_id"))
                    var name = cursor.getString(cursor.getColumnIndex("Name"))
                    var active = cursor.getInt(cursor.getColumnIndex("Active"))
                    var lastAccessed = cursor.getInt(cursor.getColumnIndex("LastAccessed"))

                    inventoryList.add(Inventory(id, name, active, lastAccessed))
                } while (cursor.moveToNext())
            }
            cursor.close()
            this.close()
        } catch (e: SQLiteException) {

        }
        return inventoryList
    }

    fun getPartList(code: Int): ArrayList<Part> {
        val partList = ArrayList<Part>()

        try {
            this.openDataBase()

            var cursor = this.readableDatabase.query("InventoriesParts" , arrayOf("_id, InventoryID, TypeID, ItemID, QuantityInSet, QuantityInStore, ColorID, Extra"), "InventoryID = " + code.toString(), null, null, null, "QuantityInStore")

            if (cursor.moveToFirst()) {
                do {
                    var id = cursor.getInt(cursor.getColumnIndex("_id"))
                    var inventoryId = cursor.getInt(cursor.getColumnIndex("InventoryID"))
                    var typeId = cursor.getInt(cursor.getColumnIndex("TypeID"))
                    var itemId = cursor.getInt(cursor.getColumnIndex("ItemID"))
                    var quantityInSet = cursor.getInt(cursor.getColumnIndex("QuantityInSet"))
                    var quantityInStore = cursor.getInt(cursor.getColumnIndex("QuantityInStore"))
                    var colorId = cursor.getInt(cursor.getColumnIndex("ColorID"))
                    var extra = cursor.getInt(cursor.getColumnIndex("Extra"))

                    partList.add(Part(id, inventoryId, typeId, itemId, quantityInSet, quantityInStore, colorId, extra))
                } while (cursor.moveToNext())
            }
            cursor.close()
            this.close()
        } catch (e: SQLiteException) {
            Log.i("SQLERR", e.toString())
        }
        return partList
    }

    fun createProject(projectCode: String): Int {
        this.openDataBase()

        val values = ContentValues()
        values.put("Name", """Projekt $projectCode""")
        values.put("Active", 1)
        values.put("LastAccessed", Calendar.getInstance().timeInMillis.toInt())

        var id = this.writableDatabase.insert("Inventories", null, values)
        this.close()
        return id.toInt()
    }

    fun getTitle(itemId: Int, colorId: Int): String {
        var cursor: Cursor? = null
        var name: String = ""
        try {
            this.openDataBase()
            cursor = this.readableDatabase.query("Parts" , arrayOf("Name"), "Code = $itemId", null, null, null, null)
            if (cursor.moveToFirst()) {
                name = cursor.getString(cursor.getColumnIndex("Name"))
            }
        } catch (e: SQLiteException) {

        } finally {
            cursor?.close()
            this.close()
        }
        return name
    }

    fun getImage(itemId: Int, colorId: Int): Bitmap? {

        this.openDataBase()

        var idCursor = this.readableDatabase.query("Parts" , arrayOf("_id"), "Code = " + itemId.toString(), null, null, null, null)
        var id: Int = 0
        if (idCursor.moveToFirst()) {
            id = idCursor.getInt(idCursor.getColumnIndex("_id"))
        }

        var colorCursor = this.readableDatabase.query("Colors" , arrayOf("_id"), "Code = " + colorId.toString(), null, null, null, null)
        var color: Int = 0
        if (colorCursor.moveToFirst()) {
            color = colorCursor.getInt(colorCursor.getColumnIndex("_id"))
        }

        var cursor = this.readableDatabase.query("Codes", arrayOf("Image"), "ItemID = " + id.toString() + " and ColorID = " + color.toString(), null, null, null, null)

        if (cursor.count > 0) {
            cursor.moveToFirst()
            if (cursor.getBlob(cursor.getColumnIndex("Image")) != null) {
                var image = cursor.getBlob(cursor.getColumnIndex("Image"))
                val bmp = BitmapFactory.decodeByteArray(image, 0, image.size)
                return Bitmap.createScaledBitmap(bmp, 250, 250, false)
            }

        }
        this.close()
        cursor.close()
        idCursor.close()
        colorCursor.close()
        return null
    }
}
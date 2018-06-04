package com.zdano.lego.model

import android.graphics.Bitmap

class Part(
        val id: Int,
        val inventoryID: Int,
        val typeID: Int,
        val itemID: Int,
        val quantityInSet: Int,
        val quantityInStore: Int,
        val colorID: Int,
        val extra: Int,
        val title: String,
        val image: Bitmap?)
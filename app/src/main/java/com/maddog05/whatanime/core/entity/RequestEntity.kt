package com.maddog05.whatanime.core.entity

import android.graphics.Bitmap

class RequestEntity {
    companion object {
        const val STATUS_REQUESTING = 0
        const val STATUS_DONE = 1
        const val STATUS_INCOMPLETED = 2
    }

    var id: Long = 0
    var bitmap: Bitmap? = null
    var date: Long = 0
    var status = STATUS_REQUESTING
    val responses = mutableListOf<ResponseEntity>()
    //FOR UI
    var imageUrl: String = ""
}
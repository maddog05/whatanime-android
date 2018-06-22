package com.maddog05.whatanime.core.database

import android.content.Context
import com.maddog05.whatanime.core.entity.RequestEntity
import com.maddog05.whatanime.core.entity.ResponseEntity

interface LogicDatabase {
    fun init(context: Context)

    fun addRequest(request: RequestEntity): Long

    fun updateRequest(request: RequestEntity)

    fun setResponsesToRequest(requestId: Long, response: List<ResponseEntity>)

    fun getRequest(requestId: Long): RequestEntity

    fun getAllResquests(): List<RequestEntity>

    fun clearRequests()
}
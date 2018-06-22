package com.maddog05.whatanime.core.database.tables

import com.maddog05.whatanime.core.entity.RequestEntity
import io.realm.RealmList
import io.realm.RealmObject

open class RequestEntityDB : RealmObject() {

    companion object {
        const val FIELD_ID = "id"
    }

    var id: Long = 0
    var image: String = ""
    var date: Long = 0
    var status = -1
    var responses: RealmList<ResponseEntityDB> = RealmList()

    fun copyToRequest(): RequestEntity {
        val entity = RequestEntity()
        entity.id = id
        entity.imageUrl = image
        entity.date = date
        entity.status = status
        for (dbResponse in responses) {
            entity.responses.add(dbResponse.copyToResponse())
        }
        return entity
    }
}
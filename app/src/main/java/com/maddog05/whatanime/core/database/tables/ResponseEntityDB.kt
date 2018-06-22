package com.maddog05.whatanime.core.database.tables

import com.maddog05.whatanime.core.entity.ResponseEntity
import io.realm.RealmObject

open class ResponseEntityDB : RealmObject() {

    var id: Long = 0
    var name: String = ""
    var episode: String = ""
    var similarity: Double = 0.0
    var atTime: Double = 0.0//USED IN GET SAMPLE FROM SERVER
    //FOR DEVICE
    var localImage: String = ""
    var remoteImage: String = ""
    var localVideo: String = ""
    var remoteVideo: String = ""
    //TO GET FROM SERVER
    var season: String = ""
    var anime: String = ""
    var fileName: String = ""
    var tokenThumb: String = ""

    fun copyToResponse(): ResponseEntity {
        val entity = ResponseEntity()
        entity.id = id
        entity.name = name
        entity.episode = episode
        entity.similarity = similarity
        entity.atTime = atTime

        entity.localImage = localImage
        entity.remoteImage = remoteImage
        entity.localVideo = localVideo
        entity.remoteVideo = remoteVideo

        entity.season = season
        entity.anime = anime
        entity.fileName = fileName
        entity.tokenThumb = tokenThumb
        return entity
    }
}
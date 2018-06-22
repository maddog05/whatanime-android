package com.maddog05.whatanime.core.entity

class ResponseEntity {
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
}
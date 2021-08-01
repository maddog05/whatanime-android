package com.maddog05.whatanime.core.entity

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.JsonObject
import com.maddog05.whatanime.util.Mapper

class SearchImageResult() : Parcelable {
    var anilistId: Int = 0
    var filename = ""
    var episode = ""
    var startSecond: Double = 0.0
    var endSecond: Double = 0.0
    var similarity: Double = 0.0
    var videoUrl = ""
    var imageUrl = ""

    constructor(parcel: Parcel) : this() {
        anilistId = parcel.readInt()
        filename = parcel.readString()?:""
        episode = parcel.readString()?:""
        startSecond = parcel.readDouble()
        endSecond = parcel.readDouble()
        similarity = parcel.readDouble()
        videoUrl = parcel.readString()?:""
        imageUrl = parcel.readString()?:""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(anilistId)
        parcel.writeString(filename)
        parcel.writeString(episode)
        parcel.writeDouble(startSecond)
        parcel.writeDouble(endSecond)
        parcel.writeDouble(similarity)
        parcel.writeString(videoUrl)
        parcel.writeString(imageUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SearchImageResult> {
        override fun createFromParcel(parcel: Parcel): SearchImageResult {
            return SearchImageResult(parcel)
        }

        override fun newArray(size: Int): Array<SearchImageResult?> {
            return arrayOfNulls(size)
        }

        fun parseJson(json: JsonObject): SearchImageResult {
            val response = SearchImageResult()
            response.anilistId = Mapper.elementInt(json.get("anilist"), 0)
            response.filename = Mapper.elementString(json.get("filename"), "")
            response.startSecond = Mapper.elementDouble(json.get("from"), 0.0)
            response.endSecond = Mapper.elementDouble(json.get("to"), 0.0)
            response.similarity = Mapper.elementDouble(json.get("similarity"), 0.0)
            response.videoUrl = Mapper.elementString(json.get("video"), "")
            response.imageUrl = Mapper.elementString(json.get("image"), "")
            return response
        }
    }
}
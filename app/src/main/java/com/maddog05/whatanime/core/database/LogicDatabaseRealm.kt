package com.maddog05.whatanime.core.database

import android.content.Context
import com.maddog05.whatanime.core.database.tables.RequestEntityDB
import com.maddog05.whatanime.core.database.tables.ResponseEntityDB
import com.maddog05.whatanime.core.entity.RequestEntity
import com.maddog05.whatanime.core.entity.ResponseEntity
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmMigration
import io.realm.annotations.RealmModule

class LogicDatabaseRealm : LogicDatabase {

    private val schemaVersion: Long = 1

    override fun init(context: Context) {
        Realm.init(context)
        Realm.setDefaultConfiguration(getRealmConfiguration())
    }

    override fun addRequest(request: RequestEntity): Long {
        val realm = with()
        val newId = realm.where(RequestEntityDB::class.java)
                .count() + 1
        realm.executeTransaction {
            val dbRequest = realm.createObject(RequestEntityDB::class.java)
            dbRequest.id = newId
            dbRequest.image = request.imageUrl
            dbRequest.status = request.status
            dbRequest.date = request.date
        }
        realm.close()
        return newId
    }

    override fun updateRequest(request: RequestEntity) {
        val realm = with()
        val dbRequest = realm.where(RequestEntityDB::class.java)
                .equalTo(RequestEntityDB.FIELD_ID, request.id)
                .findFirst()
        if (dbRequest != null) {
            realm.executeTransaction {
                dbRequest.image = request.imageUrl
                dbRequest.status = request.status
                dbRequest.date = request.date
            }
        }
        realm.close()
    }

    override fun setResponsesToRequest(requestId: Long, response: List<ResponseEntity>) {
        val realm = with()
        val dbRequest = realm.where(RequestEntityDB::class.java)
                .equalTo(RequestEntityDB.FIELD_ID, requestId)
                .findFirst()
        if (dbRequest != null) {
            realm.executeTransaction {
                for (item in response) {
                    val newId = realm.where(ResponseEntityDB::class.java)
                            .count() + 1
                    val dbResponse = realm.createObject(ResponseEntityDB::class.java)
                    dbResponse.id = newId
                    dbResponse.name = item.name
                    dbResponse.episode = item.episode
                    dbResponse.similarity = item.similarity
                    dbResponse.atTime = item.atTime

                    dbResponse.localImage = item.localImage
                    dbResponse.remoteImage = item.remoteImage
                    dbResponse.localVideo = item.localVideo
                    dbResponse.remoteVideo = item.remoteVideo

                    dbResponse.season = item.season
                    dbResponse.anime = item.anime
                    dbResponse.fileName = item.fileName
                    dbResponse.tokenThumb = item.tokenThumb

                    dbRequest.responses.add(dbResponse)
                }
            }
        }
        realm.close()
    }

    override fun getRequest(requestId: Long): RequestEntity {
        val realm = with()
        val dbRequest = realm.where(RequestEntityDB::class.java)
                .equalTo(RequestEntityDB.FIELD_ID, requestId)
                .findFirst()
        return dbRequest?.copyToRequest() ?: RequestEntity()
    }

    override fun getAllResquests(): List<RequestEntity> {
        val requests = mutableListOf<RequestEntity>()
        val realm = with()
        val dbRequests = realm.where(RequestEntityDB::class.java)
                .findAll()
        for (dbRequest in dbRequests) {
            requests.add(dbRequest.copyToRequest())
        }
        realm.close()
        return requests
    }

    override fun clearRequests() {
        val realm = with()
        realm.beginTransaction()
        realm.delete(RequestEntityDB::class.java)
        realm.commitTransaction()
        realm.close()
    }

    private fun with(): Realm = Realm.getDefaultInstance()

    private fun getRealmConfiguration(): RealmConfiguration {
        return RealmConfiguration.Builder()
                .modules(ModuleDatabase())
                .schemaVersion(schemaVersion)
                .migration(getMigration())
                .build()
    }

    private fun getMigration(): RealmMigration {
        return RealmMigration { realm, oldVersion, newVersion ->
            {
                //FOR FUTURE RELEASES
            }
        }
    }

    @RealmModule(classes = [
        (RequestEntityDB::class),
        (ResponseEntityDB::class)
    ])
    class ModuleDatabase
}
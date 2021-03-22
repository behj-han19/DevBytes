/*
 * Copyright (C) 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.devbyteviewer.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.android.devbyteviewer.database.VideosDatabase
import com.example.android.devbyteviewer.database.asDomainModel
import com.example.android.devbyteviewer.domain.DevByteVideo
import com.example.android.devbyteviewer.network.DevByteNetwork
import com.example.android.devbyteviewer.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

// Pass in a VideosDatabase object as the class's constructor parameter to access the Dao methods.
class VideosRepository(private val database: VideosDatabase){

    // refreshVideos() will be the API used to refresh the offline cache.
    // refreshVideos() should be a suspended function because it performs a database operation,
    // it must be called from a coroutine.
    suspend fun refreshVideos(){
        // switch the coroutine context to Dispatchers.IO to perform network and database operations.
        withContext(Dispatchers.IO){
            Timber.d("refresh videos is called");
            // fetch the DevByte video playlist from the network using the Retrofit service instance, DevByteNetwork.
            val playlist = DevByteNetwork.devbytes.getPlaylist()

            // after fetching the playlist from the network, store the playlist in the Room database.
            // To store the playlist, use the VideosDatabase object, database. Call the insertAll DAO method,
            // passing in the playlist retrieved from the network. Use the asDatabaseModel() extension function
            // to map the playlist to the database object.
            database.videoDao.insertAll(playlist.asDatabaseModel())
        }
    }

    /**
     * create a LiveData object to read the video playlist from the database. This LiveData object is automatically updated
     * when the database is updated. The attached fragment, or the activity, is refreshed with new values.

     * In the VideosRepository class, declare a LiveData object called videos to hold a list of DevByteVideo objects.
     * Initialize the videos object, using database.videoDao. Call the getVideos() DAO method. Because the getVideos() method
     * returns a list of database objects, not a list of DevByteVideo objects, Android Studio throws a "type mismatch" error.
     */

    // Transformations.map to convert the list of database objects to a list of domain objects. Use the asDomainModel() conversion function.
    // The Transformations.map method uses a conversion function to convert one LiveData object into another LiveData object. The
    // transformations are only calculated when an active activity or a fragment is observing the returned LiveData property.
    val videos: LiveData<List<DevByteVideo>> = Transformations.map(database.videoDao.getVideos()){
        it.asDomainModel()
    }
}
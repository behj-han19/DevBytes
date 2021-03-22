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

package com.example.android.devbyteviewer.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface VideoDao{

    // called getVideos() to fetch all the videos from the database.
    // return type of this method to LiveData, so that the data displayed in the UI is
    // refreshed whenever the data in the database is changed.
    @Query("select * from databasevideo")
    fun getVideos(): LiveData<List<DatabaseVideo>>

    // insertAll() method to insert a list of videos fetched from the network into the database.
    // For simplicity, overwrite the database entry if the video entry is already present in the
    // database. To do this, use the onConflict argument to set the conflict strategy to REPLACE.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(videos: List<DatabaseVideo>)
}

@Database(entities = [DatabaseVideo :: class], version = 1)
abstract class VideosDatabase: RoomDatabase(){
    abstract val videoDao: VideoDao
}

// Create a private lateinit variable called INSTANCE outside the classes, to hold the singleton object.
// VideosDatabase should be singleton to prevent having multiple instances of the database opened
// at the same time.
private lateinit var INSTANCE:VideosDatabase


// The .isInitialized Kotlin property returns true if the lateinit property (INSTANCE in this example)
// has been assigned a value, and false otherwise.
fun getDatabase(context: Context): VideosDatabase{
    synchronized(VideosDatabase::class.java){
        if(!::INSTANCE.isInitialized){
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                    VideosDatabase::class.java,
                    "videos").build()
        }
    }
    return INSTANCE
}
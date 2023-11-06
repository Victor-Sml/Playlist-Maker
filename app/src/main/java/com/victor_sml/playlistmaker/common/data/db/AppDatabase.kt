package com.victor_sml.playlistmaker.common.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.victor_sml.playlistmaker.common.data.db.dao.PlaylistDao
import com.victor_sml.playlistmaker.common.data.db.dao.TrackDao
import com.victor_sml.playlistmaker.common.data.db.entity.PlaylistEntity
import com.victor_sml.playlistmaker.common.data.db.entity.PlaylistTrackCrossRef
import com.victor_sml.playlistmaker.common.data.db.entity.TrackEntity

@Database(version = 1, entities = [TrackEntity::class, PlaylistEntity::class, PlaylistTrackCrossRef::class])
abstract class AppDatabase: RoomDatabase() {

    abstract fun trackDao(): TrackDao

    abstract fun playlistDao(): PlaylistDao
}
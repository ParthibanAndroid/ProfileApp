package com.learning.database.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.learning.database.converter.DatabaseConverters
import com.learning.database.profile.ProfileDao
import com.learning.database.profile.ProfileEntity
import com.learning.database.profile.SyncOperationDao
import com.learning.database.profile.SyncOperationEntity

@Database(
    entities = [ProfileEntity::class, SyncOperationEntity::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(DatabaseConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao

    abstract fun syncOperationDao(): SyncOperationDao
}

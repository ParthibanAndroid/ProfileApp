package com.learning.database.di

import android.content.Context
import androidx.room.Room
import com.learning.database.database.AppDatabase
import com.learning.database.profile.ProfileDao
import com.learning.database.profile.SyncOperationDao
import com.learning.database.transaction.ProfileDatabaseOperations
import com.learning.database.transaction.ProfileDatabaseOperationsImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase =
        Room
            .databaseBuilder(
                context = context,
                klass = AppDatabase::class.java,
                name = "profile.db",
            ).build()

    @Provides
    @Singleton
    fun provideProfileDao(database: AppDatabase): ProfileDao = database.profileDao()

    @Provides
    @Singleton
    fun provideSyncOperationDao(database: AppDatabase): SyncOperationDao = database.syncOperationDao()

    @Provides
    @Singleton
    fun provideProfileDatabaseOperations(implementation: ProfileDatabaseOperationsImpl): ProfileDatabaseOperations = implementation
}

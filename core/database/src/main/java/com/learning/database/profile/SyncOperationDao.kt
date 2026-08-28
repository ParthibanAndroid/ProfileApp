package com.learning.database.profile

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SyncOperationDao {
    @Insert
    suspend fun insert(operation: SyncOperationEntity)

    @Query(
        """
        SELECT * FROM sync_operations
        ORDER BY createdAt ASC
        """,
    )
    suspend fun getPendingOperations(): List<SyncOperationEntity>

    @Delete
    suspend fun delete(operation: SyncOperationEntity)

    @Query(
        """
        UPDATE sync_operations
        SET retryCount = retryCount + 1
        WHERE operationId = :operationId
        """,
    )
    suspend fun incrementRetryCount(operationId: String)
}

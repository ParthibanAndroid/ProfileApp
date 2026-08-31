package com.learning.sync

interface ProfileSyncHandler {
    suspend fun sync(): SyncResult
}

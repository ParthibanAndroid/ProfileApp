package com.learning.sync

interface SyncResult {
    data object Success : SyncResult

    data object Retry : SyncResult

    data class Failure(
        val message: String,
    ) : SyncResult
}

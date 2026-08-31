package com.learning.sync

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ProfileSyncWorker
    @AssistedInject
    constructor(
        @Assisted appContext: Context,
        @Assisted params: WorkerParameters,
        private val syncHandler: ProfileSyncHandler,
    ) : CoroutineWorker(
            appContext = appContext,
            params = params,
        ) {
        companion object {
            private const val TAG = "ProfileSyncWorker"
        }

        override suspend fun doWork(): Result {
            Log.d(
                TAG,
                "doWork() STARTED - id=$id",
            )

            return when (val result = syncHandler.sync()) {
                is SyncResult.Success -> {
                    Log.d(
                        TAG,
                        "Sync completed successfully",
                    )

                    Result.success()
                }

                is SyncResult.Retry -> {
                    Log.d(
                        TAG,
                        "Temporary failure. WorkManager will retry.",
                    )

                    Result.retry()
                }

                is SyncResult.Failure -> {
                    Log.e(
                        TAG,
                        "Permanent failure: ${result.message}",
                    )

                    Result.failure()
                }

                else -> {
                    Log.e(
                        TAG,
                        "Unknown sync result: $result",
                    )

                    Result.failure()
                }
            }
        }
    }

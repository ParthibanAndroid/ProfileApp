package com.learning.sync

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ProfileSyncSchedulerImpl
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : ProfileSyncScheduler {
        companion object {
            private const val WORK_NAME = "profile_sync"
        }

        override fun schedule() {
            val constraints =
                Constraints
                    .Builder()
                    .setRequiredNetworkType(
                        NetworkType.CONNECTED,
                    ).build()

            val request =
                OneTimeWorkRequestBuilder<ProfileSyncWorker>()
                    .setConstraints(constraints)
                    .setBackoffCriteria(
                        BackoffPolicy.EXPONENTIAL,
                        10,
                        TimeUnit.SECONDS,
                    ).build()

            WorkManager
                .getInstance(context)
                .enqueueUniqueWork(
                    WORK_NAME,
                    ExistingWorkPolicy.KEEP,
                    request,
                )
        }
    }

package com.rwazi.app.todo.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rwazi.app.todo.data.local.NoteDao
import com.rwazi.app.todo.data.local.SyncStatus
import com.rwazi.app.todo.data.mapper.toRemote
import com.rwazi.app.todo.data.remote.NoteRemoteDataSource
import com.rwazi.app.todo.domain.repository.AuthRepository

import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val noteDao: NoteDao,
    private val remoteDataSource: NoteRemoteDataSource,
    private val authRepository: AuthRepository
) : CoroutineWorker(context, params) {

    init {
        Timber.d("SyncWorker: Instantiated with Hilt successfully")
    }

    override suspend fun doWork(): Result {
        Timber.d("SyncWorker: Starting doWork")
        
        val uid = authRepository.currentUser?.id ?: run {
            Timber.w("SyncWorker: User not logged in, retrying...")
            return Result.retry()
        }

        val notesToSync = noteDao.getNotesToSync()
        Timber.d("SyncWorker: Found ${notesToSync.size} notes to sync")
        
        if (notesToSync.isEmpty()) {
            Timber.d("SyncWorker: Nothing to sync, success")
            return Result.success()
        }

        return try {
            notesToSync.forEach { note ->
                Timber.d("SyncWorker: Process note ${note.id} (isDeleted=${note.isDeleted})")
                if (note.isDeleted) {
                    remoteDataSource.updateNoteField(uid, note.id, "deleted", true)
                    noteDao.deleteNotePermanently(note.id)
                    Timber.d("SyncWorker: Permanently deleted note ${note.id} after remote sync")
                } else {
                    remoteDataSource.saveNote(uid, note.toRemote())
                    noteDao.updateNote(note.copy(syncStatus = SyncStatus.SYNCED))
                    Timber.d("SyncWorker: Sync success for note ${note.id}")
                }
            }
            Timber.d("SyncWorker: All notes synced successfully")
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "SyncWorker: Error during sync")
            Result.retry()
        }
    }
}

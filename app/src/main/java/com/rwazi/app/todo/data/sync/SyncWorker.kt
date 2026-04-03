package com.rwazi.app.todo.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rwazi.app.todo.data.local.NoteDao
import com.rwazi.app.todo.data.local.SyncStatus
import com.rwazi.app.todo.data.mapper.toRemote
import com.rwazi.app.todo.data.remote.NoteRemoteDataSource
import com.rwazi.app.todo.data.repository.AuthRepository
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

    override suspend fun doWork(): Result {
        val uid = authRepository.currentUser?.id ?: run {
            Timber.w("SyncWorker: User not logged in, retrying...")
            return Result.retry()
        }

        val notesToSync = noteDao.getNotesToSync()
        if (notesToSync.isEmpty()) return Result.success()

        return try {
            notesToSync.forEach { note ->
                if (note.isDeleted) {
                    remoteDataSource.updateNoteField(uid, note.id, "deleted", true)
                } else {
                    remoteDataSource.saveNote(uid, note.toRemote())
                }
                noteDao.updateNote(note.copy(syncStatus = SyncStatus.SYNCED))
            }
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "SyncWorker failed")
            Result.retry()
        }
    }
}

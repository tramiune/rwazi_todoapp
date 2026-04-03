package com.rwazi.app.todo.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rwazi.app.todo.data.local.NoteDao
import com.rwazi.app.todo.data.local.SyncStatus
import com.rwazi.app.todo.data.mapper.toRemote
import com.rwazi.app.todo.util.AppConstants
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await
import timber.log.Timber

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val noteDao: NoteDao,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val uid = auth.currentUser?.uid ?: run {
            Timber.w("SyncWorker: User not yet logged in, retrying...")
            return Result.retry()
        }
        val notesToSync = noteDao.getNotesToSync()
        if (notesToSync.isEmpty()) return Result.success()

        val collection = firestore.collection(AppConstants.COLLECTION_USERS).document(uid).collection(AppConstants.COLLECTION_NOTES)

        return try {
            notesToSync.forEach { note ->
                if (note.isDeleted) {
                    collection.document(note.id)
                        .update("deleted", true)
                        .await()
                    noteDao.updateNote(note.copy(syncStatus = SyncStatus.SYNCED))
                } else {
                    collection.document(note.id)
                        .set(note.toRemote())
                        .await()
                    noteDao.updateNote(note.copy(syncStatus = SyncStatus.SYNCED))
                }
            }
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "SyncWorker failed")
            Result.retry()
        }
    }
}

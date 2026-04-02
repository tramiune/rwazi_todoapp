package com.rwazi.app.todo.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.firestore.FirebaseFirestore
import com.rwazi.app.todo.data.local.NoteDao
import com.rwazi.app.todo.data.local.NoteEntity
import com.rwazi.app.todo.data.local.SyncStatus
import com.rwazi.app.todo.data.mapper.toEntity
import com.rwazi.app.todo.data.mapper.toRemote
import com.rwazi.app.todo.data.repository.AuthRepository
import com.rwazi.app.todo.data.repository.NoteRepository
import com.rwazi.app.todo.data.sync.SyncWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao,
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository,
    private val workManager: androidx.work.WorkManager
) : NoteRepository {

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var syncJobStarted = false
    private var snapshotListener: com.google.firebase.firestore.ListenerRegistration? = null

    init {
        repositoryScope.launch {
            authRepository.getAuthState().collect { user ->
                val uid = user?.uid
                if (uid != null && !syncJobStarted) {
                    startRealtimeSync(uid)
                } else if (uid == null) {
                    stopRealtimeSync()
                }
            }
        }
    }

    private fun startRealtimeSync(uid: String) {
        if (syncJobStarted) return
        syncJobStarted = true

        snapshotListener = firestore.collection("users").document(uid).collection("notes")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Timber.e(e, "Snapshot listener failed")
                    return@addSnapshotListener
                }

                snapshot?.documents?.mapNotNull { it.toObject(com.rwazi.app.todo.data.remote.NoteRemote::class.java) }?.let { remotes ->
                    repositoryScope.launch {
                        val entities = remotes.map { it.toEntity(SyncStatus.SYNCED) }
                        entities.forEach { entity ->
                            val local = noteDao.getNoteById(entity.id)
                            if (local == null || entity.updatedAt > (local.updatedAt)) {
                                noteDao.insertNote(entity)
                            }
                        }
                    }
                }
            }
    }

    private fun stopRealtimeSync() {
        snapshotListener?.remove()
        snapshotListener = null
        syncJobStarted = false
    }

    private val currentUserId: String?
        get() = authRepository.currentUser?.uid

    private fun getNotesCollection() = currentUserId?.let { uid ->
        firestore.collection("users").document(uid).collection("notes")
    }

    override fun getNotesPaged(query: String, sortOrder: com.rwazi.app.todo.util.SortOrder): Flow<PagingData<NoteEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                if (query.isBlank()) {
                    when (sortOrder) {
                        com.rwazi.app.todo.util.SortOrder.NEWEST_FIRST -> noteDao.getAllNotesPagedDesc()
                        com.rwazi.app.todo.util.SortOrder.OLDEST_FIRST -> noteDao.getAllNotesPagedAsc()
                    }
                } else {
                    when (sortOrder) {
                        com.rwazi.app.todo.util.SortOrder.NEWEST_FIRST -> noteDao.searchNotesPagedDesc(query)
                        com.rwazi.app.todo.util.SortOrder.OLDEST_FIRST -> noteDao.searchNotesPagedAsc(query)
                    }
                }
            }
        ).flow
    }

    override suspend fun addNote(note: NoteEntity) {
        noteDao.insertNote(note.copy(syncStatus = SyncStatus.PENDING))
        try {
            val collection = getNotesCollection()
            if (collection == null) {
                Timber.w("NoteRepository: User not logged in, syncing skipped (will retry via Worker)")
                return
            }
            collection.document(note.id).set(note.toRemote()).await()
            noteDao.updateNote(note.copy(syncStatus = SyncStatus.SYNCED))
            Timber.d("NoteRepository: Note synced successfully to Firestore")
        } catch (e: Exception) {
            Timber.e(e, "Failed to sync added note to Firestore")
        } finally {
            syncNotes()
        }
    }

    override suspend fun updateNote(note: NoteEntity) {
        noteDao.updateNote(note.copy(syncStatus = SyncStatus.PENDING, updatedAt = System.currentTimeMillis()))
        try {
            val collection = getNotesCollection()
            if (collection == null) {
                Timber.w("NoteRepository: User not logged in, update deferred")
                return
            }
            collection.document(note.id).set(note.toRemote()).await()
            noteDao.updateNote(note.copy(syncStatus = SyncStatus.SYNCED))
            Timber.d("NoteRepository: Note updated successfully in Firestore")
        } catch (e: Exception) {
            Timber.e(e, "Failed to sync updated note to Firestore")
        } finally {
            syncNotes()
        }
    }

    override suspend fun deleteNote(id: String) {
        noteDao.softDeleteNote(id)
        try {
            val collection = getNotesCollection()
            if (collection == null) {
                Timber.w("NoteRepository: User not logged in, delete deferred")
                return
            }
            collection.document(id).update("deleted", true).await()
            Timber.d("NoteRepository: Note marked as deleted in Firestore")
        } catch (e: Exception) {
            Timber.e(e, "Failed to sync deleted note to Firestore")
        } finally {
            syncNotes()
        }
    }

    override suspend fun syncNotes() {
        val constraints = androidx.work.Constraints.Builder()
            .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
            .build()
        
        val syncRequest = androidx.work.OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .build()
            
        workManager.enqueueUniqueWork(
            "note_sync",
            androidx.work.ExistingWorkPolicy.REPLACE,
            syncRequest
        )
    }
}

package com.rwazi.app.todo.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.firestore.DocumentChange
import com.rwazi.app.todo.data.local.NoteDao
import com.rwazi.app.todo.data.local.SyncStatus
import com.rwazi.app.todo.data.mapper.toDomain
import com.rwazi.app.todo.data.mapper.toEntity
import com.rwazi.app.todo.data.mapper.toRemote
import com.rwazi.app.todo.data.remote.NoteRemote
import com.rwazi.app.todo.data.remote.NoteRemoteDataSource
import com.rwazi.app.todo.data.sync.SyncWorker
import com.rwazi.app.todo.ui.model.Note
import com.rwazi.app.todo.util.AppConstants
import com.rwazi.app.todo.util.SortOrder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao,
    private val remoteDataSource: NoteRemoteDataSource,
    private val authRepository: AuthRepository,
    private val workManager: WorkManager
) : NoteRepository {

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var syncJob: Job? = null

    init {
        repositoryScope.launch {
            authRepository.getAuthState().collect { user ->
                val uid = user?.uid
                if (uid != null) {
                    startRealtimeSync(uid)
                } else {
                    stopRealtimeSync()
                }
            }
        }
    }

    private fun startRealtimeSync(uid: String) {
        if (syncJob?.isActive == true) return
        
        syncJob = repositoryScope.launch {
            remoteDataSource.getNoteChanges(uid).collect { changes ->
                changes.forEach { (type, remote) ->
                    val entity = remote.toEntity(SyncStatus.SYNCED)
                    
                    Timber.d("Sync: Received $type for ${entity.id} (updatedAt: ${entity.updatedAt})")
                    when (type) {
                        DocumentChange.Type.ADDED, DocumentChange.Type.MODIFIED -> {
                            val local = noteDao.getNoteById(entity.id)
                            if (local == null || local.syncStatus == SyncStatus.SYNCED || entity.updatedAt > local.updatedAt) {
                                noteDao.insertNote(entity)
                            }
                        }
                        DocumentChange.Type.REMOVED -> {
                            noteDao.deleteNotePermanently(entity.id)
                        }
                    }
                }
            }
        }
    }

    private fun stopRealtimeSync() {
        syncJob?.cancel()
        syncJob = null
    }

    override fun getNotesPaged(query: String, sortOrder: SortOrder): Flow<PagingData<Note>> {
        return Pager(
            config = PagingConfig(
                pageSize = AppConstants.SYNC_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                if (query.isBlank()) {
                    when (sortOrder) {
                        SortOrder.NEWEST_FIRST -> noteDao.getAllNotesPagedDesc()
                        SortOrder.OLDEST_FIRST -> noteDao.getAllNotesPagedAsc()
                    }
                } else {
                    when (sortOrder) {
                        SortOrder.NEWEST_FIRST -> noteDao.searchNotesPagedDesc(query)
                        SortOrder.OLDEST_FIRST -> noteDao.searchNotesPagedAsc(query)
                    }
                }
            }
        ).flow.map { pagingData -> pagingData.map { it.toDomain() } }
    }

    override suspend fun addNote(note: Note) {
        performRemoteAction(note) { uid, remote ->
            remoteDataSource.saveNote(uid, remote)
        }
    }

    override suspend fun updateNote(note: Note) {
        performRemoteAction(note.copy(updatedAt = System.currentTimeMillis())) { uid, remote ->
            remoteDataSource.saveNote(uid, remote)
        }
    }

    override suspend fun deleteNote(id: String) {
        val note = noteDao.getNoteById(id)?.toDomain() ?: return
        performRemoteAction(note.copy(isDeleted = true)) { uid, _ ->
            remoteDataSource.updateNoteField(uid, id, "deleted", true)
        }
    }

    /**
     * Common helper to orchestrate local update -> remote sync -> status update.
     */
    private suspend fun performRemoteAction(
        note: Note,
        action: suspend (String, NoteRemote) -> Unit
    ) {
        val entity = note.toEntity(SyncStatus.PENDING)
        noteDao.insertNote(entity)

        val uid = authRepository.currentUser?.uid
        if (uid == null) {
            Timber.w("Repository: User not logged in, sync deferred to Worker")
            syncNotes()
            return
        }

        try {
            action(uid, entity.toRemote())
            noteDao.updateNote(entity.copy(syncStatus = SyncStatus.SYNCED))
            Timber.d("Repository: Remote action successful for ${entity.id}")
        } catch (e: Exception) {
            Timber.e(e, "Repository: Remote action failed for ${entity.id}")
        } finally {
            syncNotes()
        }
    }

    override suspend fun getNoteById(id: String): Note? = noteDao.getNoteById(id)?.toDomain()

    override fun getNoteFlow(id: String): Flow<Note?> = noteDao.getNoteFlow(id).map { it?.toDomain() }

    override suspend fun syncNotes() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniqueWork(
            AppConstants.WORK_SYNC_NOTES,
            ExistingWorkPolicy.REPLACE,
            syncRequest
        )
    }

    override suspend fun clearLocalData() {
        stopRealtimeSync()
        noteDao.clearAllNotes()
        workManager.cancelUniqueWork(AppConstants.WORK_SYNC_NOTES)
    }
}


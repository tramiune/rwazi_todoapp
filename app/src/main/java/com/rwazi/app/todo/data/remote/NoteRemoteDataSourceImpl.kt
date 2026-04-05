package com.rwazi.app.todo.data.remote

import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.rwazi.app.todo.util.AppConstants
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRemoteDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : NoteRemoteDataSource {

    private fun getNotesCollection(uid: String) =
        firestore.collection(AppConstants.COLLECTION_USERS)
            .document(uid)
            .collection(AppConstants.COLLECTION_NOTES)

    override fun getNoteChanges(uid: String): Flow<List<Pair<DocumentChange.Type, NoteRemote>>> = callbackFlow {
        val listener = getNotesCollection(uid).addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
                return@addSnapshotListener
            }

            val changes = snapshot?.documentChanges?.mapNotNull { dc ->
                val note = dc.document.toObject(NoteRemote::class.java).copy(id = dc.document.id)
                Pair(dc.type, note)
            } ?: emptyList()

            trySend(changes)
        }

        awaitClose { 
            Timber.d("RemoteDataSource: Closing snapshot listener for $uid")
            listener.remove() 
        }
    }

    override suspend fun saveNote(uid: String, note: NoteRemote) {
        getNotesCollection(uid).document(note.id).set(note).await()
    }

    override suspend fun updateNoteField(uid: String, noteId: String, field: String, value: Any) {
        getNotesCollection(uid).document(noteId)
            .set(mapOf(field to value), SetOptions.merge())
            .await()
    }
}

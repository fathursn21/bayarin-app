package id.ac.pnm.bayarin_app.data.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import id.ac.pnm.bayarin_app.data.dao.NotesDao
import id.ac.pnm.bayarin_app.data.model.Notes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await

class NotesRepository(
    private val notesDao: NotesDao
) {
    private val db = FirebaseDatabase.getInstance()

    suspend fun createNotes(
        notes: Notes
    ) {
        notesDao.insert(notes)
    }

    suspend fun getAllNotes(limit : Int): Flow<List<Notes>> {
        return notesDao.getAllNotes(limit)
    }

    suspend fun sync() {
        val uid = Firebase.auth.currentUser?.uid ?: ""

        syncFromFirebase(uid)
        syncToFirebase(uid)
    }

    suspend fun syncFromFirebase(uid: String) {

        val snapshot = db.reference
            .child("notes")
            .child(uid)
            .get()
            .await()

        snapshot.children.forEach { item ->

//            Log.d("FIREBASE_KEY", item.key.toString())
//            Log.d("FIREBASE_VALUE", item.value.toString())

            val note = item.getValue(Notes::class.java)
                ?: return@forEach

            notesDao.insert(
                note.copy(
                    id = item.key ?: "",
                    isSynced = 1
                )
            )
        }
    }

    suspend fun syncToFirebase(uid: String) {

        notesDao.getUnsyncedNotes()
            .forEach { note ->

                db.reference
                    .child("notes")
                    .child(uid)
                    .child(note.id)
                    .setValue(
                        note.copy(
                            isSynced = 1
                        )
                    )
                    .await()

                notesDao.markAsSynced(note.id)
            }
    }

//    fun observeNotes(
//        uid: String,
//        limit: Int = 0,
//        onResult: (List<Notes>) -> Unit,
//        onError: (String) -> Unit
//    ) {
//
//        var query: Query = db.reference
//            .child("notes")
//            .child(uid)
//
//        if (limit > 0) {
//            query = query
//                .orderByChild("createdAt")
//                .limitToLast(limit)
//        }
//
//        query.addValueEventListener(
//                object : ValueEventListener {
//
//                    override fun onDataChange(
//                        snapshot: DataSnapshot
//                    ) {
//
//                        val notes = mutableListOf<Notes>()
//
//                        snapshot.children.forEach { child ->
//
//                            val note =
//                                child.getValue(Notes::class.java)
//
//                            note?.let {
//                                notes.add(
//                                    it.copy(
//                                        id = child.key ?: ""
//                                    )
//                                )
//                            }
//                        }
//
//                        notes.sortByDescending { it.createdAt }
//
//                        onResult(notes)
//                    }
//
//                    override fun onCancelled(
//                        error: DatabaseError
//                    ) {
//                        onError(error.message)
//                    }
//                }
//            )
//    }

//    fun createNotes(
//        expense : Boolean,
//        nominal : String,
//        category : String,
//        date : Long,
//        note : String,
//        onSuccess: () -> Unit,
//        onError: (Exception) -> Unit
//    ){
//        val uid = Firebase.auth.currentUser?.uid ?: ""
//
//        val note = Notes(
//            expense = expense,
//            nominal = nominal.toLong(),
//            category = category,
//            date = date,
//            note = note,
//            createdAt = System.currentTimeMillis()
//        )
//
//        db.reference
//            .child("notes")
//            .child(uid)
//            .push()
//            .setValue(note)
//            .addOnSuccessListener {
//                onSuccess()
//            }
//            .addOnFailureListener {
//                onError(it)
//            }
//    }

}
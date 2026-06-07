package id.ac.pnm.bayarin_app.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import id.ac.pnm.bayarin_app.data.model.Notes

class NotesRepository {

    private val db = FirebaseDatabase.getInstance()

    fun observeNotes(
        uid: String,
        limit: Int = 0,
        onResult: (List<Notes>) -> Unit,
        onError: (String) -> Unit
    ) {

        var query: Query = db.reference
            .child("notes")
            .child(uid)

        if (limit > 0) {
            query = query
                .orderByChild("createdAt")
                .limitToLast(limit)
        }

        query.addValueEventListener(
                object : ValueEventListener {

                    override fun onDataChange(
                        snapshot: DataSnapshot
                    ) {

                        val notes = mutableListOf<Notes>()

                        snapshot.children.forEach { child ->

                            val note =
                                child.getValue(Notes::class.java)

                            note?.let {
                                notes.add(
                                    it.copy(
                                        id = child.key ?: ""
                                    )
                                )
                            }
                        }

                        notes.sortByDescending { it.createdAt }

                        onResult(notes)
                    }

                    override fun onCancelled(
                        error: DatabaseError
                    ) {
                        onError(error.message)
                    }
                }
            )
    }

}
package id.ac.pnm.bayarin_app.ui.newnotes

import android.content.ContentValues.TAG
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import id.ac.pnm.bayarin_app.data.model.Notes
import id.ac.pnm.bayarin_app.data.repository.NotesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate

class NewNotesViewModel : ViewModel() {
    private val repository = NotesRepository()

    private val _uiState = MutableStateFlow(NewNotesUiState())
    val uiState: StateFlow<NewNotesUiState> = _uiState.asStateFlow()

    private val database = Firebase.database.reference

    var showExpense by mutableStateOf(true)
        private set

    fun updateVisibleExpense(visible: Boolean){
        showExpense = visible
    }

    var userTypeNominal by mutableStateOf("0")
    private set

    fun updateTypeNominal(userType: String){
        userTypeNominal = userType.filter { it.isDigit() }
    }

    var updateUserCategory by mutableStateOf("Makan")
        private set

    fun updateSelectCategory(userSelect: String){
        updateUserCategory = userSelect
    }

    var updateUserDate by mutableLongStateOf(System.currentTimeMillis())
        private set

    fun updateSelectedDate(date: Long) {
        updateUserDate = date
    }

    var showDatepicker by mutableStateOf(false)
        private set

    fun updateVisibleDatepicker(visible: Boolean){
        showDatepicker = visible
    }

    var userTypeNote by mutableStateOf("")
        private set

    fun updateTypeNote(userType: String) {
        userTypeNote = userType
    }

    fun addNotes(expense : Boolean, nominal : String, category : String, date : Long, note : String){

        if (nominal.isNotEmpty() && category.isNotEmpty() && date != 0L){

            val uid = Firebase.auth.currentUser?.uid ?: ""

            val note = Notes(
                expense = expense,
                nominal = nominal.toLong(),
                category = category,
                date = date,
                note = note,
                createdAt = System.currentTimeMillis()
            )

            database
                .child("notes")
                .child(uid)
                .push()
                .setValue(note)
                .addOnSuccessListener {
                    Log.d(TAG, "Data berhasil disimpan")
                }
                .addOnFailureListener {
                    Log.e(TAG, "Gagal simpan", it)
                }

            _uiState.update { currentState ->
                currentState.copy(
                    isSuccessfully = true,
                )
            }

        } else if (nominal.isEmpty()){
            _uiState.update { currentState ->
                currentState.copy(
                    isInputNominalEmpty = true,
                )
            }
        } else if (category.isEmpty()){
            _uiState.update { currentState ->
                currentState.copy(
                    isInputCategoryEmpty = true,
                )
            }
        } else if (date == 0L){
            _uiState.update { currentState ->
                currentState.copy(
                    isInputDateEmpty = true,
                )
            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(
                    isInputNominalEmpty = true,
                    isInputCategoryEmpty = true,
                    isInputDateEmpty = true,
                )
            }
        }

    }

    fun loadNotes() {
        val uid = Firebase.auth.currentUser?.uid ?: return

        repository.observeNotes(uid = uid, limit = 10, onResult = {
            notes ->
            _uiState.update {
                it.copy(
                    notes = notes,
                    isLoading = false,
                    error = ""
                )
            }
        },
            onError = { message ->

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = message
                    )
                }
            })
    }
}
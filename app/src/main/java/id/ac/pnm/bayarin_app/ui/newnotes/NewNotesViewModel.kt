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
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import id.ac.pnm.bayarin_app.ContextApplication
import id.ac.pnm.bayarin_app.data.AppDatabase
import id.ac.pnm.bayarin_app.data.model.Notes
import id.ac.pnm.bayarin_app.data.repository.NotesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID

class NewNotesViewModel : ViewModel() {

    private val roomDb = AppDatabase.getDatabase(ContextApplication.instance)

    private val repository = NotesRepository(
        roomDb.notesDao()
    )

    private val _uiState = MutableStateFlow(NewNotesUiState())
    val uiState: StateFlow<NewNotesUiState> = _uiState.asStateFlow()

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

    fun sync() {
        viewModelScope.launch {
            repository.sync()
        }
    }

    fun loadDetailNotes(id : String) {

        viewModelScope.launch {

            _uiState.update {
                it.copy(isLoading = true)
            }

            try {

                val note = repository.getSelectedNotes(id)

                _uiState.update {
                    it.copy(
                        detailNotes = note,
                        isLoading = false,
                        error = ""
                    )
                }

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Terjadi kesalahan"
                    )
                }

            }
        }
    }

    fun loadNotes(limit : Int = 10) {
        val uid = Firebase.auth.currentUser?.uid ?: return

        viewModelScope.launch {

            repository.getAllNotes(limit = limit, uid = uid)
                .collect { notes ->

                    _uiState.update {
                        it.copy(
                            notes = notes,
                            isLoading = false,
                            error = ""
                        )
                    }

                }
        }
    }

    fun addNotes(expense : Boolean, nominal : String, category : String, date : Long, note : String){

        if (!validateInput(
                nominal = nominal,
                category = category,
                date = date
            )
        ) {
            Log.d(TAG, "Data notes tidak valid")
            return
        }

        val uid = Firebase.auth.currentUser?.uid ?: return

        viewModelScope.launch {

            val notes = Notes(
                id = UUID.randomUUID().toString(),
                userId = uid,
                expense = expense,
                nominal = nominal.toLong(),
                category = category,
                date = date,
                note = note,
                createdAt = System.currentTimeMillis(),
                isSynced = 0
            )

            repository.createNotes(notes)

            _uiState.update {
                it.copy(
                    isSuccessfully = true
                )
            }
        }

    }

//    fun loadNotes() {
//        val uid = Firebase.auth.currentUser?.uid ?: return
//
//        repository.observeNotes(uid = uid, limit = 10, onResult = {
//                notes ->
//            _uiState.update {
//                it.copy(
//                    notes = notes,
//                    isLoading = false,
//                    error = ""
//                )
//            }
//        },
//            onError = { message ->
//
//                _uiState.update {
//                    it.copy(
//                        isLoading = false,
//                        error = message
//                    )
//                }
//            })
//    }

//    fun addNotes(expense : Boolean, nominal : String, category : String, date : Long, note : String){
//
//        if (!validateInput(
//                nominal = nominal,
//                category = category,
//                date = date
//            )
//        ) {
//            Log.d(TAG, "Data notes tidak valid")
//            return
//        }
//
//            repository.createNotes(
//                expense = expense,
//                nominal = nominal,
//                category = category,
//                date = date,
//                note = note,
//                onSuccess = {
//                    Log.d(TAG, "Data berhasil disimpan")
//
//                    _uiState.update { currentState ->
//                        currentState.copy(
//                            isSuccessfully = true,
//                        )
//                    }
//                },
//                onError = { exception ->
//
//                    Log.e(TAG, "Gagal simpan", exception)
//
//                    _uiState.update {
//                        it.copy(
//                            isSuccessfully = false
//                        )
//                    }
//                }
//            )
//    }

    private fun validateInput(
        nominal: String,
        category: String,
        date: Long
    ): Boolean {

        val nominalValue = nominal.toLongOrNull()

        val isNominalValid =
            nominalValue != null && nominalValue > 0

        val isCategoryValid =
            category.isNotBlank()

        val isDateValid =
            date != 0L

        _uiState.update {
            it.copy(
                isInputNominalEmpty = !isNominalValid,
                isInputCategoryEmpty = !isCategoryValid,
                isInputDateEmpty = !isDateValid
            )
        }

        return isNominalValid &&
                isCategoryValid &&
                isDateValid
    }
}